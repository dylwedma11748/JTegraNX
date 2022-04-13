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

package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;

import handlers.AlertHandler;
import windows.libusbKInstaller;

public class NativeLoader {
	
	public static boolean loadNatives() {
		String OS = System.getProperty("os.name");
		String ARCH = System.getProperty("os.arch");
		String suffix = "";
		
		if (ARCH.equals("amd64")) {
			ARCH = "x64";
		} else if (ARCH.equals("arm")) {
			System.err.println("JTegraNX isn't supported on ARM!");
			System.exit(-1);
		} else {
			ARCH = "x86";
		}
		
		URL nativeURL = null;
		
		if (OS.contains("Windows")) {
			File libusbk = new File(System.getenv("SystemDrive") + File.separator + "Windows" + File.separator + "System32" + File.separator + "libusbK.dll");

            if (!libusbk.exists()) {
                if (GlobalSettings.commandLineMode) {
                    System.out.println("libusbK was not found on your system.\nlibusbK is required for JTegraNX to launch.\nRunning the included installer will fix this issue.\nInstall now?\nEntering \"No\" will close JTegraNX.");
                    Scanner in = new Scanner(System.in);
                    String answer = in.nextLine();

                    if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                        int result = libusbKInstaller.installLibusbK();
                        
                        if (result != libusbKInstaller.INSTALLED) {
                        	System.exit(0);
                        }
                    } else {
                        System.exit(0);
                    }
                    
                    in.close();
                } else {
                    int install = AlertHandler.showSwingConformationDialog("libusbK missing", "libusbK was not found on your system.\nlibusbK is required for JTegraNX to launch.\nRunning the included installer will fix this issue.\nInstall now?\nSelecting \"No\" will close JTegraNX.", 0);

                    if (install == JOptionPane.YES_OPTION) {
                    	int result = libusbKInstaller.installLibusbK();
                        
                        if (result != libusbKInstaller.INSTALLED) {
                        	System.exit(0);
                        }
                    } else {
                        System.exit(0);
                    }
                }
            }
            
            suffix = ".dll";
			nativeURL = NativeLoader.class.getResource("/native/windows/JTegraNX_" + ARCH + suffix);
		} else if (OS.contains("Linux")) {
			suffix = ".so";
			nativeURL = NativeLoader.class.getResource("/native/linux/JTegraNX_" + ARCH + suffix);
		} else if (OS.contains("Mac OS X")) {
			return true;
		}
		
		if (nativeURL == null) {
			return false;
		}
		
		String protocol = nativeURL.getProtocol();
		File library = null;
		
		if (protocol.equals("file")) {
			try {
				library = new File(nativeURL.toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return false;
			}
		} else if (protocol.equals("jar")) {
			InputStream input;
			
			if (OS.contains("Windows")) {
				input = NativeLoader.class.getResourceAsStream("/native/windows/JTegraNX_" + ARCH + suffix);
			} else {
				input = NativeLoader.class.getResourceAsStream("/native/linux/JTegraNX_" + ARCH + suffix);
			}
			
			if (input == null) {
				return false;
			}
			
			try {
				File tempDir = File.createTempFile("JTegraNX", null);
				
				if (!tempDir.delete()) {
                    return false;
				}
				
				if (!tempDir.mkdirs()) {
					return false;
				}
				
				library = new File(tempDir, "JTegraNX_" + ARCH + suffix);
				byte[] buffer = new byte[8192];
				FileOutputStream output = new FileOutputStream(library);
				
				while (input.read(buffer) != -1) {
					output.write(buffer);
				}
				
				output.close();
                input.close();
                
                library.deleteOnExit();
                tempDir.deleteOnExit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return false;
		}
		
		System.load(library.getAbsolutePath());
		return true;
	}
}
