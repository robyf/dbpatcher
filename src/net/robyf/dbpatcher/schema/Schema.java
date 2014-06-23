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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.robyf.dbpatcher.parser.ScriptParser;
import net.robyf.dbpatcher.util.DirUtil;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class Schema {
    
    private final File schemaRootDirectory;
    private final Set<VersionDir> availableVersionDirs;
    private final Set<Long> availableVersions;
    private final Charset charset;
    
    public Schema(final File schemaRoorDirectory,
                  final Set<VersionDir> availableVersions,
                  final boolean cleanupOnShutdown,
                  final Charset charset) {
        this.schemaRootDirectory = schemaRoorDirectory;
        this.availableVersionDirs = availableVersions;
        this.availableVersions = new TreeSet<Long>();
        for (VersionDir versionDir : availableVersions) {
            this.availableVersions.add(versionDir.getVersion());
        }
        this.charset = charset;
        if (cleanupOnShutdown) {
            Runtime.getRuntime().addShutdownHook(new Hook(this.schemaRootDirectory));
        }
    }
    
    public Set<Long> getAvailableVersions() {
        return Collections.unmodifiableSet(this.availableVersions);
    }
    
    public List<String> getStatementsForVersion(final Long version) {
        if (!this.availableVersions.contains(version)) {
            throw new SchemaException("Version " + version + " not available");
        }
        
        VersionDir versionDir = null;
        for (VersionDir dir : this.availableVersionDirs) {
            if (dir.getVersion().equals(version)) {
                versionDir = dir;
                break;
            }
        }
        
        File[] scriptsArray =
                new File(this.schemaRootDirectory, 
                         versionDir.getDirName()).listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".sql");
            }

        });
        List<File> scripts = Arrays.asList(scriptsArray);
        Collections.sort(scripts, new Comparator<File>() {

            @Override
            public int compare(final File o1, final File o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });

        List<String> statements = new LinkedList<String>();
        for (File script : scripts) {
            statements.addAll(ScriptParser.parse(script, charset));
        }
        return statements;
    }

    public List<Long> getSteps(final Long initialVersion, final Long requiredVersion) {
        List<Long> steps = new LinkedList<Long>();
        
        for (Long version : this.availableVersions) {
            if ((initialVersion == null || version.longValue() > initialVersion.longValue())
                && version.longValue() <= requiredVersion.longValue()) {
                steps.add(version);
            }
        }
        
        return steps;
    }
    
    public List<Long> getStepsToLatest(final Long initialVersion) {
        return this.getSteps(initialVersion, this.getLatestAvailableVersion());
    }

    Long getLatestAvailableVersion() {
        List<Long> versions = new LinkedList<Long>(this.availableVersions);
        return versions.get(versions.size() - 1);
    }
    
    private final class Hook extends Thread {
        
        private final File directory;
        
        public Hook(final File directory) {
            this.directory = directory;
        }
        
        @Override
        public void run() {
            DirUtil.deleteDirectory(this.directory);
        }
        
    }

}
