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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import jtegranx.fx.JTegraNX;
import static jtegranx.util.ResourceLoader.*;

public class ConfigManager {

    public static File configDir;
    private static File mainConfig;

    private static Dialog dialog;

    public static void loadMainConfigFile() {
        mainConfig = new File(jtegranxdir.getAbsolutePath() + "\\jtegranx.ini");

        if (mainConfig.exists() && configValid(mainConfig)) {
            try (FileReader fr = new FileReader(mainConfig); BufferedReader reader = new BufferedReader(fr)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    switch (line) {
                        case "autoInject=true":
                            JTegraNX.getController().autoInject.setSelected(true);
                            JTegraNX.getController().autoInjectAction();
                            break;
                        case "hideLog=true":
                            JTegraNX.getController().hideLog.setSelected(true);
                            JTegraNX.getController().hideLogAction();
                            break;
                        case "minimizeToTray=true":
                            JTegraNX.getController().minimizeToTray.setSelected(true);
                            JTegraNX.getController().minimizeToTrayAction();
                            break;
                        default:
                            break;
                    }

                    if (line.contains("payloadPath=")) {
                        JTegraNX.getController().setPayloadPath(line.substring(line.indexOf("=") + 1));
                    } else if (line.contains("arguments=")) {
                        JTegraNX.getController().setArguments(line.substring(line.indexOf("=") + 1));
                    } else if (line.contains("savedFolderPath=")) {
                        if (line.length() > 16) {
                            JTegraNX.getController().setSavedFolderPath(line.substring(line.indexOf("=") + 1));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            saveMainConfigFile();
        }
    }

    public static void saveMainConfigFile() {
        try (PrintWriter writer = new PrintWriter(mainConfig)) {
            writer.println("[JTegraNX Main Config]");
            writer.println("autoInject=" + JTegraNX.getController().autoInjectEnabled());
            writer.println("hideLog=" + JTegraNX.getController().hideLog.isSelected());
            writer.println("minimizeToTray=" + JTegraNX.getController().minimizeToTray.isSelected());
            writer.println("payloadPath=" + JTegraNX.getController().getPayloadPath());
            writer.println("arguments=" + JTegraNX.getController().getArguments());
            writer.println("savedFolderPath=" + JTegraNX.getController().getSavedFolderPath());
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveConfig(String name) {
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        if (!configDir.exists()) {
            configDir.mkdir();
        }

        File config = new File(configDir.getAbsolutePath() + "\\Config_" + name.replaceAll(" ", "_") + ".ini");

        if (config.exists()) {
            dialog = new Dialog();
            dialog.setTitle("JTegraNX");
            dialog.setHeaderText("Config \"" + name + "\" already exists. Overwrite?");
            dialog.setGraphic(JTegraNX.getController().getDialogImage());

            ButtonType overwrite = new ButtonType("Overwrite", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(overwrite, ButtonType.CANCEL);

            dialog.initOwner(JTegraNX.getStage());
            dialog.showAndWait();

            if (dialog.getResult().toString().equals("ButtonType [text=Overwrite, buttonData=OK_DONE]")) {
                try (PrintWriter writer = new PrintWriter(config)) {
                    writer.println("[JTegraNX Config]");
                    writer.println("configName=" + name);
                    writer.println("payloadPath=" + JTegraNX.getController().getPayloadPath());
                    writer.println("arguments=" + JTegraNX.getController().getArguments());

                    JTegraNX.getController().appendLog("Config saved!");
                } catch (IOException ex) {
                    Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            try (PrintWriter writer = new PrintWriter(config)) {
                writer.println("[JTegraNX Config]");
                writer.println("configName=" + name);
                writer.println("payloadPath=" + JTegraNX.getController().getPayloadPath());
                writer.println("arguments=" + JTegraNX.getController().getArguments());

                JTegraNX.getController().appendLog("Config saved!");
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
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
                        JTegraNX.getController().setPayloadPath(line.substring(line.indexOf("=") + 1));
                    } else if (line.startsWith("arguments")) {
                        JTegraNX.getController().setArguments(line.substring(line.indexOf("=") + 1));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JTegraNX.getController().appendLog(name + " config doesn't exist!");
        }
    }

    public static void deleteConfig(String name) {
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        File config = new File(configDir.getAbsolutePath() + "\\Config_" + name.replaceAll(" ", "_") + ".ini");

        if (config.exists()) {
            config.delete();
        } else {
            JTegraNX.getController().appendLog(name + " config doesn't exist!");
        }
    }

    public static boolean configValid(File config) {
        try (FileReader fr = new FileReader(config); BufferedReader reader = new BufferedReader(fr)) {
            String line = reader.readLine();

            if (line.equals("[JTegraNX Config]") || line.equals("[JTegraNX Main Config]")) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public static String getConfigName(File config) {
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
        JTegraNX.getController().removeAllConfigsFromList();

        if (Tray.isTrayInitialized()) {
            Tray.clearTrayMenu();
        }

        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");

        if (!configDir.exists()) {
            configDir.mkdir();
        } else {
            File[] files = configDir.listFiles();

            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".ini")) {
                    if (configValid(file)) {
                        JTegraNX.getController().addConfig(getConfigName(file));

                        if (Tray.isTrayInitialized()) {
                            Tray.addConfigToTray(file);
                        }
                    }
                }
            }

            JTegraNX.getController().addConfig("Mount SD Card");

            if (Tray.isTrayInitialized()) {
                Tray.addDefaultMenuItems();
            }
        }
    }

    public static File getConfigDir() {
        return configDir;
    }
}
