package me.redtea.factionrevolutions.db.impl.sql;

import me.redtea.factionrevolutions.db.impl.sql.adapter.SQLAdapter;

import java.sql.*;
import java.util.concurrent.*;

public abstract class SQLDatabase {

    public final SQLAdapter adapter = new SQLAdapter();

    protected ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public abstract void execute(boolean async, String sql, Object...objects);

    public abstract ResultSet query(String sql, Object...objects);
    public abstract void query(boolean async, String sql, ResponseHandler<ResultSet, SQLException> handler, Object...objects);

    public abstract Connection getConnection();

    public abstract void closeConnection() throws SQLException;
}