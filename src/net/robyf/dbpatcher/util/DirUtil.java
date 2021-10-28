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
package net.robyf.dbpatcher.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class DirUtil {

    private DirUtil() {
    }

    /**
     * Creates a temporary directory.
     * 
     * @return the temporary directory
     */
    public static File createTempDirectory() {
        try {
            return Files.createTempDirectory("dbpatcher").toFile();
        } catch (IOException ioe) {
            throw new UtilException("Error creating a temporary directory", ioe);
        }
    }

    /**
     * Deletes a file or recursively delete a directory.
     * 
     * @param entry the file or directory to be deleted
     */
    public static void delete(final File entry) {
        if (!entry.exists()) {
            return;
        }
        
        if (entry.isDirectory()) {
            for (File file: entry.listFiles()) {
                DirUtil.delete(file);
            }
        }

        try {
            Files.delete(entry.toPath());
        } catch (IOException ioe) {
            throw new UtilException("Error deleting: " + entry.getAbsolutePath(), ioe);
        }
    }

}
