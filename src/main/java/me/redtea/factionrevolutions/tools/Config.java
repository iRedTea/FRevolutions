package me.redtea.factionrevolutions.tools;


import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final FRevolutions pl;
    private FileConfiguration config;

    private boolean debug;
    private boolean saveLogs;
    private String database;
    private String hello;
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

        debug = config.getBoolean("settings.debug");

        saveLogs = config.getBoolean("settings.saveLogs");
        database = config.getString("settings.database");
        hello = config.getString("hello");
        mysqlsettings = new MySQLSettings(
                config.getString("settings.mysql.host"),
                config.getInt("settings.mysql.port"),
                config.getString("settings.mysql.name"),
                config.getString("settings.mysql.user"),
                config.getString("settings.mysql.password")
        );
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

    public String getHello() {
        return hello;
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public MySQLSettings getMySQLSettings() {
        return mysqlsettings;
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
