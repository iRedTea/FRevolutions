package me.redtea.factionrevolutions.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;

@Getter
public final class FRevolutions extends JavaPlugin {

    @Getter
    private static FRevolutions instance;

    @Inject
    private FRevolutionsCommand fRevolutionsCommand;

    @Inject
    private RevolutionCommand revolutionCommand;

    @Inject
    private RPreventionCommand rPreventionCommand;

    @Inject
    private Config conf;

    @Inject
    private LoggerUtil log;

    @Inject
    private FRevolutionsCore core;

    @Inject
    private ItemUtil itemUtil;

    @Inject
    private EconomyManager eco;

    private boolean PlaceholderAPIEnabled = false;

    @Override
    public void onEnable() {
        FRevolutionsBinderModule module = new FRevolutionsBinderModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);

        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().warning("Could not found Factions plugin on this server. Disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();

        conf.init();

        eco.init();

        core.init();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIExpansion().register(); PlaceholderAPIEnabled=true; }

        Message.load(this, conf.getLang(), PlaceholderAPIEnabled);

        System.out.println(ChatColor.GREEN + "############################################################");
        System.out.println(ChatColor.GREEN + "# +------------------------------------------------------+ #");
        System.out.println(ChatColor.GREEN + "# |                      FRevolutions                    | #");
        System.out.println(ChatColor.GREEN + "# +------------------------------------------------------+ #");
        System.out.println(ChatColor.GREEN + "############################################################");
        System.out.println(ChatColor.GREEN + "Plugin version: " + ChatColor.YELLOW + " " + getDescription().getVersion());
        System.out.println(ChatColor.GREEN + "Bukkit version: " + ChatColor.YELLOW + " " + Bukkit.getBukkitVersion());
        System.out.println(ChatColor.GREEN + "Using language: " + ChatColor.YELLOW + " " + Message.getLangProperties().
                getLanguage());
        System.out.println(ChatColor.GREEN + "Localization version" + ChatColor.YELLOW + " " + Message.getLangProperties().
                getVersion() + ChatColor.GREEN + " by " + ChatColor.YELLOW + Message.getLangProperties().getAuthor());
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
        getServer().getServicesManager().unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
    }

    public void reload() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.disablePlugin(this);
        pluginManager.enablePlugin(this);
    }
}
