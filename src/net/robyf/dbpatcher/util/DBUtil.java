package net.robyf.dbpatcher.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import net.robyf.dbpatcher.LogFactory;


public final class DBUtil {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new UtilException("Error loading JDBC driver", cnfe);
        }
    }

    private DBUtil() {
    }

    public static void closeConnection(final Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException sqle) {
            throw new UtilException("Error closing connection", sqle);
        }
    }

    public static void closeStatement(final Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (SQLException sqle) {
            throw new UtilException("Error closing statement", sqle);
        }
    }

    public static void closeResultSet(final ResultSet resultSet) {
        if (resultSet == null) {
            return;
        }
        try {
            resultSet.close();
        } catch (SQLException sqle) {
            throw new UtilException("Error closing resultSet", sqle);
        }
    }

    public static List<String> getTables(final Connection connection) {
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = connection.createStatement();
            resultSet = stmt.executeQuery("show tables");

            List<String> results = new LinkedList<String>();
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }

            return results;
        } catch (SQLException sqle) {
            throw new UtilException("Error getting tables", sqle);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closeStatement(stmt);
        }
    }

    public static Long getDatabaseVersion(final Connection connection) {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            Long result = null;

            // Check if the table exists in order to avoid SQLException
            List<String> tables = DBUtil.getTables(connection);
            if (tables.contains("DATABASE_VERSION")) {
                stmt = connection.prepareStatement("select * from DATABASE_VERSION");
                resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    result = new Long(resultSet.getLong("VERSION"));
                }
            }
            LogFactory.getLog().log("Current database version: " + result);
            return result;
        } catch (SQLException sqle) {
            throw new UtilException("Error reading the current database version", sqle);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closeStatement(stmt);
        }
    }

    public static void updateDatabaseVersion(final Long version, final Connection connection) {
        LogFactory.getLog().log("Updating database version to: " + version);
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        try {
            updateStmt = connection.prepareStatement("update DATABASE_VERSION set VERSION = ?");
            updateStmt.setLong(1, version.longValue());

            if (updateStmt.executeUpdate() == 0) {
                insertStmt = connection.prepareStatement("insert into DATABASE_VERSION values (?)");
                insertStmt.setLong(1, version.longValue());
                insertStmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            throw new UtilException("Error updating the database version", sqle);
        } finally {
            DBUtil.closeStatement(insertStmt);
            DBUtil.closeStatement(updateStmt);
        }
    }

}
