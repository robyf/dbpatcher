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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import net.robyf.dbpatcher.LogFactory;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class MySqlUtil {

    private MySqlUtil() {
    }

    public static void createDatabase(final String databaseName, final String username,
            final String password) {
        CommandLine command = new CommandLine("mysqladmin");
        addCredentials(command, username, password);
        command.addArgument("create");
        command.addArgument(databaseName);

        execute(command);
    }

    public static void dropDatabase(final String databaseName, final String username,
            final String password) {
        CommandLine command = new CommandLine("mysqladmin");
        addCredentials(command, username, password);
        command.addArgument("drop");
        command.addArgument("-f");
        command.addArgument(databaseName);

        execute(command);
    }

    public static void backup(final String databaseName, final String fileName,
            final String username, final String password) {
        LogFactory.getLog().log("Backing up " + databaseName + " into " + fileName);

        CommandLine command = new CommandLine("mysqldump");
        addCredentials(command, username, password);
        command.addArgument(databaseName);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(executeAndGetOutput(command)));
                PrintWriter writer = new PrintWriter(new File(fileName))) {
            String line = reader.readLine();
            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }
        } catch (IOException ioe) {
            throw new UtilException("Error backing up a database", ioe);
        }
    }

    public static void restore(final String databaseName, final String fileName,
            final String username, final String password) {
        LogFactory.getLog().log("Restoring from " + fileName + " into " + databaseName);
        try {
            Process process = Runtime.getRuntime()
                    .exec("mysql -u " + username + " --password=" + password + " " + databaseName);
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            PrintWriter writer = new PrintWriter(process.getOutputStream());
            String line = reader.readLine();
            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }
            reader.close();
            writer.close();

            process.waitFor();
            if (process.exitValue() != 0) {
                throw new UtilException("Error restoring database, return code from mysql = "
                        + process.exitValue());
            }
        } catch (IOException ioe) {
            throw new UtilException("Error restoring a database", ioe);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new UtilException("Error restoring a database", ie);
        }
    }

    public static void createDatabaseVersionTable(final Connection connection) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(
                    "create table DATABASE_VERSION (" + "  VERSION bigint(20) not null primary key"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
        } catch (SQLException sqle) {
            throw new UtilException("Error creating the database version table", sqle);
        } finally {
            DBUtil.closeStatement(stmt);
        }
    }

    private static void addCredentials(final CommandLine commandLine, final String username,
            final String password) {
        commandLine.addArgument("-u").addArgument(username);
        commandLine.addArgument("--password=" + password);
    }

    private static void execute(final CommandLine commandLine) {
        try {
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWatchdog(new ExecuteWatchdog(300000L));
            int returnCode = executor.execute(commandLine);
            if (returnCode != 0) {
                throw new UtilException(
                        "Error executing " + commandLine + ", return code = " + returnCode);
            }
        } catch (IOException ioe) {
            throw new UtilException("Error executing: " + commandLine, ioe);
        }
    }

    private static InputStream executeAndGetOutput(final CommandLine commandLine) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            PumpStreamHandler handler = new PumpStreamHandler(outStream);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setWatchdog(new ExecuteWatchdog(300000L));
            executor.setStreamHandler(handler);
            int returnCode = executor.execute(commandLine);
            if (returnCode != 0) {
                throw new UtilException(
                        "Error executing " + commandLine + ", return code = " + returnCode);
            }
            return new ByteArrayInputStream(outStream.toByteArray());
        } catch (IOException ioe) {
            throw new UtilException("Error executing: " + commandLine, ioe);
        }
    }

}
