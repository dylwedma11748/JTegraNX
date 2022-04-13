/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2022 Dylan Wedman

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceHandler {

    private static InputStream input;
    private static BufferedInputStream bInput;
    private static FileOutputStream fos;
    private static BufferedOutputStream bos;
    private static File output;

    public static File load(String resource) {
        try {
            input = ResourceHandler.class.getResourceAsStream(resource);
            bInput = new BufferedInputStream(input);
            output = File.createTempFile(resource, null);
            fos = new FileOutputStream(output);
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024];

            while (bInput.read(buffer) != -1) {
                bos.write(buffer, 0, 1024);
            }

            bInput.close();
            bos.close();
        } catch (IOException ex) {
            Logger.getLogger(ResourceHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        output.deleteOnExit();
        return output;
    }

    public static File rename(File input, String dest) {
        File file = new File(dest);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        if (input.renameTo(file)) {
            return new File(dest);
        }

        return null;
    }
}
