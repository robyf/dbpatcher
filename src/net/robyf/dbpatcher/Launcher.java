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

import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(final String[] args) throws DBPatcherException {
        Options options = new Options();

        Option usernameOption = new Option("u", "username", true, "Database username");
        usernameOption.setRequired(true);
        options.addOption(usernameOption);
        Option passwordOption = new Option("p", "password", true, "Database password");
        passwordOption.setRequired(true);
        options.addOption(passwordOption);
        Option databaseOption = new Option("d", "databaseName", true, "Database name");
        databaseOption.setRequired(true);
        options.addOption(databaseOption);
        options.addOption("r",
                          "rollback-if-error",
                          false,
                          "Rolls back the entire operation in case of errors");
        options.addOption("v",
                          "to-version",
                          true,
                          "Target version number");
        options.addOption("s",
                          "simulation",
                          false,
                          "Simulate the operation without touching the current database");
        options.addOption("c",
                          "character-set",
                          true,
                          "Character set (default value: ISO-8859-1)");

        Parameters parameters = new Parameters();
        boolean showHelp = false;
        try {
            CommandLine commandLine = new PosixParser().parse(options, args);

            parameters.setUsername(commandLine.getOptionValue("u"));
            parameters.setPassword(commandLine.getOptionValue("p"));
            parameters.setDatabaseName(commandLine.getOptionValue("d"));

            parameters.setRollbackIfError(commandLine.hasOption("r"));
            parameters.setSimulationMode(commandLine.hasOption("s"));
            if (commandLine.hasOption("v")) {
                parameters.setTargetVersion(new Long(commandLine.getOptionValue("v")));
            }
            if (commandLine.hasOption("c")) {
                parameters.setCharset(Charset.forName(commandLine.getOptionValue("c")));
            }

            if (commandLine.getArgs().length == 1) {
                parameters.setSchemaPath(commandLine.getArgs()[0]);
            } else {
                showHelp = true;
            }
        } catch (ParseException pe) {
            showHelp = true;
        }

        if (showHelp) {
            new HelpFormatter().printHelp("java -jar dbpatcher.jar"
                                                  + " -u username -p password -d database_name"
                                                  + " [options] schema_root",
                                          "Available options:",
                                          options,
                                          "");
        } else {
            DBPatcherFactory.getDBPatcher().patch(parameters);
        }
    }

}
