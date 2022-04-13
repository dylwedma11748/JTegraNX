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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;

import util.GlobalSettings;

public class UpdateHandler {
	private static final String currentVersion = "1.6.9-R2";
	
	private static GHRepository jtegranx;
	private static GHRelease latest_jtegranx;
	private static GHAsset jtegranx_jar;
	
	public static String getCurrentVersion() {
		return currentVersion;
	}
	
	public static void checkForUpdates(Scanner scanner) {
		if (!GlobalSettings.OFFLINE_MODE) {
			if (GlobalSettings.commandLineMode) {
				if (scanner != null) {
					check();
					
					if (!latest_jtegranx.getTagName().equals(currentVersion)) {
						System.out.println("An update for JTegraNX was found, download now?\nCurrent release: JTegraNX v" + currentVersion + "\nLatest release: " + latest_jtegranx.getTagName());
                        System.out.append("> ");
                        String response = scanner.nextLine();

                        if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
                            File running = new File("");

                            try {
                                running = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                            } catch (URISyntaxException ex) {
                            	System.err.println("Failed to get running JVM!");
                            	return;
                            }

                            if (running.getPath().endsWith(".jar")) {
                                boolean updated = downloadUpdate(running);
                                
                                if (updated) {
                                	System.out.println("Update downloaded\nRestart JTegraNX to finish update");
                                }
                            } else {
                                System.out.println("JTegraNX is running from an IDE or a non-JAR build and can't be updated");
                            }
                        } else if (response.equalsIgnoreCase("n")) {
                            System.out.println("Update canceled");
                        } else {
                            System.out.println("Invalid response, canceling update");
                        }
					}
				} else {
					System.err.println("Can't check for updates with a null scanner");
					return;
				}
			} else {
				check();
				
				if (!latest_jtegranx.getTagName().equals(currentVersion)) {
					boolean update = AlertHandler.showConfirmationDialog("JTegraNX", "A new release of JTegraNX has been found. Download now?\n\nCurrent release: JTegraNX v" + currentVersion + "\nLatest release: JTegraNX v" + latest_jtegranx.getTagName(), "");
					
					if (update) {
						File running = new File("");

                        try {
                            running = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                        } catch (URISyntaxException ex) {
                            System.err.println("Failed to get running JVM!");
                            return;
                        }

                        if (running.getPath().endsWith(".jar")) {
                            boolean updated = downloadUpdate(running);
                            
                            if (updated) {
                            	AlertHandler.showInformationMessage("JTegraNX", "Update downloaded", "Restart JTegraNX to finish update");
                            }
                        } else {
                            AlertHandler.showErrorMessage("JTegraNX", "JTegraNX can't be updated.", "JTegraNX is running from an IDE or a non-JAR build.");
                        }
					}
				}
			}
		} else {
			System.out.println("Can't run updater in offline mode.");
		}
	}
	
	private static boolean downloadUpdate(File running) {
        try {
            URL download = new URL(jtegranx_jar.getBrowserDownloadUrl());
            
            try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream((running))) {
                byte[] buffer = new byte[1024];
                int bytes;

                while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytes);
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Failed to download " + jtegranx_jar.getName());
            e.printStackTrace();
            return false;
        }
    }
	
	private static void check() {
		try {
			jtegranx = GlobalSettings.gitHub.getRepository("dylwedma11748/JTegraNX");
			latest_jtegranx = jtegranx.getLatestRelease();
			
			for (GHAsset asset : latest_jtegranx.listAssets()) {
				if (asset.getName().equals("JTegraNX.jar")) {
					jtegranx_jar = asset;
					break;
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to check for JTegraNX updates: IOException happened");
			e.printStackTrace();
			return;
		}
	}
}
