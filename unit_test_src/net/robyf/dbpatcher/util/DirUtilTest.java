package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.robyf.dbpatcher.util.DirUtil;
import net.robyf.dbpatcher.util.ZipUtil;

import org.junit.After;
import org.junit.Test;

public final class DirUtilTest {
    
    private File dir = null;
    
    @After
    public void tearDown() {
        if (this.dir != null) {
            DirUtil.deleteDirectory(this.dir);
        }
    }

    @Test
    public void testCreateTempDirectory() {
        this.dir = DirUtil.createTempDirectory();
        assertTrue(this.dir.exists());
        assertTrue(this.dir.isDirectory());
    }
    
    @Test
    public void testDeleteDirectory() {
        this.dir = DirUtil.createTempDirectory();
        ZipUtil.extract(new File("config/test/simple02.zip"), this.dir);
        DirUtil.deleteDirectory(this.dir);
        assertFalse(this.dir.exists());
    }

}
