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
package net.robyf.dbpatcher;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import net.robyf.dbpatcher.schema.Schema;
import net.robyf.dbpatcher.schema.SchemaReader;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.MySqlUtil;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
final class DBPatcherImpl implements DBPatcher {

    private Parameters parameters;
    private Schema schema;
    private Connection connection;

    DBPatcherImpl() throws DBPatcherException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new DBPatcherException("Error loading JDBC driver", cnfe);
        }
    }

    @Override
    public void patch(final Parameters parameters) throws DBPatcherException {
        this.parameters = parameters;
        LogFactory.getLog().log("Using database: " + this.parameters.getDatabaseName());
        this.schema =
                SchemaReader.read(new File(parameters.getSchemaPath()),
                                  this.parameters.getCharset());

        File backupFile = null;
        if (this.parameters.rollbackIfError() || this.parameters.isSimulationMode()) {
            backupFile = this.backup();
        }

        String tempDatabase = null;
        if (this.parameters.isSimulationMode()) {
            LogFactory.getLog().log("Simulation mode enabled");
            tempDatabase = "dbp" + System.currentTimeMillis();
            LogFactory.getLog().log("Creating temporary database: " + tempDatabase);
            MySqlUtil.createDatabase(tempDatabase,
                                     this.parameters.getUsername(),
                                     this.parameters.getPassword());
            MySqlUtil.restore(tempDatabase,
                              backupFile.getAbsolutePath(), //NOSONAR
                              this.parameters.getUsername(),
                              this.parameters.getPassword());
            this.connection = this.getConnection(tempDatabase);
        } else {
            this.connection = this.getConnection();
        }
        try {
            List<Long> steps;
            if (this.parameters.getTargetVersion() == null) {
                steps =
                        this.schema.getStepsToLatest(DBUtil.getDatabaseVersion(this.connection));
            } else {
                steps =
                        this.schema.getSteps(DBUtil.getDatabaseVersion(this.connection),
                                             this.parameters.getTargetVersion());
            }
            LogFactory.getLog().log("Steps to be applied: " + steps.toString());
            for (Long step : steps) {
                this.applyStep(step);
            }
        } catch (DBPatcherException dbpe) {
            if (this.parameters.rollbackIfError()) {
                this.dropAndRestore(backupFile);
            }
            throw dbpe;
        } finally {
            DBUtil.closeConnection(connection);
            if (this.parameters.isSimulationMode()) {
                LogFactory.getLog().log("Dropping temporary database: " + tempDatabase);
                MySqlUtil.dropDatabase(tempDatabase,
                                       this.parameters.getUsername(),
                                       this.parameters.getPassword());
            }
            if (backupFile != null) {
                if (!backupFile.delete()) {
                    LogFactory.getLog()
                            .log("Error deleting backup file: " + backupFile.getAbsolutePath());
                }
            }
        }
    }

    private Connection getConnection() throws DBPatcherException {
        return this.getConnection(this.parameters.getDatabaseName());
    }

    private Connection getConnection(final String databaseName) throws DBPatcherException {
        String url = "jdbc:mysql://localhost/" + databaseName;
        Connection connection;
        try {
            connection =
                    DriverManager.getConnection(url,
                                                this.parameters.getUsername(),
                                                this.parameters.getPassword());
            return connection;
        } catch (SQLException sqle) {
            throw new DBPatcherException("Error getting a connection", sqle);
        }
    }

    private void applyStep(final Long step) throws DBPatcherException {
        Statement statement = null;
        File backupFile = null;
        LogFactory.getLog().log("Applying step: " + step);
        try {
            if (!this.parameters.rollbackIfError()) {
                backupFile = this.backup();
            }

            statement = this.connection.createStatement();
            for (String query : this.schema.getStatementsForVersion(step)) {
                statement.executeUpdate(query);
            }

            if (!DBUtil.getTables(this.connection).contains("DATABASE_VERSION")) {
                MySqlUtil.createDatabaseVersionTable(this.connection);
            }
            DBUtil.updateDatabaseVersion(step, this.connection);
        } catch (SQLException sqle) {
            if (!this.parameters.rollbackIfError()) {
                this.dropAndRestore(backupFile);
            }
            throw new ScriptException(step, sqle);
        } finally {
            DBUtil.closeStatement(statement);
            if (backupFile != null) {
                if (!backupFile.delete()) {
                    LogFactory.getLog()
                            .log("Error deleting backup file: " + backupFile.getAbsolutePath());
                }
            }
        }
    }

    private File backup() throws DBPatcherException {
        try {
            File backupFile = File.createTempFile("dbpatcher", ".sql");
            MySqlUtil.backup(this.parameters.getDatabaseName(),
                             backupFile.getAbsolutePath(),
                             this.parameters.getUsername(),
                             this.parameters.getPassword());
            return backupFile;
        } catch (IOException ioe) {
            throw new DBPatcherException("Error backing up database", ioe);
        }
    }

    private void dropAndRestore(final File backupFile) {
        MySqlUtil.dropDatabase(this.parameters.getDatabaseName(),
                               this.parameters.getUsername(),
                               this.parameters.getPassword());
        MySqlUtil.createDatabase(this.parameters.getDatabaseName(),
                                 this.parameters.getUsername(),
                                 this.parameters.getPassword());
        MySqlUtil.restore(this.parameters.getDatabaseName(),
                          backupFile.getAbsolutePath(),
                          this.parameters.getUsername(),
                          this.parameters.getPassword());
    }

}
