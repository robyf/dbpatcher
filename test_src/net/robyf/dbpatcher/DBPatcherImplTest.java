package net.robyf.dbpatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import net.robyf.dbpatcher.util.Column;
import net.robyf.dbpatcher.util.DBTestUtil;
import net.robyf.dbpatcher.util.DBUtil;
import net.robyf.dbpatcher.util.MySqlUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public final class DBPatcherImplTest {

    private static final String DATABASE_NAME = TestConfig.getDatabaseName();
    private static final String USERNAME = TestConfig.getUsername();
    private static final String PASSWORD = TestConfig.getPassword();

    private DBPatcherImpl patcher;

    @Before
    public void setUp() throws Exception {
        this.patcher = new DBPatcherImpl();
    }

    @After
    public void tearDown() {
        if (DBTestUtil.databaseExists(DATABASE_NAME)) {
            MySqlUtil.dropDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        }
    }

    @Test
    public void testPatch_simple01_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple01");
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            
            assertEquals(new Long(1), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple02_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple02");
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(3, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            assertTrue(tables.contains("ARTISTS"));
            
            assertEquals(new Long(2), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));

            expected = new LinkedList<Column>();
            expected.add(new Column("ARTIST_ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 256, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("ARTISTS", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple02_from_version_1() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        MySqlUtil.restore(DATABASE_NAME,
                          "config/test/backups/simple02_version_1.sql",
                          USERNAME,
                          PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple02");
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(3, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            assertTrue(tables.contains("ARTISTS"));
            
            assertEquals(new Long(2), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));

            expected = new LinkedList<Column>();
            expected.add(new Column("ARTIST_ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 256, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("ARTISTS", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple02_empty_db_to_version_1() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple02");
        parameters.setTargetVersion(new Long(1));
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            
            assertEquals(new Long(1), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_error01_default_behaviour() {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/error01");
        try {
            this.patcher.patch(parameters);
            fail("DBPatcherException hasn't been thrown");
        } catch (DBPatcherException dbpe) {
            
        }
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(3, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            assertTrue(tables.contains("ARTISTS"));
            
            assertEquals(new Long(2), DBUtil.getDatabaseVersion(connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_error01_rollback_if_error() {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/error01");
        parameters.setRollbackIfError(true);
        try {
            this.patcher.patch(parameters);
            fail("DBPatcherException hasn't been thrown");
        } catch (DBPatcherException dbpe) {
            
        }
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            assertTrue(DBUtil.getTables(connection).isEmpty());
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple02_empty_db_from_zip() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple02.zip");
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(3, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            assertTrue(tables.contains("ARTISTS"));
            
            assertEquals(new Long(2), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));

            expected = new LinkedList<Column>();
            expected.add(new Column("ARTIST_ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 256, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("ARTISTS", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple02_empty_db_simulation_mode() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple02");
        parameters.setSimulationMode(true);
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertTrue(tables.isEmpty());
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple03_empty_db() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);
        
        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple03");
        this.patcher.patch(parameters);
        
        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));
            
            assertEquals(new Long(1), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

    @Test
    public void testPatch_simple03_insecure_mode() throws Exception {
        MySqlUtil.createDatabase(DATABASE_NAME, USERNAME, PASSWORD);

        Parameters parameters = new Parameters();
        parameters.setDatabaseName(DATABASE_NAME);
        parameters.setUsername(USERNAME);
        parameters.setPassword(PASSWORD);
        parameters.setSchemaPath("config/test/simple03");
        parameters.setInsecureMode(true);
        this.patcher.patch(parameters);

        Connection connection = DBTestUtil.getConnection(DATABASE_NAME);
        try {
            List<String> tables = DBUtil.getTables(connection);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("DATABASE_VERSION"));
            assertTrue(tables.contains("MYTABLE"));

            assertEquals(new Long(1), DBUtil.getDatabaseVersion(connection));

            List<Column> expected = new LinkedList<Column>();
            expected.add(new Column("ID", "BIGINT", 19, false));
            expected.add(new Column("NAME", "VARCHAR", 10, false));
            assertEquals(expected, DBTestUtil.getColumnsFor("MYTABLE", connection));
        } finally {
            DBUtil.closeConnection(connection);
        }
    }

}
