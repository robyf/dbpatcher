package net.robyf.dbpatcher.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public final class SchemaTest {

    private final Schema schema =
            SchemaReader.read(new File("config/test/test01"), Charset.forName("ISO-8859-1"));

    @Test (expected = SchemaException.class)
    public void testGetStatementsForVersion_unexistant_version() {
        this.schema.getStatementsForVersion(new Long(23));
    }

    @Test
    public void testGetStatementsForVersion_one_file() {
        assertEquals(Collections.singletonList("statement1"),
                     this.schema.getStatementsForVersion(new Long(1)));
    }

    @Test
    public void testGetStatementsForVersion_two_file() {
        List<String> expected = new LinkedList<String>();
        expected.add("statement2-1");
        expected.add("statement2-2");
        assertEquals(expected, this.schema.getStatementsForVersion(new Long(2)));
    }
    
    @Test
    public void testGetSteps() {
        Schema schema = this.createSchemaWith10Versions();
        
        assertTrue(schema.getSteps(new Long(10), new Long(10)).isEmpty());
        assertTrue(schema.getSteps(new Long(10), new Long(3)).isEmpty());
        assertEquals(Collections.singletonList(new Long(5)),
                     schema.getSteps(new Long(4), new Long(5)));
        
        List<Long> expected = new LinkedList<Long>();
        expected.add(new Long(3));
        expected.add(new Long(4));
        expected.add(new Long(5));
        expected.add(new Long(6));
        expected.add(new Long(7));
        assertEquals(expected, schema.getSteps(new Long(2), new Long(7)));
    }
    
    @Test
    public void testGetStepsToLatest() {
        Schema schema = this.createSchemaWith10Versions();

        assertTrue(schema.getStepsToLatest(new Long(10)).isEmpty());
        assertEquals(Collections.singletonList(new Long(10)), schema.getStepsToLatest(new Long(9)));

        List<Long> expected = new LinkedList<Long>();
        for (long i = 1; i < 11; i++) {
            expected.add(new Long(i));
        }
        assertEquals(expected, schema.getStepsToLatest(null));
    }
    
    @Test
    public void testGetLatestAvailableVersion() {
        Schema schema = this.createSchemaWith10Versions();
        assertEquals(new Long(10), schema.getLatestAvailableVersion());

        assertEquals(new Long(2), this.schema.getLatestAvailableVersion());
    }

    private Schema createSchemaWith10Versions() {
        Set<Long> availableVersions = new TreeSet<Long>();
        for (long i = 1; i < 11; i++) {
            availableVersions.add(new Long(i));
        }
        return new Schema(new File("."), availableVersions, false, Charset.forName("ISO-8859-1"));
    }

}
