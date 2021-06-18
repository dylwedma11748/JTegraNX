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
package ui.fx;

import configs.ConfigManager;
import handlers.AlertHandler;
import handlers.UpdateHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import handlers.PayloadHandler;
import handlers.ResourceHandler;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javax.swing.JOptionPane;
import ui.UIGlobal;
import util.GlobalSettings;
import util.Tray;
import windows.libusbKInstaller;

public class JTegraNX extends Application {

    private static MainUIController controller;
    private static Stage stage;
    private static Image icon;

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainUI.fxml"));
        Pane base = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(base);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("JTegraNX");
        icon = new Image("/ui/images/icon.png");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        JTegraNX.stage = stage;

        boolean configFileFoundAndRead = false;

        if (UIGlobal.readMainConfigFile()) {
            configFileFoundAndRead = true;
            UIGlobal.applyGlobalSettings();
            UIGlobal.startDeviceListener();

            if (GlobalSettings.enableTrayIcon) {
                Tray.enableTrayIcon();
            }

            if (GlobalSettings.checkPayloadUpdates) {
                PayloadHandler.updatePayloads();
            } else {
                PayloadHandler.addSelectedPayloadsToMenu();
            }

            if (GlobalSettings.checkJTegraNXUpdates) {
                UpdateHandler.checkForUpdates();
            }

            ConfigManager.updateConfigList();
        }

        controller.getPayloadPathField().textProperty().addListener((observable, oldValue, newValue) -> {
            UIGlobal.checkIfSpecifiedPayloadExists();
            
            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        });

        stage.show();
        
        controller.configName.getParent().requestFocus();
        
        if (!System.getProperty("os.name").contains("Windows")) {
            controller.getInstallAPXDriverMenuItem().setDisable(true);
            controller.getInstallAPXDriverMenuItem().setVisible(false);
            
            if (!System.getProperty("user.name").equals("root")) {
                UIGlobal.appendLog("Linux support requires either root access or udev rules configured");
                AlertHandler.showInformationMessage("JTegraNX on Linux", "Linux support requires either root access or udev rules configured", "You are currently running as " + System.getProperty("user.name"));
            }
        }

        if (configFileFoundAndRead) {
            if (GlobalSettings.portableMode) {
                UIGlobal.appendLog("Using portable mode");
                controller.getPortableModeMenuItem().setSelected(true);
                controller.getStandardModeMenuItem().setSelected(false);
            } else {
                UIGlobal.appendLog("Using standard mode");
                controller.getPortableModeMenuItem().setSelected(false);
                controller.getStandardModeMenuItem().setSelected(true);
            }
        } else {
            List<String> choices = new ArrayList<>();
            choices.add("Standard mode");
            choices.add("Portable mode");
            String mode = AlertHandler.createChoiceDialog("JTegraNX", "Choose configuration mode", "Mode: ", choices);

            if (mode != null && mode.equals("Portable mode")) {
                GlobalSettings.portableMode = true;
                UIGlobal.appendLog("Using portable mode");
                controller.getPortableModeMenuItem().setSelected(true);
                controller.getStandardModeMenuItem().setSelected(false);
            } else {
                GlobalSettings.portableMode = false;
                UIGlobal.appendLog("Using standard mode");
                controller.getPortableModeMenuItem().setSelected(false);
                controller.getStandardModeMenuItem().setSelected(true);
            }

            PayloadHandler.updatePayloads();
            UpdateHandler.checkForUpdates();
            UIGlobal.startDeviceListener();
            ConfigManager.updateConfigList();
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JTegraNX.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (System.getProperty("os.name").contains("Windows")) {
            File libusbk = new File(System.getenv("SystemDrive") + File.separator + "Windows" + File.separator + "System32" + File.separator + "libusbK.dll");
            
            if (!libusbk.exists()) {
                int install = AlertHandler.showSwingConformationDialog("libusbK missing", "libusbK was not found on your system.\nlibusbK is required for JTegraNX to launch.\nRunning the included installer will fix this issue.\nInstall now?\nSelecting \"No\" will close JTegraNX.", 0);
                
                if (install == JOptionPane.YES_OPTION) {
                    libusbKInstaller.installLibusbK();
                } else {
                    System.exit(0);
                }
            }
            
            File library;
            
            if (GlobalSettings.JRE_ARCH.equals("64")) {
                library = ResourceHandler.load("/windows/natives/JTegraNX_x64.dll");
            } else {
                library = ResourceHandler.load("/windows/natives/JTegraNX_x86.dll");
            }
            
            library.deleteOnExit();
            System.load(library.getAbsolutePath());
            launch();
        } else if (System.getProperty("os.name").contains("Linux")) {
            File library;
            
            if (GlobalSettings.JRE_ARCH.equals("64")) {
                library = ResourceHandler.load("/linux/natives/JTegraNX_x64.so");
            } else {
                library = ResourceHandler.load("/linux/natives/JTegraNX_x86.so");
            }
            
            library.deleteOnExit();
            System.load(library.getAbsolutePath());
            launch();
        } else {
            Platform.runLater(() -> {
                AlertHandler.showErrorMessage("Unsupported Platform", "JTegraNX is only supported on Windows and Linux", "Your OS: " + System.getProperty("os.name"));
            });
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static MainUIController getController() {
        return controller;
    }
}
