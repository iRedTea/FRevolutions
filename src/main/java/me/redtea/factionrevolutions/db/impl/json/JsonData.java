package me.redtea.factionrevolutions.db.impl.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import lombok.val;
import me.redtea.factionrevolutions.db.DataType;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.json.adapter.*;
import me.redtea.factionrevolutions.types.*;
import me.redtea.factionrevolutions.types.impl.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class JsonData implements IDatabase {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Revolution.class, new RevolutionSerializer())
            .registerTypeAdapter(RPlayer.class, new RPlayerSerializer())
            .setPrettyPrinting()
            .create();

    private final @NonNull Plugin plugin;

    private Map<Class<?>, Map<String, Data>> dataMap = new HashMap<>();

    public JsonData(@NonNull Plugin plugin) {
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "database.json");

        if(file.exists()) {
            try (FileReader fileReader = new FileReader(file)) {
                val token = new TypeToken<Map<DataType, Map<String, Data>>>() {};

                this.dataMap = GSON.fromJson(fileReader, token.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Data> V getData(@NonNull Class<V> clazz, @NonNull String id) {
        this.dataMap.putIfAbsent(clazz, new HashMap<>());
        return (V) this.dataMap.get(clazz).get(id);
    }

    @Override
    public <V extends Data> void saveData(@NonNull Class<V> clazz, @NonNull String id, V value) {
        this.dataMap.putIfAbsent(clazz, new HashMap<>());
        this.dataMap.get(clazz).put(id, value);
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
