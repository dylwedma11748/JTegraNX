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
package windows;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import util.GlobalSettings;
import handlers.ResourceHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DriverInstaller {

	public static final int CANCELED = -2147483648;
	public static final int READY_FOR_USE = 256;
	public static final int DEVICE_UPDATED = 1;
	public static final int UAC_CANCEL = 1223;
	
	private static int result;

	public static int installDriver() {
		File installer;
		File outputDir;

		if (!GlobalSettings.portableMode) {
			outputDir = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "Driver");

			if (outputDir.exists()) {
				try {
					FileUtils.deleteDirectory(outputDir);
				} catch (IOException ex) {
					Logger.getLogger(DriverInstaller.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		} else {
			outputDir = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "Driver");

			if (outputDir.exists()) {
				try {
					FileUtils.deleteDirectory(outputDir);
				} catch (IOException ex) {
					Logger.getLogger(DriverInstaller.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		
		//ProgressAlert alert = AlertHandler.createProgressAlert("JTegraNX", "Extracting");
    	//alert.show();
    	
		installer = ResourceHandler.rename(ResourceHandler.load("/windows/APX-Driver.exe"), outputDir + File.separator + "APX-Driver.exe");
		//alert.setHeaderText("Running installer");
		
		result = runInstaller(installer, outputDir);
		
		//alert.close();
		
		return result;
	}
	
	private static int runInstaller(File installer, File outputDir) {
		int exitCode = 0;
		
		try {
			Process install = Runtime.getRuntime().exec("cmd /c " + installer.getAbsolutePath());
			exitCode = install.waitFor();

			InputStreamReader reader = new InputStreamReader(install.getErrorStream());
			BufferedReader bReader = new BufferedReader(reader);
			String line;

			while ((line = bReader.readLine()) != null) {
				if (line.equals("Access is denied.")) {
					FileUtils.deleteDirectory(outputDir);
					return UAC_CANCEL;
				}
			}

			FileUtils.deleteDirectory(outputDir);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return exitCode;
	}
}
