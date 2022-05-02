package me.redtea.factionrevolutions.tools;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Message {
    usage, reload, noPermissions;


    private List<String> msg;

    private boolean PAPI;

    @SuppressWarnings("unchecked")
    public static void load(FileConfiguration c, boolean PAPIEnabled) {
        for(Message message : Message.values()) {
            message.PAPI = PAPIEnabled;
            Object obj = c.get("messages." + message.name().replace("_", "."));
            if(obj instanceof List) {
                message.msg = (((List<String>) obj)).stream().map(m -> ChatColor.translateAlternateColorCodes('&', m)).collect(Collectors.toList());
            }
            else {
                message.msg = Lists.newArrayList(obj == null ? "" : ChatColor.translateAlternateColorCodes('&', obj.toString()));
            }
        }
    }

    public Sender replace(String from, String to) {
        Sender sender = new Sender();
        return sender.replace(from, to);
    }

    public void send(CommandSender player) {
        new Sender().send(player);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(String s : Message.this.msg) {
            str.append(" ").append(s);
        }
        return ChatColor.translateAlternateColorCodes('&', str.toString());
    }

    public List<String> toList() {
        ArrayList<String> list = new ArrayList<>(Message.this.msg);
        return list;
    }

    public class Sender {
        private final Map<String, String> placeholders = new HashMap<>();

        public void send(CommandSender player) {
            if(PAPI && player instanceof Player) {
                for(String message : Message.this.msg) {
                    Player p = ((Player) player).getPlayer();
                    sendMessage(player, PlaceholderAPI.setPlaceholders(p, replacePlaceholders(message)));
                }
            } else {
                for(String message : Message.this.msg) {
                    sendMessage(player, replacePlaceholders(message));
                }
            }

        }

        public Sender replace(String from, String to) {
            placeholders.put(from, to);
            return this;
        }

        private void sendMessage(CommandSender player, String message) {
            if(message.startsWith("json:")) {
                net.md_5.bungee.api.CommandSender sender = (net.md_5.bungee.api.CommandSender) player;
                sender.sendMessage(new TextComponent(ComponentSerializer.parse(message.substring(5))));
            } else {
                player.sendMessage(message);
            }
        }

        private String replacePlaceholders(String message) {
            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
            return message;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            for(String s : Message.this.msg) {
                str.append(" ").append(replacePlaceholders(s));
            }
            return ChatColor.translateAlternateColorCodes('&', str.toString());
        }

        public List<String> toList() {
            ArrayList<String> list = new ArrayList<>();
            for(String s : Message.this.msg) {
                list.add(replacePlaceholders(s));
            }
            return list;
        }
    }

}