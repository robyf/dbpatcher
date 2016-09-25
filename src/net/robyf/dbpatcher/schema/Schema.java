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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.robyf.dbpatcher.parser.ScriptParser;
import net.robyf.dbpatcher.util.DirUtil;

/**
 * A schema definition.
 * 
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class Schema {
    
    private final File schemaRootDirectory;
    private final Map<Long, VersionDir> availableVersions;
    private final Charset charset;
    
    /**
     * Constructs an instance of this class.
     * 
     * @param schemaRoorDirectory Root directory for the schema definition in the file system
     * @param availableVersions Available versions
     * @param cleanupOnShutdown True if the schema definition must be removed from the file system
     *                          after executing this tool
     * @param charset Character set used by this schema definition
     */
    public Schema(final File schemaRoorDirectory,
                  final Set<VersionDir> availableVersions,
                  final boolean cleanupOnShutdown,
                  final Charset charset) {
        this.schemaRootDirectory = schemaRoorDirectory;
        this.availableVersions = new TreeMap<>();
        for (VersionDir versionDir : availableVersions) {
            this.availableVersions.put(versionDir.getVersion(), versionDir);
        }
        this.charset = charset;
        if (cleanupOnShutdown) {
            Runtime.getRuntime().addShutdownHook(new Hook(this.schemaRootDirectory));
        }
    }
    
    public Set<Long> getAvailableVersions() {
        return Collections.unmodifiableSet(this.availableVersions.keySet());
    }
    
    /**
     * Returns the list of statements belonging to a schema version
     * 
     * @param version The given version
     * @return The list of statements
     */
    public List<String> getStatementsForVersion(final Long version) {
        VersionDir versionDir = this.availableVersions.get(version);

        if (versionDir == null) {
            throw new SchemaException("Version " + version + " not available");
        }
        
        File[] scriptsArray = versionDir.getDirectory()
                .listFiles((dir, name) -> name.endsWith(".sql"));
        List<File> scripts = Arrays.asList(scriptsArray);
        Collections.sort(scripts, (file1, file2) -> file1.getName().compareTo(file2.getName()));

        List<String> statements = new LinkedList<>();
        for (File script : scripts) {
            statements.addAll(new ScriptParser(script, charset).parse());
        }
        return statements;
    }

    /**
     * Returns the list of steps to be applied for going from one version to another.
     * 
     * @param initialVersion The current version
     * @param requiredVersion The required version
     * @return List of steps
     */
    public List<Long> getSteps(final Long initialVersion, final Long requiredVersion) {
        List<Long> steps = new LinkedList<>();
        
        for (Long version : this.availableVersions.keySet()) {
            if ((initialVersion == null || version.longValue() > initialVersion.longValue())
                && version.longValue() <= requiredVersion.longValue()) {
                steps.add(version);
            }
        }
        
        return steps;
    }
    
    /**
     * Returns the list of steps to be applied for going from one version to the latest available.
     * 
     * @param initialVersion The current version
     * @return List of steps
     */
    public List<Long> getStepsToLatest(final Long initialVersion) {
        return this.getSteps(initialVersion, this.getLatestAvailableVersion());
    }

    Long getLatestAvailableVersion() {
        List<Long> versions = new LinkedList<>(this.availableVersions.keySet());
        return versions.get(versions.size() - 1);
    }
    
    private final class Hook extends Thread {
        
        private final File directory;
        
        Hook(final File directory) {
            this.directory = directory;
        }
        
        @Override
        public void run() {
            DirUtil.delete(this.directory);
        }
        
    }

}
