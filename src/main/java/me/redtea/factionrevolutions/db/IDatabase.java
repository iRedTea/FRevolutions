package me.redtea.factionrevolutions.db;

import lombok.NonNull;
import me.redtea.factionrevolutions.types.RPlayer;
import me.redtea.factionrevolutions.types.Revolution;

public interface IDatabase {
    String getRevolution(@NonNull String id);

    void saveRevolution(@NonNull String id, Revolution value);

    String getRPlayer(@NonNull String id);

    void saveRPlayer(@NonNull String id, RPlayer value);
    void saveData();
}
