package me.redtea.factionrevolutions.db;

import lombok.NonNull;
import me.redtea.factionrevolutions.types.*;

public interface IDatabase {

    <V extends Data> V getData(@NonNull Class<V> clazz, @NonNull String id);
    <V extends Data> void saveData(@NonNull Class<V> clazz, @NonNull String id, V value);

    void saveData();
}
