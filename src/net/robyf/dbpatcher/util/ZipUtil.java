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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @since 0.9.0
 * @author Roberto Fasciolo
 */
public final class ZipUtil {

    private ZipUtil() {
    }

    private static void copyInputStream(final InputStream in, final OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * Extracts the content of a zip file in a directory in the file system.
     * 
     * @param inputFile The zip file to be extracted
     * @param outDir The target directory
     */
    public static void extract(final File inputFile, final File outDir) {
        try (ZipFile zipFile = new ZipFile(inputFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
                    // This is not robust, just for demonstration purposes.
                    (new File(outDir, entry.getName())).mkdirs();
                } else {
                    ZipUtil.copyInputStream(zipFile.getInputStream(entry),
                                            new BufferedOutputStream(
                                                    new FileOutputStream(
                                                            new File(outDir, entry.getName()))));
                }
            }
        } catch (IOException ioe) {
            throw new UtilException("Error extracting a zip file", ioe);
        }

    }

}
