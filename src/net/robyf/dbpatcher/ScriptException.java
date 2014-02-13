package net.robyf.dbpatcher;

public final class ScriptException extends DBPatcherException {

    public ScriptException(final Long version, final Throwable cause) {
        super("An error occurred while patching to version: " + version, cause);
    }

}
