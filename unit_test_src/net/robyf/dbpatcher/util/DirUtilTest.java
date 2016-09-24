package net.robyf.dbpatcher.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public final class DirUtilTest {
    
    private File dir = null;
    
    @After
    public void tearDown() {
        if (this.dir != null) {
            DirUtil.delete(this.dir);
        }
    }

    @Test
    public void testCreateTempDirectory() {
        this.dir = DirUtil.createTempDirectory();
        assertTrue(this.dir.exists());
        assertTrue(this.dir.isDirectory());
    }
    
    @Test
    public void testDelete() {
        this.dir = DirUtil.createTempDirectory();
        ZipUtil.extract(new File("config/test/simple02.zip"), this.dir);
        DirUtil.delete(this.dir);
        assertFalse(this.dir.exists());
    }

}
