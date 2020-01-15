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
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import jtegranx.gui.LoadConfigDialog;
import jtegranx.gui.MainGUI;
import static jtegranx.util.ResourceLoader.*;

public class ConfigManager {

    public static File configDir;

    public static void generateSDConfig() {
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        File config = new File(configDir.getAbsolutePath() + "\\Config_Mount_SD_Card.ini");

        if (!config.exists()) {
            try (PrintWriter writer = new PrintWriter(config)) {
                writer.println("[JTegraNX Config]");
                writer.println("configName=Mount SD Card");
                writer.println("payloadPath=jtegranx\\memloader\\memloader_usb.bin");
                writer.println("arguments=-r --dataini=\"jtegranx\\memloader\\ums_sd.ini\"");
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void saveConfig(String name) {
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        File config = new File(configDir.getAbsolutePath() + "\\Config_" + name.replaceAll(" ", "_") + ".ini");

        if (!config.exists()) {
            try (PrintWriter writer = new PrintWriter(config)) {
                writer.println("[JTegraNX Config]");
                writer.println("configName=" + name);
                writer.println("payloadPath=" + MainGUI.PayloadPath.getText());
                writer.println("arguments=" + MainGUI.Arguments.getText());

                MainGUI.Log.append("\nConfig Saved");
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            int choice = JOptionPane.showOptionDialog(null, "Config \"" + name + "\" already exists. Overwrite?", "Save Config", 0, 1, null, null, null);

            if (choice == 0) {
                try (PrintWriter writer = new PrintWriter(config)) {
                    writer.println("[JTegraNX Config]");
                    writer.println("configName=" + name);
                    writer.println("payloadPath=" + MainGUI.PayloadPath.getText());
                    writer.println("arguments=" + MainGUI.Arguments.getText());

                    MainGUI.Log.append("\nConfig Saved");
                } catch (IOException ex) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void loadConfig(String name) {
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        File config = new File(configDir.getAbsolutePath() + "\\Config_" + name.replaceAll(" ", "_") + ".ini");

        if (config.exists()) {
            try (FileReader fr = new FileReader(config); BufferedReader reader = new BufferedReader(fr)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("payloadPath")) {
                        MainGUI.PayloadPath.setText(line.substring(line.indexOf("=") + 1));
                    } else if (line.startsWith("arguments")) {
                        MainGUI.Arguments.setText(line.substring(line.indexOf("=") + 1));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Config \"" + name + "\" doesn't exist", "Load Config", 0);
        }
    }

    private static boolean configValid(File config) {
        try (FileReader fr = new FileReader(config); BufferedReader reader = new BufferedReader(fr)) {
            if (reader.readLine().equals("[JTegraNX Config]")) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private static String getConfigName(File config) {
        try (FileReader fr = new FileReader(config); BufferedReader reader = new BufferedReader(fr)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains("configName=")) {
                    return line.substring(line.indexOf("=") + 1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void updateConfigList() {
        DefaultListModel configList = new DefaultListModel();
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        if (!configDir.exists()) {
            configDir.mkdir();
        } else {
            File[] files = configDir.listFiles();

            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".ini")) {
                    if (configValid(file)) {
                        configList.addElement(getConfigName(file));
                    }
                }
            }
        }

        LoadConfigDialog.ConfigList.setModel(configList);
    }
}
