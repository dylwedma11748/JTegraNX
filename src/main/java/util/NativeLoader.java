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
			System.out.println("Mac OS X doesn't need a native");
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
