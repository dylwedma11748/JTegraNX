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

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFileChooser;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtegranx.gui.MainGUI;

public class Updater {

    public static void checkForUpdates(Component parent) {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Checking for JTegraNX updates");
                    MainGUI.Log.append("Checking for updates");
                    String git = "https://github.com/dylwedma11748/JTegraNX/releases";
                    URL github = new URL(git);
                    URLConnection connection = github.openConnection();

                    try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                        String line;

                        while ((line = bReader.readLine()) != null) {
                            if (line.contains("<a href=\"/dylwedma11748/JTegraNX/releases/tag/")) {
                                String latestVersion = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
                                String versionNumber = latestVersion.substring(latestVersion.indexOf("v") + 1);
                                String currentVersion = MainGUI.VersionLabel.getText();

                                float latestVersionFloat = Float.valueOf(versionNumber);
                                float currentVersionFloat = Float.valueOf(currentVersion.substring(currentVersion.indexOf("v") + 1));

                                if (currentVersionFloat < latestVersionFloat) {
                                    MainGUI.Log.append("\nUpdate detected");
                                    int downloadUpdate = showOptionDialog(parent, "Latest Version: " + latestVersionFloat + "\nDownload now?", "New update available", YES_NO_OPTION, INFORMATION_MESSAGE, null, null, null);

                                    if (downloadUpdate == YES_OPTION) {
                                        FileFilter jar = new FileNameExtensionFilter("Jar files", "jar");

                                        JFileChooser chooser = new JFileChooser();
                                        chooser.setDialogTitle("Save file as");
                                        chooser.setAcceptAllFileFilterUsed(false);
                                        chooser.addChoosableFileFilter(jar);
                                        chooser.setMultiSelectionEnabled(false);
                                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                                        int returnValue = chooser.showSaveDialog(parent);

                                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                                            String filePath = chooser.getSelectedFile().getAbsolutePath();

                                            if (!filePath.endsWith(".jar")) {
                                                filePath = filePath.concat(".jar");
                                            }

                                            String downloadURL = git + "/download/" + latestVersionFloat + "/JTegraNX.jar";
                                            URL download = new URL(downloadURL);
                                            Downloader.downloadFile(download, filePath, false);
                                        } else {
                                            MainGUI.Log.append("\nUpdate canceled");
                                            MainGUI.Inject.setEnabled(true);
                                            MainGUI.SaveConfig.setEnabled(true);
                                            MainGUI.LoadConfig.setEnabled(true);
                                            MainGUI.Reset.setEnabled(true);
                                            break;
                                        }
                                    } else {
                                        MainGUI.Log.append("\nUpdate canceled");
                                        MainGUI.Inject.setEnabled(true);
                                        MainGUI.SaveConfig.setEnabled(true);
                                        MainGUI.LoadConfig.setEnabled(true);
                                        MainGUI.Reset.setEnabled(true);
                                        break;
                                    }
                                } else if (latestVersionFloat < currentVersionFloat || currentVersionFloat == latestVersionFloat) {
                                    System.out.println("JTegraNX is up to date.");
                                    MainGUI.Log.append("\nNo updates found");
                                    MainGUI.Inject.setEnabled(true);
                                    MainGUI.SaveConfig.setEnabled(true);
                                    MainGUI.LoadConfig.setEnabled(true);
                                    MainGUI.Reset.setEnabled(true);
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Unable to check for updates on JTegraNX. Reason: " + ex.getClass().getName() + " was thrown!");
                    MainGUI.Log.append("\n" + ex.getClass().getName() + " was thrown!");
                    MainGUI.Log.append("\nUnable to check for updates");
                    MainGUI.Inject.setEnabled(true);
                    MainGUI.SaveConfig.setEnabled(true);
                    MainGUI.LoadConfig.setEnabled(true);
                    MainGUI.Reset.setEnabled(true);
                }
            }
        }.start();
    }
}
