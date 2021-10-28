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

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class Parameters {

    private String databaseName;
    private String username;
    private String password;
    private String schemaPath;
    private Long targetVersion;
    private boolean rollbackIfError = false;
    private boolean simulationMode = false;
    private boolean insecureMode = false;
    private Charset charset = Charset.forName("ISO-8859-1");

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getSchemaPath() {
        return schemaPath;
    }

    public void setSchemaPath(final String schemaPath) {
        this.schemaPath = schemaPath;
    }

    public Long getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(final Long targetVersion) {
        this.targetVersion = targetVersion;
    }

    public boolean rollbackIfError() { //NOSONAR
        return rollbackIfError;
    }

    public void setRollbackIfError(final boolean rollbackIfError) {
        this.rollbackIfError = rollbackIfError;
    }

    public boolean isSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(final boolean simulationMode) {
        this.simulationMode = simulationMode;
    }

    public boolean isInsecureMode() {
        return insecureMode;
    }

    public void setInsecureMode(final boolean insecureMode) {
        this.insecureMode = insecureMode;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

}
