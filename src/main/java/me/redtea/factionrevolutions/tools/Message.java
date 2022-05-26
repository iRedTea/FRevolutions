package me.redtea.factionrevolutions.tools;

import com.google.common.collect.Lists;
import com.massivecraft.factions.FactionsPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import me.redtea.factionrevolutions.core.FRevolutions;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Message {
    usage_title, usage_format, reload, noPermissions, usage_noneArgs;


    private List<String> msg;

    private static LangProperties langProperties;

    private static boolean PAPI;

    @SuppressWarnings("unchecked")
    public static void load(FRevolutions plugin, String lang, boolean PAPIEnabled) {
        FileConfiguration c = initLang(plugin, lang);
        try {
            String language = c.getString("langTitle.language");
            String author = c.getString("langTitle.author");
            String version = c.getString("langTitle.version");
            String pluginVersion = c.getString("langTitle.pluginVersion");
            langProperties = new LangProperties(language, author, version, pluginVersion);
            if(langProperties.getLanguage() == null) lang = "en";
        } catch (Throwable e) {
            plugin.getLog().sendWarning("Your lang is not valid! Using English lang namespace!");
            lang = "en";
        }
        for(Message message : Message.values()) {
            message.PAPI = PAPIEnabled;
            boolean needRecover;
            try {
                Object obj = c.get("messages." + message.name().replace("_", "."));
                if(obj instanceof List) {
                    message.msg = (((List<String>) obj)).stream().map(m -> ChatColor.translateAlternateColorCodes('&', m)).collect(Collectors.toList());
                } else {
                    message.msg = Lists.newArrayList(obj == null ? "" : ChatColor.translateAlternateColorCodes('&', obj.toString()));
                }
                needRecover = message.msg == null || message.msg.equals("") || obj.equals(null);
            } catch (NullPointerException e) {
                needRecover = true;
            }
            if(needRecover) recover(plugin, lang);
        }
    }

    public static void recover(FRevolutions plugin, String lang) {
        File langFile = new File(plugin.getDataFolder() + File.separator +"lang" + File.separator + lang + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(langFile);
        for(Message message : Message.values()) {
            boolean recover = false;
            String path = "messages." + message.name().replace("_", ".");
            try {
                Object obj = c.get(path);
                if(obj == null) recover = true;
            } catch (Throwable e) {
                recover = true;
            }
            if(recover) {
                Object value;
                File tempFile = new File("cache_file");
                try {
                    tempFile.createNewFile();
                } catch (Throwable e) {
                    e.printStackTrace();
                } try {
                    InputStream ddlStream = FRevolutions.class.getClassLoader().getResourceAsStream("lang" + File.separator + lang + ".yml");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buf = new byte[2048];
                        int r;
                        if (ddlStream != null) {
                            while(-1 != (r = ddlStream.read(buf))) {
                                fos.write(buf, 0, r);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FileConfiguration defaultMsgFile = YamlConfiguration.loadConfiguration(tempFile);
                    value = defaultMsgFile.get(path);
                } catch (Throwable e) {
                    value = 0;
                } finally {
                    tempFile.delete();
                }
                c.set(path, value);
            }
        } try {
            c.save(langFile);
            Message.load(plugin, lang, PAPI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileConfiguration initLang(FRevolutions plugin, String lang) {
        File folder = new File(plugin.getDataFolder() + File.separator + "lang");
        if(!folder.exists()) {
            try {
                folder.createNewFile();
                folder.mkdirs();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File langFile = new File(plugin.getDataFolder() + File.separator + "lang"+ File.separator +
                lang + ".yml");
        if(!langFile.exists()) {
            try {
                langFile.createNewFile();
                InputStream ddlStream = FactionsPlugin.class.getClassLoader().getResourceAsStream("lang" + File.separator + lang +".yml");
                try (FileOutputStream fos = new FileOutputStream(langFile)) {
                    byte[] buf = new byte[2048];
                    int r;
                    while(-1 != (r = ddlStream.read(buf))) {
                        fos.write(buf, 0, r);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(langFile);
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
        ArrayList<String> result = new ArrayList<>();
        for(String m : msg) {
            result.add(ChatColor.translateAlternateColorCodes('&',m));
        }
        return result;
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
                list.add(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(s)));
            }
            return list;
        }

    }

    public static LangProperties getLangProperties() {
        return langProperties;
    }

    @AllArgsConstructor
    @Getter
    @NonNull
    public static class LangProperties {
        private final String language;

        private final String author;

        private final String version;

        private final String pluginVersion;
    }
}