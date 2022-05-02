package me.redtea.factionrevolutions.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.entity.Player;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    public PlaceholderAPIExpansion() {}

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "redtea";
    }

    @Override
    public String getIdentifier() {
        return ("test");
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        // %test_playername% (возвращает имя игрока)
        if (identifier.equals("playername")) {
            return p.getName();
        }
        // %test_placeholder2% (возвращает строку "placeholder2 works")
        if (identifier.equals("placeholder2")) {
            return "placeholder2 works";
        }

        return null;
    }
}