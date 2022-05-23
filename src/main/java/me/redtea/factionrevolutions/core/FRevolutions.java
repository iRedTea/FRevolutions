package me.redtea.factionrevolutions.core;

import com.massivecraft.factions.FactionsPlugin;
import lombok.Getter;
import me.redtea.factionrevolutions.commands.exts.FRevolutionsCommand;
import me.redtea.factionrevolutions.commands.exts.RPreventionCommand;
import me.redtea.factionrevolutions.commands.exts.RevolutionCommand;
import me.redtea.factionrevolutions.hooks.papi.PlaceholderAPIExpansion;
import me.redtea.factionrevolutions.hooks.vault.EconomyManager;
import me.redtea.factionrevolutions.tools.Config;
import me.redtea.factionrevolutions.tools.Message;
import me.redtea.factionrevolutions.utils.ItemUtil;
import me.redtea.factionrevolutions.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@Getter
public final class FRevolutions extends JavaPlugin {

    @Getter
    private static FRevolutions instance;

    private Config conf;

    private LoggerUtil log;

    private FRevolutionsCore core;

    private ItemUtil itemUtil;

    private EconomyManager eco;

    private boolean PlaceholderAPIEnabled = false;

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().warning("Could not found Factions plugin on this server. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        conf = new Config(this, getConfig());

        log = new LoggerUtil(this, conf);

        itemUtil = new ItemUtil(this);

        eco = new EconomyManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion().register(); PlaceholderAPIEnabled=true; }

        try {
            core = new FRevolutionsCore(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        File messages = new File(getDataFolder() + File.separator + "messages.yml");
        if(!messages.exists()) {
            try {
                messages.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        InputStream ddlStream = FactionsPlugin.class.getClassLoader().getResourceAsStream("messages.yml");
        try (FileOutputStream fos = new FileOutputStream(messages)) {
            byte[] buf = new byte[2048];
            int r;
            while(-1 != (r = ddlStream.read(buf))) {
                fos.write(buf, 0, r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message.load(YamlConfiguration.loadConfiguration(messages), PlaceholderAPIEnabled);

        new FRevolutionsCommand(this);
        new RevolutionCommand(this);
        new RPreventionCommand(this);

        System.out.println(ChatColor.GREEN + "############################################################");
        System.out.println(ChatColor.GREEN + "# +------------------------------------------------------+ #");
        System.out.println(ChatColor.GREEN + "# |                      FRevolutions                    | #");
        System.out.println(ChatColor.GREEN + "# +------------------------------------------------------+ #");
        System.out.println(ChatColor.GREEN + "############################################################");
        System.out.println(ChatColor.GREEN + "Plugin version: " + ChatColor.YELLOW + " " + getDescription().getVersion());
        System.out.println(ChatColor.GREEN + "Bukkit version: " + ChatColor.YELLOW + " " + Bukkit.getBukkitVersion());
        log.sendLogger("Plugin successfully enabled!");
    }

    @Override
    public void onDisable() {
        conf.saveConfig();
        if(core != null) {
            try {
                core.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        log.sendLogger("Plugin disabled!");
    }
}
