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

import net.robyf.dbpatcher.parser.ScriptParser;
import net.robyf.dbpatcher.util.DirUtil;



public final class Schema {
    
    private final File schemaRootDirectory;
    private final Set<Long> availableVersions;
    private final Charset charset;
    
    public Schema(final File schemaRoorDirectory,
                  final Set<Long> availableVersions,
                  final boolean cleanupOnShutdown,
                  final Charset charset) {
        this.schemaRootDirectory = schemaRoorDirectory;
        this.availableVersions = availableVersions;
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
        
        File[] scriptsArray =
                new File(this.schemaRootDirectory, 
                         version.toString()).listFiles(new FilenameFilter() {
            
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
