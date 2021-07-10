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

import git.Asset;
import git.GitHandler;
import git.Release;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import ui.UIGlobal;
import ui.fx.JTegraNX;
import util.GlobalSettings;

public class UpdateHandler {

    private static Release jtegranx;
    private static final String currentVersion = "1.6.8";

    public static void checkForUpdates(Scanner scanner) {
        if (GlobalSettings.commandLineMode) {
            if (scanner != null) {
                jtegranx = GitHandler.generateLatestRelease("https://github.com/dylwedma11748/JTegraNX/releases");

                if (!jtegranx.getTag().equals(currentVersion)) {
                    System.out.println("An update for JTegraNX was found, download now?\nCurrent release: JTegraNX v" + currentVersion + "\nLatest release: " + jtegranx.getReleaseName());
                    System.out.append("> ");
                    String response = scanner.nextLine();

                    if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
                        File running = new File("");

                        try {
                            running = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(UpdateHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        boolean updated = false;
                        File jar = null;

                        if (running.getPath().endsWith(".jar")) {
                            for (Asset asset : jtegranx.getAssets()) {
                                if (asset.getAssetName().equals("JTegraNX.jar")) {
                                    jar = GitHandler.downloadAsset(asset, running.getAbsolutePath());

                                    if (jar.exists()) {
                                        updated = true;
                                    }
                                }
                            }

                            if (updated) {
                                System.out.println("JTegraNX has been updated and needs to be restarted for the changes to apply.\nJTegraNX can't be restarted from command line mode.");
                            }
                        } else {
                            System.out.println("JTegraNX is running from an IDE or a non-JAR build and can't be updated using the built-in updater.");
                        }
                    } else if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("n")) {
                        System.out.println("Update canceled");
                    } else {
                        System.out.println("Invalid response, canceling update");
                    }
                }
            } else {
                throw new NullPointerException("Unable to check for JTegraNX updates. Scanner must not be null in command line mode.");
            }
        } else {
            Platform.runLater(() -> {
                jtegranx = GitHandler.generateLatestRelease("https://github.com/dylwedma11748/JTegraNX/releases");

                if (!jtegranx.getTag().equals(currentVersion)) {
                    boolean update = AlertHandler.showConfirmationDialog("JTegraNX updater", "A new release of JTegraNX has been found. Download now?\n\nCurrent release: JTegraNX v" + currentVersion + "\nLatest release: " + jtegranx.getReleaseName(), "");

                    if (update) {
                        File running = new File("");

                        try {
                            running = new File(UpdateHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(UpdateHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        boolean updated = false;
                        File jar = null;

                        if (running.getPath().endsWith(".jar")) {
                            for (Asset asset : jtegranx.getAssets()) {
                                if (asset.getAssetName().equals("JTegraNX.jar")) {
                                    jar = GitHandler.downloadAsset(asset, running.getAbsolutePath());

                                    if (jar.exists()) {
                                        updated = true;
                                    }
                                }
                            }

                            if (updated) {
                                UIGlobal.appendLog("Download complete");
                                boolean restart = AlertHandler.showConfirmationDialog("JTegraNX updater", "JTegraNX has been updated and needs to be restarted for the changes to apply.", "Restart now?");

                                if (restart) {
                                    Platform.runLater(() -> {
                                        UIGlobal.restartJTegraNX();
                                    });
                                } else {
                                    GlobalSettings.restartPending = true;

                                    Platform.runLater(() -> {
                                        JTegraNX.getController().getJTegraNXUpdateMenuItem().setText("Restart JTegraNX to finish update");
                                    });
                                }
                            } else {
                                if (jar == null) {
                                    AlertHandler.showErrorMessage("JTegraNX updater", "A GitHandler error occured.", "Unable to locate valid asset.");
                                }
                            }
                        } else {
                            AlertHandler.showErrorMessage("JTegraNX updater", "JTegraNX can't be updated.", "JTegraNX is running from an IDE or a non-JAR build.");
                        }
                    }
                } else {
                    UIGlobal.appendLog("JTegraNX is up to date");
                }
            });
        }
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }
}
