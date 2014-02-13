package net.robyf.dbpatcher.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.robyf.dbpatcher.TestConfig;
import net.robyf.dbpatcher.util.Column;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.UtilException;


public final class DBTestUtil {

    private static final String DB_SUPERUSER_USERNAME = TestConfig.getUsername();
    private static final String DB_SUPERUSER_PASSWORD = TestConfig.getPassword();

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new UtilException("Error loading JDBC driver", cnfe);
        }
    }

    private DBTestUtil() {
    }

    public static boolean databaseExists(final String databaseName) {
        String url = "jdbc:mysql://localhost/" + databaseName;
        Connection connection;
        try {
            connection =
                    DriverManager.getConnection(url,
                                                DB_SUPERUSER_USERNAME,
                                                DB_SUPERUSER_PASSWORD);
        } catch (SQLException sqle) {
            return false;
        }
        DBUtil.closeConnection(connection);
        return true;
    }

    public static Connection getConnection(final String databaseName) {
        String url = "jdbc:mysql://localhost/" + databaseName;
        Connection connection;
        try {
            connection =
                    DriverManager.getConnection(url,
                                                DB_SUPERUSER_USERNAME,
                                                DB_SUPERUSER_PASSWORD);
            return connection;
        } catch (SQLException sqle) {
            throw new UtilException("Error getting a connection", sqle);
        }
    }

    public static List<Column> getColumnsFor(final String tableName, final Connection connection) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("select * from " + tableName);
            ResultSetMetaData metadata = stmt.getMetaData();

            List<Column> result = new LinkedList<Column>();
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                result.add(new Column(metadata, i));
            }
            return result;
        } catch (SQLException sqle) {
            throw new UtilException("Error reading data for table: " + tableName, sqle);
        } finally {
            DBUtil.closeStatement(stmt);
        }
    }

}
