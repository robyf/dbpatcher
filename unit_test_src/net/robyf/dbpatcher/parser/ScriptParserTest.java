package net.robyf.dbpatcher.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public final class ScriptParserTest {

    @Test
    public void testParsing() {
        List<String> expected = new ArrayList<String>();
        expected.add("create table TEST (     ID      integer,     NAME    varchar(254) )");
        expected.add("alter table TEST add column KALA varchar(30) not null");

        assertEquals(expected,
                new ScriptParser(new File(this.getClass().getResource("testscript.sql").getPath()))
                        .parse());
    }

    @Test
    public void testParsing_different_delimiter() {
        List<String> expected = new ArrayList<String>();
        expected.add("alter table TEST add column KALA varchar(30) not null");

        assertEquals(expected,
                new ScriptParser(new File(this.getClass().getResource("testscript2.sql").getPath()))
                        .parse());
    }

    @Test(expected = ParsingException.class)
    public void testParsing_unexisting_file() {
        new ScriptParser(new File("kala.sql")).parse();
    }

    @Test
    public void testParsing_latin1() {
        List<String> expected = new ArrayList<String>();
        expected.add("test latin1 צהו");

        assertEquals(expected,
                new ScriptParser(
                        new File(this.getClass().getResource("testscript_latin1.sql").getPath()),
                        Charset.forName("ISO-8859-1")).parse());
    }

    @Test
    public void testParsing_utf8() {
        List<String> expected = new ArrayList<String>();
        expected.add("test utf8 צהו");

        assertEquals(expected,
                new ScriptParser(
                        new File(this.getClass().getResource("testscript_utf8.sql").getPath()),
                        Charset.forName("UTF-8")).parse());
    }

}
