package me.redtea.factionrevolutions.types.impl;

import com.massivecraft.factions.FPlayers;
import lombok.*;
import me.redtea.factionrevolutions.types.*;
import me.redtea.factionrevolutions.types.Data;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Revolution implements Data {

    private final UUID id;
    private String leader;
    private HashMap<String, Role> roles;
    private ArrayList<String> members;
    private Phase phase;
    private int points;
    private double balance;
    private ArrayList<String> sponsoringFactions;

    public double getSummaryPower() {
        double result = 0.0d;
        for(String name : members) {
            result += FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(name)).getPower();
        }
        return result;
    }

    public double getMaxPower() {
        double result = 0.0d;
        for(String name : members) {
            result += FPlayers.getInstance().getByOfflinePlayer(Bukkit.getOfflinePlayer(name)).getPowerMax();
        }
        return result;
    }

}
