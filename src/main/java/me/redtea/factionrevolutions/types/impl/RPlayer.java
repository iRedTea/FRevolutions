package me.redtea.factionrevolutions.types.impl;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import lombok.*;
import me.redtea.factionrevolutions.types.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class RPlayer implements Data {

    private final UUID uuid;
    private boolean inRevolution;
    private String revolution;

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(UUID.fromString(uuid.toString()));
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getById(uuid.toString());
    }

    public Faction getFaction() {
        return getFPlayer().getFaction();
    }

    public boolean hasFaction() {
        return getFPlayer().hasFaction();
    }
}
