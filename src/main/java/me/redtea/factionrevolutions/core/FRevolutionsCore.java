package me.redtea.factionrevolutions.core;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.json.JsonData;
import me.redtea.factionrevolutions.types.impl.Revolution;

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
            case "MYSQL" ->
                //db = new DataMySQL(plugin);
                plugin.getLog().sendLogger("Using MYSQL database.");
            default -> {
                db = new JsonData(plugin);
                plugin.getConf().setDatabase("JSON");
                plugin.getLog().sendError("Unsupported database type in config.yml! Using JSON database.");
            }
        }
    }

    public void close() throws SQLException {
        db.saveData();
        if(db != null) db.saveData();
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
