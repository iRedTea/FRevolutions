package me.redtea.factionrevolutions.db.impl.sql.type;
import com.mysql.jdbc.jdbc2.optional.*;
import lombok.*;
import me.redtea.factionrevolutions.db.impl.sql.ResponseHandler;
import me.redtea.factionrevolutions.db.impl.sql.SQLDatabase;
import me.redtea.factionrevolutions.db.impl.sql.SQLStatement;;

import java.sql.*;

public class MySQLDatabase implements SQLDatabase {

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
}