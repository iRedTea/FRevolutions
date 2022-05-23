package me.redtea.factionrevolutions.api;

import lombok.Getter;
import me.redtea.factionrevolutions.core.FRevolutions;
import me.redtea.factionrevolutions.core.FRevolutionsCore;
import me.redtea.factionrevolutions.types.impl.Revolution;

import java.util.UUID;

public class Revolutions {
    private final FRevolutionsCore core;

    @Getter
    private static Revolutions instance;

    public Revolutions(FRevolutions plugin) {
        this.core = plugin.getCore();
        instance = this;
    }

    public Revolution getRevolutionByUUID(UUID uuid) {
        return null;
    }




}
