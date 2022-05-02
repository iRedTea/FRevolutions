package me.redtea.factionrevolutions.db.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import lombok.val;
import me.redtea.factionrevolutions.db.DataType;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.adapter.JsonAdapter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class DataJson implements IDatabase {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Object.class, new JsonAdapter())
            .setPrettyPrinting()
            .create();

    private final @NonNull Plugin plugin;
    private Map<DataType, Map<String, Object>> dataMap = new HashMap<>();

    public DataJson(@NonNull Plugin plugin) {
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "database.json");

        if(file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                val token = new TypeToken<Map<DataType, Map<String, Object>>>() {
                };

                this.dataMap = GSON.fromJson(fileReader, token.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (DataType value : DataType.values()) {
            this.dataMap.putIfAbsent(value, new HashMap<>());
        }
    }

    @Override
    public Object getData(@NonNull DataType dataType, @NonNull String id) {
        return this.dataMap.get(dataType).get(id);
    }

    @Override
    public void saveData(@NonNull DataType dataType, @NonNull String playerName, @NonNull Object value) {
        this.dataMap.get(dataType).put(playerName, value);
    }

    @Override
    public void saveData() {
        File file = new File(this.plugin.getDataFolder(), "database.json");

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this.dataMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
