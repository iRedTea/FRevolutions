package me.redtea.factionrevolutions.core;

import me.redtea.factionrevolutions.db.DataType;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.DataJson;
import me.redtea.factionrevolutions.db.impl.DataSQL;

import java.sql.SQLException;
import java.util.Locale;


public class FRevolutionsCore {
    private final FRevolutions plugin;

    private IDatabase db;

    public FRevolutionsCore(FRevolutions plugin) throws SQLException {
        this.plugin = plugin;
        load();
    }

    public void load() {
        String d = plugin.getConf().getDatabase().toUpperCase(Locale.ROOT);
        switch (d) {
            case "JSON":
                db = new DataJson(plugin);
                plugin.getLog().sendLogger("Using JSON database.");
                break;
            case "MYSQL":
                db = new DataSQL(plugin);
                plugin.getLog().sendLogger("Using MYSQL database.");
            default: db = new DataJson(plugin);
        }
    }

    public void close() throws SQLException {
        db.saveData();
        if(db != null) db.saveData();
        plugin.getLog().sendLogger("The connection to the database was closed.");
    }

    public void removeRevolution(String id) {
        db.saveRevolution(id, null);
    }
}
