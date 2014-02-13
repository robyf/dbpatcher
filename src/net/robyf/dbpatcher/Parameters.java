package net.robyf.dbpatcher;

import java.nio.charset.Charset;

public final class Parameters {

    private String databaseName;
    private String username;
    private String password;
    private String schemaPath;
    private Long targetVersion;
    private boolean rollbackIfError = false;
    private boolean simulationMode = false;
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

    public boolean rollbackIfError() {
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

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(final Charset charset) {
        this.charset = charset;
    }

}
