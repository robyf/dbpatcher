package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public final class ZipUtilTest {

    private final File tempDir = DirUtil.createTempDirectory();

    @After
    public void tearDown() {
        DirUtil.deleteDirectory(this.tempDir);
    }

    @Test
    public void testExtract() {
        ZipUtil.extract(new File("config/test/simple02.zip"), this.tempDir);

        File dir1 = new File(this.tempDir, "1");
        assertTrue(dir1.exists());
        assertTrue(dir1.isDirectory());

        File file1 = new File(dir1, "create.sql");
        assertTrue(file1.exists());
        assertTrue(file1.isFile());
        assertEquals(169L, file1.length());

        File dir2 = new File(this.tempDir, "2");
        assertTrue(dir2.exists());
        assertTrue(dir2.isDirectory());

        File file2 = new File(dir2, "01_artists.sql");
        assertTrue(file2.exists());
        assertTrue(file2.isFile());
        assertEquals(227L, file2.length());
    }

    @Test (expected = UtilException.class)
    public void testExtract_missing_file() {
        ZipUtil.extract(new File("kalakukko.zip"), this.tempDir);
    }

}
