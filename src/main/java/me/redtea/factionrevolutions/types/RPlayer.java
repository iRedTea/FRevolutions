package me.redtea.factionrevolutions.types;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RPlayer {
    private final String uuid;

    private boolean inRevolution;

    private String revolution;

    public RPlayer(@NonNull String uuid, @NonNull boolean inRevolution, String revolution) {
        this.uuid = uuid;
        this.inRevolution = inRevolution;
        this.revolution = revolution;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isInRevolution() {
        return inRevolution;
    }

    public void setInRevolution(boolean inRevolution) {
        this.inRevolution = inRevolution;
    }

    public String getRevolution() {
        return revolution;
    }

    public void setRevolution(String revolution) {
        this.revolution = revolution;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public FPlayer getFPlayer() {
        return FPlayers.getInstance().getById(uuid);
    }

    public Faction getFaction() {
        return getFPlayer().getFaction();
    }

    public boolean hasFaction() {
        return getFPlayer().hasFaction();
    }
}
