package net.robyf.dbpatcher.schema;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.TreeSet;

import net.robyf.dbpatcher.LogFactory;
import net.robyf.dbpatcher.util.DirUtil;
import net.robyf.dbpatcher.util.ZipUtil;


public final class SchemaReader {

    private SchemaReader() {
    }
    
    public static Schema read(final File schemaRoot, final Charset charset) {
        if (!schemaRoot.exists()) {
            throw new SchemaException("Unexistant schema directory: " + schemaRoot.getName());
        }
        File schemaDir;
        boolean needsCleanup;
        if (!schemaRoot.isDirectory()) {
            schemaDir = DirUtil.createTempDirectory();
            ZipUtil.extract(schemaRoot, schemaDir);
            needsCleanup = true;
        } else {
            schemaDir = schemaRoot;
            needsCleanup = false;
        }
        
        File[] children = schemaDir.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(final File dir, final String name) {
                return !name.startsWith(".");
            }

        });
        if (children.length == 0) {
            throw new SchemaException(schemaRoot.getName() + " is empty");
        }
        
        Set<Long> versions = new TreeSet<Long>();
        boolean foundVersionDirectory = false;
        for (File child : children) {
            if (child.isDirectory()) {
                try {
                    versions.add(Long.valueOf(child.getName()));
                    foundVersionDirectory = true;
                    
                    File[] versionScripts = child.listFiles(new FilenameFilter() {
                        
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.endsWith(".sql");
                        }

                    });
                    if (versionScripts.length == 0) {
                        throw new SchemaException("Version " + child.getName()
                                                  + " doesn't contain any sql script");
                    }
                } catch (NumberFormatException nfe) {
                }
            }
        }
        if (!foundVersionDirectory) {
            throw new SchemaException(schemaRoot.getName()
                                      + " does not contain version directories");
        }
        
        LogFactory.getLog().log("Found versions: " + versions.toString());
        
        return new Schema(schemaDir, versions, needsCleanup, charset);
    }

}
