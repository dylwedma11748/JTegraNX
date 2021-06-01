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
package ui;

import configs.Config;
import configs.ConfigManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import handlers.PayloadHandler;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import rcm.RCM;
import ui.fx.JTegraNX;
import util.GlobalSettings;
import util.Tray;

public class UIGlobal {

    private static String rcm_status = "NO_STATUS";
    private static String previous_rcm_status = "NO_STATUS";

    private static final Image RCM_DETECTED = new Image("/ui/images/rcm_detected.png");
    private static final Image RCM_UNDETECTED = new Image("/ui/images/rcm_undetected.png");
    private static final Image RCM_LOADING = new Image("/ui/images/loading.png");
    private static final Image RCM_LOADED = new Image("/ui/images/loaded.png");
    private static final Image DRIVER_MISSING = new Image("/ui/images/driver.png");
    private static final Image ERROR = new Image("/ui/images/error.png");

    private static Alert deviceAlert;

    public static void startDeviceListener() {
        new Thread("Device listener") {
            @Override
            public void run() {
                RCM.startDeviceListener();
            }
        }.start();
    }

    public static void injectPayload(String payloadPath) {
        if (rcm_status.equals("RCM_DETECTED")) {
            clearLog();
            RCM.injectPayload(payloadPath);
        }
    }

    public static void setRCMStatus(String status) {
        previous_rcm_status = rcm_status;
        rcm_status = status;

        switch (rcm_status) {
            case "RCM_DETECTED":
                JTegraNX.getController().getRCMImageView().setImage(RCM_DETECTED);
                JTegraNX.getController().getInjectButton().setDisable(false);

                if (GlobalSettings.autoInject) {
                    injectPayload(JTegraNX.getController().getPayloadPathField().getText());
                }

                if (previous_rcm_status.equals("RCM_UNDETECTED") && GlobalSettings.driverUpdatedThisSession) {
                    RCM.promptDeviceReconnect();
                    GlobalSettings.driverUpdatedThisSession = false;
                }

                if (previous_rcm_status.equals("DRIVER_MISSING")) {
                    RCM.promptDeviceReconnect();
                    GlobalSettings.driverUpdatedThisSession = false;
                }

                if (previous_rcm_status.equals("RCM_UNDETECTED") && deviceAlert.isShowing()) {
                    Platform.runLater(() -> {
                        deviceAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        deviceAlert.close();
                    });
                }

                break;

            case "RCM_UNDETECTED":
                JTegraNX.getController().getRCMImageView().setImage(RCM_UNDETECTED);
                JTegraNX.getController().getInjectButton().setDisable(true);
                break;

            case "RCM_LOADING":
                JTegraNX.getController().getRCMImageView().setImage(RCM_LOADING);
                break;

            case "RCM_LOADED":
                JTegraNX.getController().getRCMImageView().setImage(RCM_LOADED);
                break;

            case "DRIVER_MISSING":
                JTegraNX.getController().getRCMImageView().setImage(DRIVER_MISSING);
                JTegraNX.getController().getInjectButton().setDisable(true);
                RCM.promptDriverInstall();
                break;

            case "ERROR":
                JTegraNX.getController().getRCMImageView().setImage(ERROR);
                break;
        }
    }

    public static String getRCMStatus() {
        return rcm_status;
    }

    public static String getPreviousRCMStatus() {
        return previous_rcm_status;
    }

    public static void setDeviceAlert(Alert alert) {
        deviceAlert = alert;
    }

    public static Alert getDeviceAlert() {
        return deviceAlert;
    }

    public static void appendLog(String line) {
        JTegraNX.getController().getLog().appendText(line + "\n");
    }

    public static void clearLog() {
        JTegraNX.getController().getLog().clear();
    }

    public static void clearPayloadMenu() {
        JTegraNX.getController().getPayloadMenu().getItems().removeAll(JTegraNX.getController().getPayloadMenu().getItems());
    }

    public static void closeJTegraNX() {
        GlobalSettings.savedPayloadPath = JTegraNX.getController().getPayloadPathField().getText();

        if (GlobalSettings.enableTrayIcon) {
            Tray.disableTrayIcon();
        }

        saveMainConfigFile();
        Platform.exit();
        System.exit(0);
    }

    public static void applyGlobalSettings() {
        ConfigManager.selectConfig(GlobalSettings.selectedConfig);

        JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.savedPayloadPath);
        JTegraNX.getController().getAutoInjectMenuItem().setSelected(GlobalSettings.autoInject);
        JTegraNX.getController().getCheckJTegraNXUpdatesMenuItem().setSelected(GlobalSettings.checkJTegraNXUpdates);
        JTegraNX.getController().getCheckPayloadUpdatesMenuItem().setSelected(GlobalSettings.checkPayloadUpdates);
        JTegraNX.getController().getEnableTrayIconMenuItem().setSelected(GlobalSettings.enableTrayIcon);
        JTegraNX.getController().getIncludeFuseePrimaryMenuItem().setSelected(GlobalSettings.includeFuseePrimary);
        JTegraNX.getController().getIncludeHekateMenuItem().setSelected(GlobalSettings.includeHekate);
        JTegraNX.getController().getIncludeLockpickRCMMenuItem().setSelected(GlobalSettings.includeLockpickRCM);
        JTegraNX.getController().getIncludeTegraExplorerItem().setSelected(GlobalSettings.includeTegraExplorer);

        if (!GlobalSettings.selectedConfig.equals("null")) {
            Platform.runLater(() -> {
                JTegraNX.getController().getConfigList().getSelectionModel().select(GlobalSettings.selectedConfig);
            });
        }

        if (GlobalSettings.enableTrayIcon) {
            JTegraNX.getController().getMinimizeToTrayItem().setDisable(false);
            JTegraNX.getController().getMinimizeToTrayItem().setSelected(GlobalSettings.minimizeToTray);
        }
    }

    public static boolean readMainConfigFile() {
        if (GlobalSettings.JTEGRANX_CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(GlobalSettings.JTEGRANX_CONFIG_FILE); BufferedReader bReader = new BufferedReader(reader)) {
                String line;

                while ((line = bReader.readLine()) != null) {
                    if (line.contains("savedFolderPath")) {
                        GlobalSettings.savedFolderPath = line.substring(line.indexOf("=") + 1);
                    }

                    if (line.contains("savedPayloadPath")) {
                        GlobalSettings.savedPayloadPath = line.substring(line.indexOf("=") + 1);
                    }

                    if (line.contains("selectedConfig")) {
                        GlobalSettings.selectedConfig = line.substring(line.indexOf("=") + 1);
                    }

                    if (line.contains("autoInject")) {
                        GlobalSettings.autoInject = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("checkJTegraNXUpdates")) {
                        GlobalSettings.checkJTegraNXUpdates = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("checkPayloadUpdates")) {
                        GlobalSettings.checkPayloadUpdates = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("enableTrayIcon")) {
                        GlobalSettings.enableTrayIcon = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("includeFussePrimary")) {
                        GlobalSettings.includeFuseePrimary = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("includeHekate")) {
                        GlobalSettings.includeHekate = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("includeLockpickRCM")) {
                        GlobalSettings.includeLockpickRCM = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("includeTegraExplorer")) {
                        GlobalSettings.includeTegraExplorer = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("minimizeToTray")) {
                        GlobalSettings.minimizeToTray = Boolean.valueOf(line.substring(line.indexOf("=") + 1));
                    }

                    if (line.contains("[PAYLOAD RELEASE INFO]")) {
                        if (GlobalSettings.includeFuseePrimary) {
                            line = bReader.readLine();
                            GlobalSettings.fuseePrimaryTag = line.substring(line.indexOf("=") + 1);
                        }

                        if (GlobalSettings.includeHekate) {
                            line = bReader.readLine();
                            GlobalSettings.hekateTag = line.substring(line.indexOf("=") + 1);
                        }

                        if (GlobalSettings.includeLockpickRCM) {
                            line = bReader.readLine();
                            GlobalSettings.lockpickRCMTag = line.substring(line.indexOf("=") + 1);
                        }

                        if (GlobalSettings.includeTegraExplorer) {
                            line = bReader.readLine();
                            GlobalSettings.tegraExplorerTag = line.substring(line.indexOf("=") + 1);
                        }
                    }

                    if (line.contains("[JTEGRANX CONFIG]")) {
                        String configNameLine = bReader.readLine();
                        String configName = configNameLine.substring(configNameLine.indexOf("=") + 1);
                        String payloadPathLine = bReader.readLine();
                        String payloadPath = payloadPathLine.substring(payloadPathLine.indexOf("=") + 1);
                        Config config = new Config(configName, payloadPath);
                        ConfigManager.addConfig(config);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(UIGlobal.class.getName()).log(Level.SEVERE, null, ex);
            }

            return true;
        }

        return false;
    }

    public static void saveMainConfigFile() {
        if (!GlobalSettings.JTEGRANX_DIR.exists()) {
            GlobalSettings.JTEGRANX_DIR.mkdir();
        }

        try (PrintWriter writer = new PrintWriter(GlobalSettings.JTEGRANX_CONFIG_FILE)) {
            writer.println("[SETTINGS]");
            writer.println("savedFolderPath=" + GlobalSettings.savedFolderPath);
            writer.println("savedPayloadPath=" + GlobalSettings.savedPayloadPath);
            writer.println("selectedConfig=" + GlobalSettings.selectedConfig);
            writer.println("autoInject=" + GlobalSettings.autoInject);
            writer.println("checkJTegraNXUpdates=" + GlobalSettings.checkJTegraNXUpdates);
            writer.println("checkPayloadUpdates=" + GlobalSettings.checkPayloadUpdates);
            writer.println("enableTrayIcon=" + GlobalSettings.enableTrayIcon);
            writer.println("includeFussePrimary=" + GlobalSettings.includeFuseePrimary);
            writer.println("includeHekate=" + GlobalSettings.includeHekate);
            writer.println("includeLockpickRCM=" + GlobalSettings.includeLockpickRCM);
            writer.println("includeTegraExplorer=" + GlobalSettings.includeTegraExplorer);
            writer.println("minimizeToTray=" + GlobalSettings.minimizeToTray);
            writer.println();
            writer.println(PayloadHandler.getPayloadInfoAsString());
            writer.println(ConfigManager.getConfigs());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UIGlobal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
