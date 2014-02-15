package net.robyf.dbpatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

public final class LauncherTest {

    @After
    public void tearDown() {
        DBPatcherFactory.reset();
    }

    @Test
    public void testLauncher() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        patcher.patch(EasyMock.isA(Parameters.class));
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "schema" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_username() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-p", "password", "-d", "database", "schema" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_password() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-u", "username", "-d", "database", "schema" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_database_name() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "schema" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_schema_root() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_multiple_schema_roots() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "schema1", "schema2" });
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_min_parameter_set() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "schema" });
        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
        assertEquals(Charset.forName("ISO-8859-1"), parms.getCharset());
    }

    @Test
    public void testLauncher_with_rollback_if_error() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "-r", "schema" });
        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertTrue(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_with_version_number() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "-v", "15", "schema" });
        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("schema", parms.getSchemaPath());
        assertEquals(new Long(15), parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_with_simulation() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "-s", "schema" });
        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertTrue(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_different_character_set() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        Launcher.main(new String[] { "-u", "username", "-p", "password", "-d", "database",
                                    "schema", "-c", "UTF-8" });
        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
        assertEquals(Charset.forName("UTF-8"), parms.getCharset());
    }

    private final class FakePatcher implements DBPatcher {

        private Parameters parameters;

        @Override
        public void patch(final Parameters parameters) throws DBPatcherException {
            this.parameters = parameters;
        }

        public Parameters getParameters() {
            return parameters;
        }

    }

}
