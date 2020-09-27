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
package jtegranx.fx;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jtegranx.payloads.PayloadManager;
import jtegranx.util.ConfigManager;
import jtegranx.util.Directories;
import jtegranx.util.TegraRCM;
import jtegranx.util.Tray;
import jtegranx.util.Updater;

public class JTegraNX extends Application {

    private static GUIController controller;
    private static Stage guiStage;
    private static Image icon;
    
    private static final Updater updater = new Updater();

    @Override
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void start(Stage stage) throws IOException {
        Directories.initDirectories();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
        Pane base = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(base);

        guiStage = stage;
        stage.setScene(scene);
        stage.setTitle("JTegraNX");
        icon = new Image("/jtegranx/gui/images/icon.png");
        stage.getIcons().add(icon);
        stage.setResizable(false);
        
        PayloadManager.initPayloads();
        ConfigManager.loadMainConfigFile();
        
        TegraRCM.initRCMDeviceListener();
        
        stage.show();
        updater.checkForUpdates();
        ConfigManager.updateConfigList();
    }

    @Override
    public void stop() {
        TegraRCM.closeRCMDeviceListener();
        ConfigManager.saveMainConfigFile();
        Tray.removeTrayIcon();
    }
    
    public static Updater getUpdater() {
        return updater;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void checkForUpdates() {
        updater.checkForUpdates();
    }

    public static GUIController getController() {
        return controller;
    }

    public static Stage getStage() {
        return guiStage;
    }
}
