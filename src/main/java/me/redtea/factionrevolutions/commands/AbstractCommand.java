package me.redtea.factionrevolutions.commands;

import lombok.NonNull;
import me.redtea.factionrevolutions.core.FRevolutions;
import me.redtea.factionrevolutions.tools.Message;
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
        //
    }

    public List<String> complete(CommandSender sender) {
        ArrayList<String> listOfSubCommands = new ArrayList<>();
        for (Method m : this.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(SubCommand.class)) {
                SubCommand sub = m.getAnnotation(SubCommand.class);
                if(sender.hasPermission(sub.permission()) || sub.permission().equals("")) {
                    listOfSubCommands.add(sub.name());
                    listOfSubCommands.addAll(Arrays.asList(sub.aliases()));
                }
            }
        }
        listOfSubCommands.add("help");
        return listOfSubCommands;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            ArrayList<String> messages = new ArrayList<>(Message.usage_title.replace("%command%", command.getName()).toList());
            for (Method m : this.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(SubCommand.class)) {
                    SubCommand sub = m.getAnnotation(SubCommand.class);
                    messages.addAll(Message.usage_format.replace("%command%", command.getName()).replace("%alias%", sub.name()).
                            replace("%description%", sub.description()).toList());
                }
            }
            for(String message : messages) {
                sender.sendMessage(message);
            }
        } else for (Method m : this.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(SubCommand.class))
            {
                SubCommand sub = m.getAnnotation(SubCommand.class);

                boolean isMustBeProcessed = false;

                if(args[0].equalsIgnoreCase(sub.name())) {
                    isMustBeProcessed = true;
                } else {
                    for(String alias : sub.aliases()) {
                        if (args[0].equalsIgnoreCase(alias)) {
                            isMustBeProcessed = true;
                            break;
                        }
                    }
                }

                String[] subArgs = {};
                if(args.length > 1) subArgs = Arrays.copyOfRange(args, 1, args.length-1);


                if(isMustBeProcessed) {
                    if(!Objects.equals(sub.permission(), "")) {
                        if(sender.hasPermission(sub.permission())) {
                            try {
                                m.invoke(this, sender, subArgs);
                                break;
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        } else Message.noPermissions.send(sender);
                    }
                    try {
                        m.invoke(this, sender, subArgs);
                        break;
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
    List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        return filter(complete(sender), args);
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
