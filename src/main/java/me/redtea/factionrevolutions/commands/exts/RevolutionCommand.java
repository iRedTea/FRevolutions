package me.redtea.factionrevolutions.commands.exts;

import me.redtea.factionrevolutions.commands.AbstractCommand;
import me.redtea.factionrevolutions.core.FRevolutions;

public class RevolutionCommand extends AbstractCommand {
    private final FRevolutions plugin;

    public RevolutionCommand(FRevolutions plugin) {
        super("revolution");
        this.plugin = plugin;
    }


}
