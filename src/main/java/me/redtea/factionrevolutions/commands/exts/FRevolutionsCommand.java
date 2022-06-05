package me.redtea.factionrevolutions.commands.exts;

import com.google.inject.Inject;
import me.redtea.factionrevolutions.commands.AbstractCommand;
import me.redtea.factionrevolutions.commands.SubCommand;
import me.redtea.factionrevolutions.core.FRevolutions;
import me.redtea.factionrevolutions.tools.Message;
import org.bukkit.command.CommandSender;

public class FRevolutionsCommand extends AbstractCommand {
    private final FRevolutions plugin;

    @Inject
    public FRevolutionsCommand(FRevolutions plugin) {
        super("frevolutions");
        this.plugin =  plugin;
    }

    @SubCommand(name = "reload", permission = "frevolutions.admin.reload")
    public void reload(CommandSender sender, String[] args) {
        long timeInitStart = System.currentTimeMillis();
        plugin.reload();
        long timeReload = (System.currentTimeMillis() - timeInitStart);
        Message.reload.replace("%time%", String.valueOf(timeReload)).send(sender);
    }

    @SubCommand(name = "list", permission = "frevolutions.admin.list")
    public void list(CommandSender sender, String[] args) {

    }

}
