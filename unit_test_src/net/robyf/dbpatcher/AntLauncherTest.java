package net.robyf.dbpatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.Charset;

import net.robyf.dbpatcher.AntLauncher;
import net.robyf.dbpatcher.DBPatcher;
import net.robyf.dbpatcher.DBPatcherException;
import net.robyf.dbpatcher.DBPatcherFactory;
import net.robyf.dbpatcher.Parameters;

import org.apache.tools.ant.BuildException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;

public final class AntLauncherTest {
    
    private final AntLauncher launcher = new AntLauncher();

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

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("schema"));
        this.launcher.execute();
        EasyMock.verify(patcher);
    }

    @Test (expected = BuildException.class)
    public void testLauncher_error() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        patcher.patch(EasyMock.isA(Parameters.class));
        EasyMock.expectLastCall().andThrow(new BuildException());
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("schema"));
        this.launcher.execute();
    }

    @Test
    public void testLauncher_missing_username() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("schema"));
        try {
            this.launcher.execute();
            fail("BuildException not thrown");
        } catch (BuildException be) {
        }
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_password() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        this.launcher.setUsername("username");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("schema"));
        try {
            this.launcher.execute();
            fail("BuildException not thrown");
        } catch (BuildException be) {
        }
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_database_name() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setSchemaRoot(new File("schema"));
        try {
            this.launcher.execute();
            fail("BuildException not thrown");
        } catch (BuildException be) {
        }
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_missing_schema_root() throws Exception {
        DBPatcher patcher = EasyMock.createMock(DBPatcher.class);
        EasyMock.replay(patcher);
        DBPatcherFactory.setDBPatcher(patcher);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        try {
            this.launcher.execute();
            fail("BuildException not thrown");
        } catch (BuildException be) {
        }
        EasyMock.verify(patcher);
    }

    @Test
    public void testLauncher_min_parameter_set() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("/schema"));
        this.launcher.execute();

        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("/schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
        assertEquals(Charset.forName("ISO-8859-1"), parms.getCharset());
    }

    @Test
    public void testLauncher_with_rollback_if_error() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("/schema"));
        this.launcher.setRollbackIfError(true);
        this.launcher.execute();

        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("/schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertTrue(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_with_version_number() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("/schema"));
        this.launcher.setVersion(new Long(15));
        this.launcher.execute();

        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("/schema", parms.getSchemaPath());
        assertEquals(new Long(15), parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertFalse(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_with_simulation_mode() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("/schema"));
        this.launcher.setSimulationMode(true);
        this.launcher.execute();

        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("/schema", parms.getSchemaPath());
        assertNull(parms.getTargetVersion());
        assertFalse(parms.rollbackIfError());
        assertTrue(parms.isSimulationMode());
    }

    @Test
    public void testLauncher_different_charset() throws Exception {
        FakePatcher fake = new FakePatcher();
        DBPatcherFactory.setDBPatcher(fake);

        this.launcher.setUsername("username");
        this.launcher.setPassword("password");
        this.launcher.setDatabase("database");
        this.launcher.setSchemaRoot(new File("/schema"));
        this.launcher.setCharset("UTF-8");
        this.launcher.execute();

        Parameters parms = fake.getParameters();
        assertEquals("username", parms.getUsername());
        assertEquals("password", parms.getPassword());
        assertEquals("database", parms.getDatabaseName());
        assertEquals("/schema", parms.getSchemaPath());
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
