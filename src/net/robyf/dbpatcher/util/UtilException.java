package net.robyf.dbpatcher.util;

public final class UtilException extends RuntimeException {

    public UtilException(final String message) {
        super(message);
    }

    public UtilException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
