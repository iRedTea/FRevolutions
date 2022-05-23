package me.redtea.factionrevolutions.tools;


import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Config {
    private final FRevolutions pl;
    private FileConfiguration config;

    private boolean debug;
    private boolean saveLogs;
    private String database;
    private MySQLSettings mysqlsettings;

    public Config(FRevolutions pl, FileConfiguration config) {
        this.pl = pl;
        this.config = config;
        reload();
    }

    public void reload() {
        pl.saveDefaultConfig();
        pl.reloadConfig();
        config = pl.getConfig();

        try {
            debug = config.getBoolean("settings.debug");
            saveLogs = config.getBoolean("settings.saveLogs");
            database = config.getString("settings.database");
            mysqlsettings = new MySQLSettings(
                    config.getString("settings.mysql.host"),
                    config.getInt("settings.mysql.port"),
                    config.getString("settings.mysql.name"),
                    config.getString("settings.mysql.user"),
                    config.getString("settings.mysql.password")
            );
        } catch (Exception e) {
            pl.getLogger().severe("Could not load config.yml! Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }


        for(Field f : this.getClass().getFields()) {
            try {
                if(f.get(this.getClass()).equals(null)) {
                    switch (f.getName()) {
                        case "debug" -> debug = false;
                        case "saveLogs" -> saveLogs = true;
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

    public boolean getDebug() {
        return debug;
    }


    public boolean isSaveLogs() {
        return saveLogs;
    }

    public String getDatabase() {
        return database;
    }

    public MySQLSettings getMySQLSettings() {
        return mysqlsettings;
    }

    public void saveConfig() {
        try {
            pl.getConfig().set("settings.debug", debug);
            pl.getConfig().set("settings.saveLogs", saveLogs);
            pl.getConfig().set("settings.database", database);
            pl.getConfig().set("settings.mysql.host", mysqlsettings.getHost());
            pl.getConfig().set("settings.mysql.port", mysqlsettings.getPort());
            pl.getConfig().set("settings.mysql.name", mysqlsettings.getName());
            pl.getConfig().set("settings.mysql.user", mysqlsettings.getUser());
            pl.getConfig().set("settings.mysql.password", mysqlsettings.getPassword());
            pl.saveConfig();
        } catch (Exception e) {
            pl.getLogger().severe("Could not save config.yml! Error: " + e.getLocalizedMessage());
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
}
