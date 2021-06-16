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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import ui.UIGlobal;
import util.GlobalSettings;
import util.Tray;

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
            System.loadLibrary("JTegraNX");
            launch();
        } else {
            Platform.runLater(() -> {
                AlertHandler.showErrorMessage("Unsupported Platform", "JTegraNX is only supported on Windows", "Your OS: " + System.getProperty("os.name"));
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
