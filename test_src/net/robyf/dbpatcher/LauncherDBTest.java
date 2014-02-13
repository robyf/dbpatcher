package net.robyf.dbpatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import net.robyf.dbpatcher.Launcher;
import net.robyf.dbpatcher.util.Column;
import net.robyf.dbpatcher.util.DBTestUtil;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.MySqlUtil;

import org.junit.After;
import org.junit.Test;


public final class LauncherDBTest {

    private static final String DATABASE_NAME = TestConfig.getDatabaseName();
    private static final String USERNAME = TestConfig.getUsername();
    private static final String PASSWORD = TestConfig.getPassword();

    @After
    public void tearDown() {
        if (DBTestUtil.databaseExists(DATABASE_NAME)) {
            MySqlUtil.dropDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        }
    }

    @Test
    public void testLauncher_simple01_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Launcher.main(new String[] { "-u", USERNAME, "-p", PASSWORD, "-d", DATABASE_NAME,
                                    "config/test/simple01" });

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));

            assertEquals(new Long(1), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 20, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

}
