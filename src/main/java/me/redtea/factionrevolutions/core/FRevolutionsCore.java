package me.redtea.factionrevolutions.core;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.json.JsonData;
import me.redtea.factionrevolutions.db.impl.sql.SQLDatabase;
import me.redtea.factionrevolutions.db.impl.sql.type.MySQLDatabase;
import me.redtea.factionrevolutions.db.impl.sql.type.SQLiteDatabase;
import me.redtea.factionrevolutions.tools.Config;
import me.redtea.factionrevolutions.types.impl.Revolution;

import java.io.File;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FRevolutionsCore {
    private final FRevolutions plugin;

    private IDatabase db;

    public void init() {
        String d = plugin.getConf().getDatabase().toUpperCase(Locale.ROOT);
        switch (d) {
            case "JSON" -> {
                db = new JsonData(plugin);
                plugin.getLog().sendLogger("Using JSON database.");
            }
            case "MYSQL" -> {
                Config.MySQLSettings settings = plugin.getConf().getMySQLSettings();
                try {
                    db = new MySQLDatabase(settings.getHost(), settings.getPort(), settings.getUser(), settings.getPassword(),
                            settings.getName());
                    plugin.getLog().sendLogger("Using MYSQL database.");
                } catch (SQLException e) {
                    db = new JsonData(plugin);
                    plugin.getConf().setDatabase("JSON");
                    plugin.getLog().sendError("Error connecting to SQLite database! Automatic switching to JSON database.");
                    throw new RuntimeException(e);
                }
            }
            case "SQLITE" -> {
                File file = new File(plugin.getDataFolder(), "database.db");
                try {
                    db = new SQLiteDatabase("jdbc:sqlite:" + file.getAbsolutePath(), "frevolutions");
                    plugin.getLog().sendLogger("Using SQLITE database.");
                } catch (SQLException e) {
                    db = new JsonData(plugin);
                    plugin.getConf().setDatabase("JSON");
                    plugin.getLog().sendError("Error connecting to SQLite database! Automatic switching to JSON database.");
                    throw new RuntimeException(e);
                }
            }
            default -> {
                db = new JsonData(plugin);
                plugin.getConf().setDatabase("JSON");
                plugin.getLog().sendError("Unsupported database type in config.yml!  Automatic switching to JSON database.");
            }
        }
    }

    public void close() throws SQLException {
        db.saveData();
        if(db != null) db.saveData();
        if(db instanceof SQLDatabase) ((SQLDatabase) db).closeConnection();
        plugin.getLog().sendLogger("The connection to the database was closed.");
    }

    public void removeRevolution(String id) {
        db.saveData(Revolution.class, id, null);
    }
    public Revolution getRevolution(UUID uuid) {
        return db.getData(Revolution.class, uuid.toString());
    }

    public void saveRevolution(Revolution value) {
        db.saveData(Revolution.class, value.getId().toString(), value);
    }
}
