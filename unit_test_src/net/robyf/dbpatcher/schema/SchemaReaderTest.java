package net.robyf.dbpatcher.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        assertEquals(Collections.singletonList("statement1"),
                     schema.getStatementsForVersion(new Long(1)));

        assertTrue(versions.contains(new Long(2)));
        List<String> expected = new LinkedList<String>();
        expected.add("statement2-1");
        expected.add("statement2-2");
        assertEquals(expected, schema.getStatementsForVersion(new Long(2)));
    }

    @Test
    public void testRead_with_trailing_zeros() {
        Schema schema = SchemaReader.read(new File("config/test/test04"), CHARSET);
        assertNotNull(schema);
        Set<Long> versions = schema.getAvailableVersions();
        assertEquals(2, versions.size());

        assertTrue(versions.contains(new Long(1)));
        assertEquals(Collections.singletonList("statement1"),
                     schema.getStatementsForVersion(new Long(1)));

        assertTrue(versions.contains(new Long(2)));
        List<String> expected = new LinkedList<String>();
        expected.add("statement2-1");
        expected.add("statement2-2");
        assertEquals(expected, schema.getStatementsForVersion(new Long(2)));
    }

}
