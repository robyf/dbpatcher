package net.robyf.dbpatcher;

public final class DBPatcherFactory {

    private static DBPatcher patcher = null;

    private DBPatcherFactory() {
    }

    public static DBPatcher getDBPatcher() throws DBPatcherException {
        if (patcher != null) {
            return patcher;
        }
        return new DBPatcherImpl();
    }

    public static void setDBPatcher(final DBPatcher patcher) {
        DBPatcherFactory.patcher = patcher;
    }

    public static void reset() {
        DBPatcherFactory.patcher = null;
    }

}
