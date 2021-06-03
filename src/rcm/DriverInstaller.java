/*

JTegraNX - Another RCM payload injector

Copyright (C) 2021 Dylan Wedman

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
package rcm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import util.GlobalSettings;
import handlers.ResourceHandler;

public class DriverInstaller {

    public static final int CANCELED = -2147483648;
    public static final int READY_FOR_USE = 256;

    public static int installDriver() {
        try {
            File driverInstaller;
            
            if (!GlobalSettings.portableMode) {
                driverInstaller = ResourceHandler.rename(ResourceHandler.load("APX-Driver.exe"), GlobalSettings.STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "APX-Driver.exe");
            } else {
                driverInstaller = ResourceHandler.rename(ResourceHandler.load("APX-Driver.exe"), GlobalSettings.PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "APX-Driver.exe");
            }
            
            Process install = Runtime.getRuntime().exec("cmd /c " + driverInstaller.getAbsolutePath());
            int exitCode = install.waitFor();
            FileUtils.forceDelete(driverInstaller);
            return exitCode;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DriverInstaller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }
}
