package me.redtea.factionrevolutions.commands.exts;

import com.google.inject.Inject;
import me.redtea.factionrevolutions.commands.AbstractCommand;
import me.redtea.factionrevolutions.core.FRevolutions;

public class RPreventionCommand extends AbstractCommand {
    private final FRevolutions plugin;

    @Inject
    public RPreventionCommand(FRevolutions plugin) {
        super("rprevention");
        this.plugin = plugin;
    }
}
