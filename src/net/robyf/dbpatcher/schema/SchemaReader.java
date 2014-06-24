/*
 * Copyright 2014 Roberto Fasciolo
 * 
 * This file is part of dbpatcher.
 * 
 * dbpatcher is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * dbpatcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with dbpatcher; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.robyf.dbpatcher.schema;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.TreeSet;

import net.robyf.dbpatcher.LogFactory;
import net.robyf.dbpatcher.util.DirUtil;
import net.robyf.dbpatcher.util.ZipUtil;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
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
        
        Set<VersionDir> versions = new TreeSet<VersionDir>();
        boolean foundVersionDirectory = false;
        for (File child : children) {
            if (child.isDirectory()) {
                try {
                    String name = child.getName();
                    if (name.indexOf("-") != -1) {
                        name = name.substring(0, name.indexOf("-"));
                    }
                    versions.add(new VersionDir(Long.valueOf(name), child.getName()));
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
