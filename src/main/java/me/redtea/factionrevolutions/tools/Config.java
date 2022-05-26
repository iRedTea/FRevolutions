package me.redtea.factionrevolutions.tools;


import lombok.Getter;
import lombok.Setter;
import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class Config {
    private final FRevolutions plugin;

    private FileConfiguration config;

    @CanRecover
    private boolean debug;

    @CanRecover
    private boolean saveLogs;

    @CanRecover
    private String database;

    @CanRecover
    private MySQLSettings mysqlsettings;

    @CanRecover
    private String lang;

    public Config(FRevolutions plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        try {
            debug = config.getBoolean("settings.debug");
            saveLogs = config.getBoolean("settings.saveLogs");
            lang = config.getString("settings.lang");
            database = config.getString("settings.database");
            mysqlsettings = new MySQLSettings(
                    config.getString("settings.mysql.host"),
                    config.getInt("settings.mysql.port"),
                    config.getString("settings.mysql.name"),
                    config.getString("settings.mysql.user"),
                    config.getString("settings.mysql.password")
            );
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load config.yml! Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }


        for(Field f : this.getClass().getFields()) {
            if(f.isAnnotationPresent(CanRecover.class)) {
                try {
                    if(f.get(this.getClass()).equals(null)) {
                        switch (f.getName()) {
                            case "debug" -> debug = false;
                            case "saveLogs" -> saveLogs = true;
                            case "lang" -> lang = "en";
                            case "database" -> database = "JSON";
                            case "mysqlsettings" -> mysqlsettings = new MySQLSettings(
                                    "localhost",
                                    3306,
                                    "name",
                                    "user",
                                    "password");
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        saveConfig();
    }

    public void saveConfig() {
        try {
            plugin.getConfig().set("settings.debug", debug);
            plugin.getConfig().set("settings.saveLogs", saveLogs);
            plugin.getConfig().set("settings.database", database);
            plugin.getConfig().set("settings.mysql.host", mysqlsettings.getHost());
            plugin.getConfig().set("settings.mysql.port", mysqlsettings.getPort());
            plugin.getConfig().set("settings.mysql.name", mysqlsettings.getName());
            plugin.getConfig().set("settings.mysql.user", mysqlsettings.getUser());
            plugin.getConfig().set("settings.mysql.password", mysqlsettings.getPassword());
            plugin.saveConfig();
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save config.yml! Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public class MySQLSettings {
        private final String host;
        private final int port;
        private final String name;
        private final String user;
        private final String password;

        MySQLSettings(String host, int port, String name, String user, String password) {
            this.host = host;
            this.port = port;
            this.name = name;
            this.user = user;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getName() {
            return name;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public MySQLSettings getMySQLSettings() {
        return mysqlsettings;
    }

    public boolean isSaveLogs() {
        return saveLogs;
    }

    public String getLang() {
        return lang;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private @interface CanRecover {}
}
