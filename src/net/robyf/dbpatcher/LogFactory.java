package net.robyf.dbpatcher;

public final class LogFactory {

    private static Log log = null;

    private LogFactory() {
    }

    public static Log getLog() {
        if (LogFactory.log != null) {
            return LogFactory.log;
        }
        return new ConsoleLog();
    }

    public static void setLog(final Log log) {
        LogFactory.log = log;
    }

}
