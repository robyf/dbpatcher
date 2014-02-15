package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import net.robyf.dbpatcher.TestConfig;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;


public final class DBUtilTest {

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
    public void testCloseConnection_null() {
        try {
            DBUtil.closeConnection(null);
        } catch (NullPointerException npe) {
            fail("NullPointerException thrown");
        }
    }

    @Test (expected = UtilException.class)
    public void testCloseConnection_sql_exception() throws Exception {
        Connection connection = EasyMock.createMock(Connection.class);
        connection.close();
        EasyMock.expectLastCall().andThrow(new SQLException());
        EasyMock.replay(connection);

        DBUtil.closeConnection(connection);
    }

    @Test
    public void testCloseStatement_null() {
        try {
            DBUtil.closeStatement(null);
        } catch (NullPointerException npe) {
            fail("NullPointerException thrown");
        }
    }

    @Test (expected = UtilException.class)
    public void testCloseStatement_sql_exception() throws Exception {
        Statement statement = EasyMock.createMock(Statement.class);
        statement.close();
        EasyMock.expectLastCall().andThrow(new SQLException());
        EasyMock.replay(statement);

        DBUtil.closeStatement(statement);
    }

    @Test
    public void testCloseResultSet_null() {
        try {
            DBUtil.closeResultSet(null);
        } catch (NullPointerException npe) {
            fail("NullPointerException thrown");
        }
    }

    @Test (expected = UtilException.class)
    public void testCloseResultSet_sql_exception() throws Exception {
        ResultSet resultSet = EasyMock.createMock(ResultSet.class);
        resultSet.close();
        EasyMock.expectLastCall().andThrow(new SQLException());
        EasyMock.replay(resultSet);

        DBUtil.closeResultSet(resultSet);
    }

    @Test
    public void testGetTables() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertEquals(Collections.emptyList(), DBUtil.getTables(connection));

            Statement stmt = connection.createStatement();
            stmt.execute("create table MYTABLE ("
                         + "  ID bigint(20) not null primary key, "
                         + "  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL "
                         + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            DBUtil.closeStatement(stmt);

            assertEquals(Collections.singletonList("MYTABLE"), DBUtil.getTables(connection));

            stmt = connection.createStatement();
            stmt.execute("create table ANOTHERTABLE ("
                         + "  ID bigint(20) not null primary key, "
                         + "  NAME varchar(10) character set latin1 collate latin1_bin NOT NULL "
                         + ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
            DBUtil.closeStatement(stmt);

            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("MYTABLE"));
            assertTrue(tables.contains("ANOTHERTABLE"));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testGetDatabaseVersion_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertNull(DBUtil.getDatabaseVersion(connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testGetDatabaseVersion_empty_table() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            MySqlUtil.createDatabaseVersionTable(connection);
            assertNull(DBUtil.getDatabaseVersion(connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testGetDatabaseVersion() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            MySqlUtil.createDatabaseVersionTable(connection);
            DBUtil.updateDatabaseVersion(new Long(23), connection);
            assertEquals(new Long(23), DBUtil.getDatabaseVersion(connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test (expected = UtilException.class)
    public void testUpdateDatabaseVersion_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            DBUtil.updateDatabaseVersion(new Long(32), connection);
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testUpdateDatabaseVersion_existing_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        MySqlUtil.restore(DATABASE_NAME,
                          "config/test/backups/only_version_table_version_1.sql",
                          USERNAME,
                          PASSWORD);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            DBUtil.updateDatabaseVersion(new Long(32), connection);
            assertEquals(new Long(32), DBUtil.getDatabaseVersion(connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

}
