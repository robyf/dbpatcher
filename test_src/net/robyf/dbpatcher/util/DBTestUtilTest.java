package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import net.robyf.dbpatcher.TestConfig;
import net.robyf.dbpatcher.util.Column;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.MySqlUtil;
import net.robyf.dbpatcher.util.UtilException;

import org.junit.After;
import org.junit.Test;


public final class DBTestUtilTest {

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
    public void testDatabaseExists() {
        assertFalse(DBTestUtil.databaseExists("kalakukko"));
        assertTrue(DBTestUtil.databaseExists("test"));
    }

    @Test (expected = UtilException.class)
    public void testGetConnection_unexisting_database() {
        DBTestUtil.getConnection("kalakukko");
    }

    @Test
    public void testGetConnection() {
        Connection connection = DBTestUtil.getConnection("test");
        assertNotNull(connection);
        DBUtil.closeConnection(connection);
    }

    @Test
    public void testGetColumnsFor() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("create table MYTABLE ("
                         + "  ID bigint(20) not null primary key, "
                         + "  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL, "
                         + "  COMMENT varchar(255) character set latin1 collate latin1_bin "
                         + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            DBUtil.closeStatement(stmt);

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 20, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            expected.add(new Column("COMMENT", "VARCHAR", 255, true));
            
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test (expected = UtilException.class)
    public void testGetColumnsFor_missing_table() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            DBTestUtil.getColumnsFor("MYTABLE", connection);
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

}
