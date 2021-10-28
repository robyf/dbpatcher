/*
 * Copyright 2014 Roberto Fasciolo
 *
 * This file is part of dbpatcher.
 *
 * dbpatcher is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * dbpatcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dbpatcher; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.robyf.dbpatcher.util;

import net.robyf.dbpatcher.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roberto Fasciolo
 * @since 0.9.0
 */
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

    /**
     * Closes a database connection.
     *
     * @param connection The connection to be closed
     */
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

    /**
     * Closes a database statement
     *
     * @param statement The statement to be closed
     */
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

    /**
     * Closes a result set.
     *
     * @param resultSet The result set to be closed
     */
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

    /**
     * Lists the tables present in a database.
     *
     * @param connection Connection to the database
     * @return List of table names
     */
    public static List<String> getTables(final Connection connection) {
        try (Statement stmt = connection.createStatement();
                ResultSet resultSet = stmt.executeQuery("show tables")) {
            List<String> results = new LinkedList<>();
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }

            return results;
        } catch (SQLException sqle) {
            throw new UtilException("Error getting tables", sqle);
        }
    }

    /**
     * Reads the version number from a database.
     *
     * @param connection Connection to the database
     * @return The version number, or null if the versions table is not present
     */
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
                    result = resultSet.getLong("VERSION");
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

    /**
     * Updates the version number in a database.
     *
     * @param version    The new version number
     * @param connection Connection to the database
     */
    public static void updateDatabaseVersion(final Long version, final Connection connection) {
        LogFactory.getLog().log("Updating database version to: " + version);
        PreparedStatement insertStmt = null;
        try (PreparedStatement updateStmt = connection.prepareStatement(
                "update DATABASE_VERSION set VERSION = ?")) {
            updateStmt.setLong(1, version.longValue());

            if (updateStmt.executeUpdate() == 0) {
                insertStmt =
                        connection.prepareStatement("insert into DATABASE_VERSION values (?)");
                insertStmt.setLong(1, version.longValue());
                insertStmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            throw new UtilException("Error updating the database version", sqle);
        } finally {
            DBUtil.closeStatement(insertStmt);
        }
    }

}
