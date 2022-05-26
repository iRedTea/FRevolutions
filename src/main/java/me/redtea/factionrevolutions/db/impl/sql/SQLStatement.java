package me.redtea.factionrevolutions.db.impl.sql;

import java.io.*;
import java.sql.*;

public class SQLStatement implements Closeable {

    private final PreparedStatement statement;
    private ResultSet resultSet;

    public SQLStatement(Connection connection, String sql, Object... objects) throws SQLException {
        this.statement = connection.prepareStatement(sql);

        if (objects != null && objects.length > 0)
            for (int i = 0; i < objects.length; i++)
                this.statement.setObject(i + 1, objects[i]);
    }

    public int execute() throws SQLException {
        return this.statement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return this.resultSet = this.statement.executeQuery();
    }

    @Override
    public void close() {
        try {
            this.statement.close();

            if (this.resultSet != null) {
                this.resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
