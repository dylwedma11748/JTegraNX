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
package windows;

import handlers.ResourceHandler;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class libusbKInstaller {

    public static void installLibusbK() {
        try {
            File installer = ResourceHandler.rename(ResourceHandler.load("/windows/libusbK-3.0.7.0-setup.exe"), System.getProperty("user.dir") + File.separator + "libusbK-3.0.7.0-setup.exe");
            Process install = Runtime.getRuntime().exec("cmd /c \"" + installer.getAbsolutePath() + "\"");
            install.waitFor();
            FileUtils.forceDelete(installer);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(libusbKInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
