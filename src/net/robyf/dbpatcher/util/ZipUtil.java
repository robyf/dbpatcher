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

    public static void extract(final File inputFile, final File outDir) {
        try {
            ZipFile zipFile = new ZipFile(inputFile);

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

            zipFile.close();
        } catch (IOException ioe) {
            throw new UtilException("Error extracting a zip file", ioe);
        }

    }

}
