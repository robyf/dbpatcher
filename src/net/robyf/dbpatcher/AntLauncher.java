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
import java.nio.charset.Charset;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public final class AntLauncher extends Task {

    private String username;
    private String password;
    private String database;
    private File schemaRoot;
    private Long version = null;
    private boolean rollbackIfError = false;
    private boolean simulationMode = false;
    private Charset charset = Charset.forName("ISO-8859-1");

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setDatabase(final String database) {
        this.database = database;
    }

    public void setSchemaRoot(final File schemaRoot) {
        this.schemaRoot = schemaRoot;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public void setRollbackIfError(final boolean rollbackIfError) {
        this.rollbackIfError = rollbackIfError;
    }

    public void setSimulationMode(final boolean simulationMode) {
        this.simulationMode = simulationMode;
    }

    public void setCharset(final String charset) {
        this.charset = Charset.forName(charset);
    }

    @Override
    public void execute() {
        if (this.username == null) {
            throw new BuildException("Username hasn't been specified");
        }
        if (this.password == null) {
            throw new BuildException("Password hasn't been specified");
        }
        if (this.database == null) {
            throw new BuildException("Database name hasn't been specified");
        }
        if (this.schemaRoot == null) {
            throw new BuildException("Schema root directory hasn't been specified");
        }

        Parameters parameters = new Parameters();
        parameters.setUsername(this.username);
        parameters.setPassword(this.password);
        parameters.setDatabaseName(this.database);
        parameters.setSchemaPath(this.schemaRoot.getAbsolutePath());
        parameters.setTargetVersion(this.version);
        parameters.setRollbackIfError(this.rollbackIfError);
        parameters.setSimulationMode(this.simulationMode);
        parameters.setCharset(this.charset);

        LogFactory.setLog(new AntLog(this));

        try {
            DBPatcherFactory.getDBPatcher().patch(parameters);
        } catch (Exception e) {
            throw new BuildException("Error patching database", e);
        }
    }

}
