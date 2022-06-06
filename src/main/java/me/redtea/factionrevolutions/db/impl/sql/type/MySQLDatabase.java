package me.redtea.factionrevolutions.db.impl.sql.type;
import com.mysql.jdbc.jdbc2.optional.*;
import lombok.*;
import me.redtea.factionrevolutions.db.IDatabase;
import me.redtea.factionrevolutions.db.impl.sql.ResponseHandler;
import me.redtea.factionrevolutions.db.impl.sql.SQLDatabase;
import me.redtea.factionrevolutions.db.impl.sql.SQLStatement;
import me.redtea.factionrevolutions.types.Data;
import me.redtea.factionrevolutions.types.Phase;
import me.redtea.factionrevolutions.types.Role;
import me.redtea.factionrevolutions.types.impl.RPlayer;
import me.redtea.factionrevolutions.types.impl.Revolution;;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class MySQLDatabase extends SQLDatabase implements IDatabase {

    private final MysqlDataSource dataSource = new MysqlDataSource();
    private Connection connection;

    @Builder(buildMethodName = "create")
    public MySQLDatabase(String host, int port, String user, String password, String database) throws SQLException {
        this.dataSource.setServerName(host);
        this.dataSource.setPort(port);
        this.dataSource.setUser(user);
        this.dataSource.setPassword(password);
        this.dataSource.setDatabaseName(database);
        this.dataSource.setEncoding("UTF-8");
        this.refreshConnection();
        load();
    }

    public void load() {
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
            } catch (SQLException e) {
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
            throw new RuntimeException("[MySQLDatabase] Error executing query to database", e);
        }
    }


    @Override
    public void query(boolean async, String sql, ResponseHandler<ResultSet, SQLException> handler, Object...objects) {
        Runnable runnable = () -> {
            try (SQLStatement statement = new SQLStatement(getConnection(), sql, objects)) {
                handler.handle(statement.executeQuery());
            } catch (SQLException e) {
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
            throw new RuntimeException("[MysqlDatabase] Error connecting to database", e);
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
        if(clazz.equals(RPlayer.class)) {
            try {
                ResultSet r = getPlayerDataFromDB(id);
                return (V) new RPlayer(
                        UUID.fromString(r.getString("uuid")),
                        r.getBoolean("inRevolution"),
                        r.getString("revolution")
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (clazz.equals(Revolution.class)) {
            try {
                ResultSet r = getRevolutionDataFromDB(id);
                return (V) new Revolution(
                        UUID.fromString(r.getString("id")),
                        r.getString("leader"),
                        (HashMap<String, Role>) adapter.deserialize(r.getString("roles")),
                        adapter.stringToList(r.getString("members")),
                        Phase.valueOf(r.getString("phase")),
                        r.getInt("points"),
                        r.getDouble("balance"),
                        adapter.stringToList(r.getString("sponsoringFactions"))
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Data with id " + id + " not exists!");
    }

    @Override
    public <V extends Data> void saveData(@NonNull Class<V> clazz, @NonNull String id, V value) {
        if(value instanceof RPlayer) {
            RPlayer player = (RPlayer) value;
            execute(true, "INSERT INTO rplayer (uuid,inRevolution,revolution) VALUES (?,?,?) ON DUPLICATE KEY " +
                            "UPDATE inRevolution = ?, revolution = ?;",
                    id,
                    player.isInRevolution(),
                    player.getRevolution(),
                    player.isInRevolution(),
                    player.getRevolution()
            );
        } else if (value instanceof Revolution) {
            Revolution revolution = (Revolution) value;
            try {
                execute(true, "INSERT INTO revolution (id,leader,roles,members,phase,points,balance,sponsoringFactions) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY " +
                                "UPDATE leader = ?, roles = ?, members = ?, phase = ?, points = ?, balance = ?, sponsoringFactions = ?;",
                        id,
                        revolution.getLeader(),
                        adapter.serialize(revolution.getRoles()),
                        adapter.listToString(revolution.getMembers()),
                        revolution.getPhase().name(),
                        revolution.getPoints(),
                        revolution.getBalance(),
                        adapter.listToString(revolution.getSponsoringFactions())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ResultSet getPlayerDataFromDB(String uuid) throws SQLException {
        return query("SELECT * FROM rplayer WHERE uuid = ?", uuid);
    }

    public ResultSet getRevolutionDataFromDB(String id) throws SQLException {
        return query("SELECT * FROM revolution WHERE id = ?", id);
    }

    @Override
    public void saveData() {
        try {
            this.refreshConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        load();
    }
}