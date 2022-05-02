package me.redtea.factionrevolutions.hooks.vault;

import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconomyManager {
    private Economy e;

    private FRevolutions plugin;

    private boolean enabled = true;

    public EconomyManager(FRevolutions plugin) {
        this.plugin = plugin;
        init();
    }

    public void init() {
        RegisteredServiceProvider<Economy> reg = Bukkit.getServicesManager().getRegistration(Economy.class);
        if(reg != null) e = reg.getProvider();
        if(e == null) {
            plugin.getLog().sendWarning("Vault is not installed! Economy is Disabled.");
            enabled = false;
        }
    }
    public boolean takeMoney(Player p, double a) {
        if(enabled) {
            if (e.getBalance(p) < a) return false;
            return e.withdrawPlayer(p, a).transactionSuccess();
        } else return false;
    }

    public double getMoney(Player p, double a) {
        if(enabled) return e.getBalance(p);
        return -1;
    }

    public boolean setMoney(Player p, double a) {
        boolean b = true;
        if(enabled) {
            if(!e.withdrawPlayer(p, e.getBalance(p)).transactionSuccess()) b = false;
            if(!e.depositPlayer(p, a).transactionSuccess()) b = false;
        }
        return b;
    }

}
