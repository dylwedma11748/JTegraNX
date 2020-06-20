/*

JTegraNX - Another GUI for TegraRcmSmash

Copyright (C) 2020 Dylan Wedman

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
package jtegranx.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import jtegranx.fx.JTegraNX;

public class Updater {

    public Dialog updaterDialog;
    private final String currentVersion = "v1.4";

    public void checkForUpdates() {
        new Thread() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        System.out.println("Checking for JTegraNX updates");
                        String git = "https://github.com/dylwedma11748/JTegraNX/releases";
                        URL github = new URL(git);
                        URLConnection connection = github.openConnection();

                        try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                            String line;

                            while ((line = bReader.readLine()) != null) {
                                if (line.contains("<a href=\"/dylwedma11748/JTegraNX/releases/tag/")) {
                                    String latestVersion = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
                                    String versionNumber = latestVersion.substring(latestVersion.indexOf("v") + 1);

                                    float latestVersionFloat = Float.valueOf(versionNumber);
                                    float currentVersionFloat = Float.valueOf(currentVersion.substring(currentVersion.indexOf("v") + 1));

                                    if (currentVersionFloat < latestVersionFloat) {
                                        updaterDialog = new Dialog();
                                        updaterDialog.setTitle("JTegraNX");
                                        updaterDialog.setHeaderText("A new update has been released. Download now?");
                                        updaterDialog.setGraphic(JTegraNX.getController().getDialogImage());

                                        ButtonType download = new ButtonType("Download", ButtonBar.ButtonData.OK_DONE);
                                        updaterDialog.getDialogPane().getButtonTypes().addAll(download, ButtonType.CANCEL);
                                        updaterDialog.initOwner(JTegraNX.getStage());
                                        updaterDialog.showAndWait();

                                        if (updaterDialog.getResult().toString().equals("ButtonType [text=Download, buttonData=OK_DONE]")) {
                                            FileChooser chooser = new FileChooser();

                                            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Jar files (*.jar)", "*jar");
                                            chooser.getExtensionFilters().add(filter);

                                            File file = chooser.showSaveDialog(JTegraNX.getStage());

                                            if (file != null) {
                                                String filePath = file.getAbsolutePath();

                                                if (!filePath.endsWith(".jar")) {
                                                    filePath = filePath.concat(".jar");
                                                }

                                                String downloadURLString = git + "/download/" + latestVersionFloat + "/JTegraNX.jar";
                                                URL downloadURL = new URL(downloadURLString);
                                                Downloader.downloadFile(downloadURL, filePath, false);
                                            } else {
                                                JTegraNX.getController().appendLog("Update canceled");
                                                break;
                                            }
                                        } else {
                                            JTegraNX.getController().appendLog("Update canceled");
                                            break;
                                        }
                                    } else if (latestVersionFloat < currentVersionFloat || currentVersionFloat == latestVersionFloat) {
                                        System.out.println("JTegraNX is up to date.");
                                        JTegraNX.getController().appendLog("No updates found");
                                        break;
                                    }

                                    break;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        }.start();
    }
}
