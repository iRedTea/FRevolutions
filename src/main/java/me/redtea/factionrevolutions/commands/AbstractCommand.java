package me.redtea.factionrevolutions.commands;

import lombok.NonNull;
import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.command.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    public AbstractCommand(String command) {
        PluginCommand pluginCommand = FRevolutions.getInstance().getCommand(command);
        if(pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract void execute(CommandSender sender, String label, String[] args);

    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        execute(sender, label, args);
        for (Method m : this.getClass().getDeclaredMethods())
        {
            if (m.isAnnotationPresent(SubCommand.class))
            {
                SubCommand sub = m.getAnnotation(SubCommand.class);

                boolean isMustBeProcessed = false;

                if(args[0].equalsIgnoreCase(sub.name())) {
                    isMustBeProcessed = true;
                } else {
                    for(String ally : sub.aliases()) {
                        if(args[0].equalsIgnoreCase(ally)) isMustBeProcessed = true;
                    }
                }

                String[] subArgs = {};
                if(args.length > 1) subArgs = Arrays.copyOfRange(args, 1, args.length-1);


                if(isMustBeProcessed) {
                    if(!Objects.equals(sub.permission(), "")) {
                        if(sender.hasPermission(sub.permission())) {
                            try {
                                m.invoke(sender, subArgs);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    try {
                        m.invoke(sender, subArgs);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public
    List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if(list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for(String arg : list) {
            if(arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }
        return result;
    }

}
