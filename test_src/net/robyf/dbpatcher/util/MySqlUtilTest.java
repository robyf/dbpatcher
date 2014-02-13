package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import net.robyf.dbpatcher.TestConfig;
import net.robyf.dbpatcher.util.Column;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.MySqlUtil;
import net.robyf.dbpatcher.util.UtilException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class MySqlUtilTest {

    private static final String DATABASE_NAME = TestConfig.getDatabaseName();
    private static final String USERNAME = TestConfig.getUsername();
    private static final String PASSWORD = TestConfig.getPassword();
    private static final String BACKUP_FILE = "/tmp/backup.txt";

    @Before
    public void setUp() {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
    }

    @After
    public void tearDown() {
        MySqlUtil.dropDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        File file = new File(BACKUP_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCreateAndDropDatabase() {
        final String databaseName = "kalakukko";
        assertFalse(DBTestUtil.databaseExists(databaseName));

        MySqlUtil.createDatabase(databaseName, USERNAME, PASSWORD);
        assertTrue(DBTestUtil.databaseExists(databaseName));

        MySqlUtil.dropDatabase(databaseName, USERNAME, PASSWORD);
        assertFalse(DBTestUtil.databaseExists(databaseName));
    }

    @Test (expected = UtilException.class)
    public void testCreateDatabase_already_existing() {
        final String databaseName = "test";
        assertTrue(DBTestUtil.databaseExists(databaseName));

        MySqlUtil.createDatabase(databaseName, USERNAME, PASSWORD);
    }

    @Test (expected = UtilException.class)
    public void testDropDatabase_not_existing() {
        final String databaseName = "kalakukko";
        assertFalse(DBTestUtil.databaseExists(databaseName));

        MySqlUtil.dropDatabase(databaseName, USERNAME, PASSWORD);
    }

    @Test
    public void testBackup() throws Exception {
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("create table MYTABLE ("
                         + "  ID bigint(20) not null primary key, "
                         + "  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL "
                         + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            stmt.execute("insert into MYTABLE (ID, NAME) values (1, 'value1')");
            stmt.execute("insert into MYTABLE (ID, NAME) values (2, 'value2')");
            DBUtil.closeStatement(stmt);

            MySqlUtil.backup(DATABASE_NAME, BACKUP_FILE, USERNAME, PASSWORD);

            File file = new File(BACKUP_FILE);
            assertTrue(file.exists());

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();

            String backup = builder.toString();
            assertTrue(backup.contains("DROP TABLE IF EXISTS `MYTABLE`;"));
            assertTrue(backup.contains("CREATE TABLE `MYTABLE`"));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test (expected = UtilException.class)
    public void testBackup_unexisting_database() {
        MySqlUtil.backup("kalakukko", BACKUP_FILE, USERNAME, PASSWORD);
    }

    @Test
    public void testRestore() throws Exception {
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        List<Column> columns;
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("create table MYTABLE ("
                         + "  ID bigint(20) not null primary key, "
                         + "  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL "
                         + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            stmt.execute("insert into MYTABLE (ID, NAME) values (1, 'value1')");
            stmt.execute("insert into MYTABLE (ID, NAME) values (2, 'value2')");
            DBUtil.closeStatement(stmt);

            columns = DBTestUtil.getColumnsFor("MYTABLE", connection);
            MySqlUtil.backup(DATABASE_NAME, BACKUP_FILE, USERNAME, PASSWORD);
        } finally {
            DBUtil.closeConnection(connection);
        }
        
        MySqlUtil.dropDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        MySqlUtil.restore(DATABASE_NAME, BACKUP_FILE, USERNAME, PASSWORD);
        
        connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertEquals(Collections.singletonList("MYTABLE"), DBUtil.getTables(connection));
            assertEquals(columns, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testCreateDatabaseVersionTable() throws Exception {
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertTrue(DBUtil.getTables(connection).isEmpty());
            MySqlUtil.createDatabaseVersionTable(connection);

            assertEquals(Collections.singletonList("DATABASE_VERSION"),
                         DBUtil.getTables(connection));
            List<Column> columns = DBTestUtil.getColumnsFor("DATABASE_VERSION", connection);
            assertEquals(1, columns.size());
            Column column = columns.get(0);
            assertEquals("VERSION", column.getName());
            assertEquals("BIGINT", column.getType());
            assertEquals(20, column.getSize());
            assertFalse(column.isNullable());
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test (expected = UtilException.class)
    public void testCreateDatabaseVersionTable_twice() throws Exception {
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertTrue(DBUtil.getTables(connection).isEmpty());
            MySqlUtil.createDatabaseVersionTable(connection);
            MySqlUtil.createDatabaseVersionTable(connection);
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

}
