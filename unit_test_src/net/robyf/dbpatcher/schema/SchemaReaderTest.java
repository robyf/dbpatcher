package net.robyf.dbpatcher.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;

import net.robyf.dbpatcher.schema.Schema;
import net.robyf.dbpatcher.schema.SchemaException;
import net.robyf.dbpatcher.schema.SchemaReader;
import net.robyf.dbpatcher.util.UtilException;

import org.junit.Test;


public final class SchemaReaderTest {
    
    private static final Charset CHARSET = Charset.forName("ISO-8859-1");

    @Test (expected = SchemaException.class)
    public void testRead_unexistant_directory() {
        SchemaReader.read(new File("kalakukko"), CHARSET);
    }

    @Test (expected = UtilException.class)
    public void testRead_not_a_zip_file() {
        SchemaReader.read(new File("config/checkstyle.xsl"), CHARSET);
    }

    @Test (expected = SchemaException.class)
    public void testRead_empty_directory() {
        SchemaReader.read(new File("config/test/empty"), CHARSET);
    }

    @Test (expected = SchemaException.class)
    public void testRead_missing_version_directories() {
        SchemaReader.read(new File("config/test/test01/1"), CHARSET);
    }

    @Test (expected = SchemaException.class)
    public void testRead_missing_versions() {
        SchemaReader.read(new File("config/test"), CHARSET);
    }

    @Test (expected = SchemaException.class)
    public void testRead_empty_version_inside_schema() {
        SchemaReader.read(new File("config/test/test02"), CHARSET);
    }

    @Test (expected = SchemaException.class)
    public void testRead_version_without_scripts_inside_schema() {
        SchemaReader.read(new File("config/test/test03"), CHARSET);
    }

    @Test
    public void testRead() {
        Schema schema = SchemaReader.read(new File("config/test/test01"), CHARSET);
        assertNotNull(schema);
        Set<Long> versions = schema.getAvailableVersions();
        assertEquals(2, versions.size());
        assertTrue(versions.contains(new Long(1)));
        assertTrue(versions.contains(new Long(2)));
    }

}
