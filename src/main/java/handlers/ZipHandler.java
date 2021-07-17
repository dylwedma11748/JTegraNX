/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2021 Dylan Wedman

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */
package handlers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHandler {

    private static final int BUFFER_SIZE = 4096;

    public static void unzip(String inputFile, String destination) {
        File destDir = new File(destination);

        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try (ZipInputStream zipInput = new ZipInputStream(new FileInputStream(inputFile))) {
            ZipEntry entry = zipInput.getNextEntry();

            while (entry != null) {
                File file = new File(destination, entry.getName());

                if (file.toPath().normalize().startsWith(destDir.toPath())) {
                	if (!entry.isDirectory()) {
                        extractFile(zipInput, file.getAbsolutePath());
                    } else {
                        File dir = new File(file.getAbsolutePath());
                        dir.mkdir();
                    }
                } else {
                	System.err.println("CWE-22 Zip Slip: Bad entry blocked. Stopping entire extraction.");
                	break;
                }

                zipInput.closeEntry();
                entry = zipInput.getNextEntry();
            }

            zipInput.close();
        } catch (IOException ex) {
            Logger.getLogger(ZipHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void extractFile(ZipInputStream zipInput, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytes = new byte[BUFFER_SIZE];
            int read;

            while ((read = zipInput.read(bytes)) != -1) {
                bos.write(bytes, 0, read);
            }
        }
    }
}
