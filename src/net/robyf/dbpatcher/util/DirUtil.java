package net.robyf.dbpatcher.util;

import java.io.File;
import java.io.IOException;

public final class DirUtil {

    private DirUtil() {
    }

    public static File createTempDirectory() {
        // Kludge due to the lack of native functionality in java
        try {
            File file = File.createTempFile("dbpatcher", "");
            file.delete();
            file.mkdir();
            return file;
        } catch (IOException ioe) {
            throw new UtilException("Error creating a temporary directory", ioe);
        }
    }

    public static void deleteDirectory(final File dir) {
        if (!dir.exists()) {
            return;
        }

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                DirUtil.deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        dir.delete();
    }

}
