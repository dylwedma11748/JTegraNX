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
package ui.fx;

import configs.Config;
import configs.ConfigManager;
import handlers.PayloadHandler;
import handlers.UpdateHandler;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import rcm.DriverInstaller;
import ui.UIGlobal;
import util.GlobalSettings;
import util.SDPrepare;
import util.Tray;

public class MainUIController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private TextField payloadPath;

    @FXML
    private ImageView rcmStatus;

    @FXML
    private TextArea log;

    @FXML
    private ComboBox configList;

    @FXML
    private TextField configName;

    @FXML
    private Menu payloadMenu;

    @FXML
    private CheckMenuItem autoInjectItem;

    @FXML
    private CheckMenuItem autoCheckJTegraNXUpdates;

    @FXML
    private CheckMenuItem autoCheckPayloadUpdates;

    @FXML
    private CheckMenuItem enableTrayIcon;

    @FXML
    private CheckMenuItem minimizeToTray;

    @FXML
    private CheckMenuItem includeFuseePrimary;

    @FXML
    private CheckMenuItem includehekate;

    @FXML
    private CheckMenuItem includeLockpickRCM;

    @FXML
    private CheckMenuItem includeTegraExplorer;

    @FXML
    private Button inject;

    @FXML
    private Button loadConfig;

    @FXML
    private MenuItem installDriver;

    double xOffset;
    double yOffset;

    int configListVisible = 0;
    int configNameTextFieldVisible = 0;

    @FXML
    private void close() {
        JTegraNX.getStage().close();
        UIGlobal.closeJTegraNX();
    }

    @FXML
    private void minimize() {
        if (GlobalSettings.minimizeToTray) {
            JTegraNX.getStage().setIconified(true);
            JTegraNX.getStage().hide();
        } else {
            JTegraNX.getStage().setIconified(true);
        }
    }

    @FXML
    private void browseForPayload() {
        FileChooser chooser = new FileChooser();

        if (GlobalSettings.savedFolderPath != null) {
            if (new File(GlobalSettings.savedFolderPath).exists()) {
                chooser.setInitialDirectory(new File(GlobalSettings.savedFolderPath));
            } else {
                chooser.setInitialDirectory(GlobalSettings.JTEGRANX_PAYLOAD_DIR);
            }
        } else {
            chooser.setInitialDirectory(GlobalSettings.JTEGRANX_PAYLOAD_DIR);
        }

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("RCM payload files (*.bin)", "*bin");
        chooser.getExtensionFilters().add(filter);

        File file = chooser.showOpenDialog(JTegraNX.getStage());

        if (file != null && file.exists()) {
            payloadPath.setText(file.getAbsolutePath());
            GlobalSettings.savedFolderPath = file.getParent();
        }
    }

    @FXML
    private void changeSetting(ActionEvent event) {
        Object source = event.getSource();

        if (source.equals(autoInjectItem)) {
            GlobalSettings.autoInject = autoInjectItem.isSelected();

            if (GlobalSettings.enableTrayIcon) {
                Tray.toggleAutoInjectLabel();
            }
        } else if (source.equals(autoCheckJTegraNXUpdates)) {
            GlobalSettings.checkJTegraNXUpdates = autoCheckJTegraNXUpdates.isSelected();
        } else if (source.equals(autoCheckPayloadUpdates)) {
            GlobalSettings.checkPayloadUpdates = autoCheckPayloadUpdates.isSelected();
        } else if (source.equals(enableTrayIcon)) {
            GlobalSettings.enableTrayIcon = enableTrayIcon.isSelected();

            if (GlobalSettings.enableTrayIcon) {
                Tray.enableTrayIcon();
                minimizeToTray.setDisable(false);

                if (GlobalSettings.minimizeToTray) {
                    minimizeToTray.setSelected(true);
                }
            } else {
                Tray.disableTrayIcon();
                GlobalSettings.minimizeToTray = false;
                minimizeToTray.setSelected(false);
                minimizeToTray.setDisable(true);
            }
        } else if (source.equals(minimizeToTray)) {
            GlobalSettings.minimizeToTray = minimizeToTray.isSelected();
        } else if (source.equals(includeFuseePrimary)) {
            GlobalSettings.includeFuseePrimary = includeFuseePrimary.isSelected();
            PayloadHandler.updatePayloads();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includehekate)) {
            GlobalSettings.includeHekate = includehekate.isSelected();
            PayloadHandler.updatePayloads();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includeLockpickRCM)) {
            GlobalSettings.includeLockpickRCM = includeLockpickRCM.isSelected();
            PayloadHandler.updatePayloads();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includeTegraExplorer)) {
            GlobalSettings.includeTegraExplorer = includeTegraExplorer.isSelected();
            PayloadHandler.updatePayloads();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        }
    }

    @FXML
    private void injectPayload() {
        UIGlobal.injectPayload(payloadPath.getText());
    }

    @FXML
    private void checkForJTegraNXUpdates() {
        UpdateHandler.checkForUpdates();
    }

    @FXML
    private void checkForPayloadUpdates() {
        new Thread() {
            @Override
            public void run() {
                PayloadHandler.updatePayloads();
            }
        }.start();
    }

    @FXML
    private void clearLog() {
        UIGlobal.clearLog();
    }

    @FXML
    private void prepareSDCard() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select SD Card root");
        File dir = chooser.showDialog(JTegraNX.getStage());

        if (dir != null && dir.exists()) {
            SDPrepare.prepareSDCard(dir);
        }
    }

    @FXML
    private void installAPXDriver() {
        int result = DriverInstaller.installDriver();

        if (result == DriverInstaller.CANCELED) {
            UIGlobal.appendLog("Driver installation canceled");
        } else {
            if (!UIGlobal.getRCMStatus().equals("RCM_DETECTED")) {
                GlobalSettings.driverUpdatedThisSession = true;
            }
        }
    }

    @FXML
    private void showConfigList() {
        switch (configListVisible) {
            case 0:
                loadConfig.setLayoutX(92);
                configList.setVisible(true);
                configListVisible = 1;
                break;
            case 1:
                loadConfig.setLayoutX(247);
                configList.setVisible(false);
                configListVisible = 0;
                break;
        }
    }

    @FXML
    private void showSaveConfigTextField() {
        switch (configNameTextFieldVisible) {
            case 0:
                configName.setVisible(true);
                configNameTextFieldVisible = 1;
                break;
            case 1:
                configName.setVisible(false);
                configNameTextFieldVisible = 0;
                break;
        }
    }

    @FXML
    private void saveConfig() {
        Config config = new Config(configName.getText(), payloadPath.getText());
        ConfigManager.addConfig(config);
        ConfigManager.selectConfig(config.getConfigName());
        ConfigManager.updateConfigList();
        configName.clear();
        showSaveConfigTextField();

        if (GlobalSettings.enableTrayIcon) {
            Tray.updateMenuItems();
        }
    }

    @FXML
    private void selectConfig() {
        GlobalSettings.selectedConfig = configList.getSelectionModel().getSelectedItem();
        ConfigManager.selectConfig(GlobalSettings.selectedConfig);
    }

    @FXML
    private void deleteSelectedConfig() {
        if (configList.getSelectionModel().getSelectedItem() != null && !configList.getSelectionModel().getSelectedItem().equals("No configs")) {
            if (payloadPath.getText().equals(ConfigManager.findConfig(configList.getSelectionModel().getSelectedItem()).getPayloadPath())) {
                payloadPath.clear();
            }

            ConfigManager.deleteConfig(ConfigManager.findConfig(configList.getSelectionModel().getSelectedItem()));
            ConfigManager.updateConfigList();
            configList.getSelectionModel().clearSelection();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        }
    }

    private void setFrameDragEvent() {
        pane.setOnMousePressed((MouseEvent event) -> {
            xOffset = JTegraNX.getStage().getX() - event.getScreenX();
            yOffset = JTegraNX.getStage().getY() - event.getScreenY();
        });

        pane.setOnMouseDragged((MouseEvent event) -> {
            JTegraNX.getStage().setX(event.getScreenX() + xOffset);
            JTegraNX.getStage().setY(event.getScreenY() + yOffset);
        });
    }

    public ImageView getRCMImageView() {
        return rcmStatus;
    }

    public TextArea getLog() {
        return log;
    }

    public ComboBox getConfigList() {
        return configList;
    }

    public TextField getPayloadPathField() {
        return payloadPath;
    }

    public Menu getPayloadMenu() {
        return payloadMenu;
    }

    public CheckMenuItem getAutoInjectMenuItem() {
        return autoInjectItem;
    }

    public CheckMenuItem getCheckJTegraNXUpdatesMenuItem() {
        return autoCheckJTegraNXUpdates;
    }

    public CheckMenuItem getCheckPayloadUpdatesMenuItem() {
        return autoCheckPayloadUpdates;
    }

    public CheckMenuItem getEnableTrayIconMenuItem() {
        return enableTrayIcon;
    }

    public CheckMenuItem getMinimizeToTrayItem() {
        return minimizeToTray;
    }

    public CheckMenuItem getIncludeFuseePrimaryMenuItem() {
        return includeFuseePrimary;
    }

    public CheckMenuItem getIncludeHekateMenuItem() {
        return includehekate;
    }

    public CheckMenuItem getIncludeLockpickRCMMenuItem() {
        return includeLockpickRCM;
    }

    public CheckMenuItem getIncludeTegraExplorerItem() {
        return includeTegraExplorer;
    }

    public Button getInjectButton() {
        return inject;
    }

    public MenuItem getInstallAPXDriverMenuItem() {
        return installDriver;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFrameDragEvent();
    }
}
