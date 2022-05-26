package me.redtea.factionrevolutions.db.impl.sql.type;
import lombok.*;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.sql.ResponseHandler;
import me.redtea.factionrevolutions.db.impl.sql.SQLDatabase;
import me.redtea.factionrevolutions.db.impl.sql.SQLStatement;
import me.redtea.factionrevolutions.types.Data;
import org.sqlite.*;

import java.io.*;
import java.sql.*;


public class SQLiteDatabase implements SQLDatabase, IDatabase {

    private final SQLiteDataSource dataSource = new SQLiteDataSource();
    private Connection connection;

    @Builder(buildMethodName = "create")
    public SQLiteDatabase(File databaseFile, String database) throws SQLException {
        this("jdbc:sqlite:" + databaseFile, database);
        load();
    }

    public SQLiteDatabase(String url, String database) throws SQLException {
        this.dataSource.setDatabaseName(database);
        this.dataSource.setUrl(url);
        this.refreshConnection();
        load();
    }

    private void load() {
        execute(true,
                "CREATE TABLE IF NOT EXISTS `revolution` (" +
                        "`id` varchar(255) PRIMARY KEY," +
                        "`leader` varchar(255) NOT NULL," +
                        "`roles` varchar(255) NOT NULL," +
                        "`members` varchar(255) NOT NULL," +
                        "`phase` varchar(255) NOT NULL," +
                        "`points` INTEGER NOT NULL," +
                        "`balance` DOUBLE NOT NULL," +
                        "`sponsoringFactions` varchar(255) NOT NULL)"
        );
        execute(true,
                "CREATE TABLE IF NOT EXISTS `rplayer` (" +
                        "`uuid` varchar(255) PRIMARY KEY," +
                        "`inRevolution` INTEGER NOT NULL," +
                        "`revolution` varchar(255) NOT NULL)"
        );
    }

    @Override
    public void execute(boolean async, String sql, Object... objects) {

        Runnable runnable = () -> {
            try (SQLStatement statement = new SQLStatement(getConnection(), sql, objects)) {
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if(async) {
            THREAD_POOL.submit(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public ResultSet query(String sql, Object... objects) {
        try {
            SQLStatement statement = new SQLStatement(getConnection(), sql, objects);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("[SqliteDatabase] Error executing query to database", e);
        }
    }

    @Override
    public void query(boolean async, String sql, ResponseHandler<ResultSet, SQLException> handler, Object... objects) {

        Runnable runnable = () -> {
            try (SQLStatement statement = new SQLStatement(getConnection(), sql, objects)) {
                handler.handle(statement.executeQuery());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if(async) {
            THREAD_POOL.submit(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return this.refreshConnection();
        } catch (SQLException e) {
            throw new RuntimeException("[SqliteDatabase] Error connecting to database", e);
        }
    }

    private Connection refreshConnection() throws SQLException {
        if(this.connection == null || this.connection.isClosed() || !this.connection.isValid(1000)) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    }

    @Override
    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    @Override
    public <V extends Data> V getData(@NonNull Class<V> clazz, @NonNull String id) {
        return null;
    }

    @Override
    public <V extends Data> void saveData(@NonNull Class<V> clazz, @NonNull String id, V value) {

    }

    @Override
    public void saveData() {

    }
}