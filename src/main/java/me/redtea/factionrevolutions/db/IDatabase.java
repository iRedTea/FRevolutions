package me.redtea.factionrevolutions.db;

import lombok.NonNull;
import me.redtea.factionrevolutions.types.RPlayer;
import me.redtea.factionrevolutions.types.Revolution;

public interface IDatabase {
    Object getData(@NonNull DataType dataType, @NonNull String id);

    void saveData(@NonNull DataType dataType, @NonNull String id, Object value);

    void saveData();
}
