package me.redtea.factionrevolutions.core;

import lombok.Getter;
import me.redtea.factionrevolutions.hooks.papi.PlaceholderAPIExpansion;
import me.redtea.factionrevolutions.hooks.vault.EconomyManager;
import me.redtea.factionrevolutions.tools.Config;
import me.redtea.factionrevolutions.tools.Message;
import me.redtea.factionrevolutions.utils.ItemUtil;
import me.redtea.factionrevolutions.utils.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
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

        Message.load(getConfig(), PlaceholderAPIEnabled);
    }

    @Override
    public void onDisable() {
        if(core != null) {
            try {
                core.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
