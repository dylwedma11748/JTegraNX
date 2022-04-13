/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2022 Dylan Wedman

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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import configs.Config;
import configs.ConfigManager;
import handlers.AlertHandler;
import handlers.PayloadHandler;
import handlers.ProgressAlert;
import handlers.UpdateHandler;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import rcm.RCM;
import util.GlobalSettings;
import util.SDPrepare;
import util.Tray;
import windows.DriverInstaller;

public class MainUIController implements Initializable {

    @FXML
    private Pane pane;

    @FXML
    private TextField payloadPath;

    @FXML
    private ImageView rcmStatus;

    @FXML
    private TextArea log;
    
	@SuppressWarnings("rawtypes")
	@FXML
    private ComboBox configList;

    @FXML
    public TextField configName;

    @FXML
    private MenuBar menuBar;
    
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
    private CheckMenuItem includeFusee;

    @FXML
    private CheckMenuItem includehekate;

    @FXML
    private CheckMenuItem includeLockpickRCM;

    @FXML
    private CheckMenuItem includeTegraExplorer;

    @FXML
    private CheckMenuItem includeIncognitoRCM;

    @FXML
    private CheckMenuItem standardMode;

    @FXML
    private CheckMenuItem portableMode;

    @FXML
    private Button inject;

    @FXML
    private MenuItem installDriver;

    @FXML
    private MenuItem checkJTegraNXUpdates;

    double xOffset;
    double yOffset;

    int configListVisible = 0;
    int configNameTextFieldVisible = 0;

    @FXML
    private void close() {
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
                if (!GlobalSettings.portableMode) {
                    chooser.setInitialDirectory(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR);
                } else {
                    chooser.setInitialDirectory(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR);
                }
            }
        } else {
            if (!GlobalSettings.portableMode) {
                chooser.setInitialDirectory(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR);
            } else {
                chooser.setInitialDirectory(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR);
            }
        }

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("RCM payload files (*.bin)", "*bin");
        chooser.getExtensionFilters().add(filter);

        File file = chooser.showOpenDialog(JTegraNX.getStage());

        if (file != null && file.exists()) {
            payloadPath.setText(file.getAbsolutePath());
            GlobalSettings.savedFolderPath = file.getParent();
            configList.getSelectionModel().select(-1);
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

                if (GlobalSettings.enableTrayIcon) {
                    minimizeToTray.setDisable(false);
                }

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
        } else if (source.equals(includeFusee)) {
            GlobalSettings.includeFusee = includeFusee.isSelected();
            PayloadHandler.updatePayloads();
            UIGlobal.checkIfSpecifiedPayloadExists();

            if (payloadPath.getText().contains("Payloads" + File.separator + "fusee.bin")) {
                payloadPath.clear();
            }

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includehekate)) {
            GlobalSettings.includeHekate = includehekate.isSelected();
            PayloadHandler.updatePayloads();
            UIGlobal.checkIfSpecifiedPayloadExists();

            if (payloadPath.getText().contains("Payloads" + File.separator + "hekate_ctcaer") && payloadPath.getText().endsWith(".bin")) {
                payloadPath.clear();
            }

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includeLockpickRCM)) {
            GlobalSettings.includeLockpickRCM = includeLockpickRCM.isSelected();
            PayloadHandler.updatePayloads();
            UIGlobal.checkIfSpecifiedPayloadExists();

            if (payloadPath.getText().contains("Payloads" + File.separator + "Lockpick_RCM.bin")) {
                payloadPath.clear();
            }

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(includeTegraExplorer)) {
            GlobalSettings.includeTegraExplorer = includeTegraExplorer.isSelected();
            PayloadHandler.updatePayloads();
            UIGlobal.checkIfSpecifiedPayloadExists();

            if (payloadPath.getText().contains("Payloads" + File.separator + "TegraExplorer.bin")) {
                payloadPath.clear();
            }

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        } else if (source.equals(standardMode)) {
            if (!GlobalSettings.portableMode) {
            	standardMode.setSelected(true);
            } else {
            	if (GlobalSettings.PORTABLE_MODE_JTEGRANX_CONFIG_FILE.exists()) {
                    try {
                        FileUtils.forceDelete(GlobalSettings.PORTABLE_MODE_JTEGRANX_CONFIG_FILE);
                    } catch (IOException ex) {
                        Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
                    try {
                        FileUtils.deleteDirectory(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR);
                    } catch (IOException ex) {
                        Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                UIGlobal.clearLog();
                UIGlobal.appendLog("Using standard mode");
                GlobalSettings.portableMode = false;
                portableMode.setSelected(false);

                PayloadHandler.updatePayloads();

                if (GlobalSettings.enableTrayIcon) {
                    Tray.updateMenuItems();
                }
                
                payloadPath.clear();
            }
        } else if (source.equals(portableMode)) {
            if (GlobalSettings.portableMode) {
            	portableMode.setSelected(true);
            } else {
            	if (GlobalSettings.STANDARD_MODE_JTEGRANX_CONFIG_FILE.exists()) {
                    try {
                        FileUtils.forceDelete(GlobalSettings.STANDARD_MODE_JTEGRANX_CONFIG_FILE);
                    } catch (IOException ex) {
                        Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
                    try {
                        FileUtils.deleteDirectory(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR);
                    } catch (IOException ex) {
                        Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                UIGlobal.clearLog();
                UIGlobal.appendLog("Using portable mode");
                GlobalSettings.portableMode = true;
                standardMode.setSelected(false);

                PayloadHandler.updatePayloads();

                if (GlobalSettings.enableTrayIcon) {
                    Tray.updateMenuItems();
                }
                
                payloadPath.clear();
            }
        }
    }

    @FXML
    private void injectPayload() {
        UIGlobal.injectPayload(payloadPath.getText(), false);
    }

    @FXML
    private void checkForJTegraNXUpdates() {
        if (!GlobalSettings.OFFLINE_MODE) {
    		if (!GlobalSettings.restartPending) {
                UpdateHandler.checkForUpdates(null);
            } else {
                UIGlobal.restartJTegraNX();
            }
    	} else {
    		AlertHandler.showErrorMessage("JTegraNX", "JTegraNX is in offline mode", "This feature can't be used in offline mode.");
    	}
    }

    @FXML
    private void showAboutDialog() {
        AlertHandler.showAboutDialog();
    }

    @FXML
    private void checkForPayloadUpdates() {
    	if (!GlobalSettings.OFFLINE_MODE) {
    		PayloadHandler.updatePayloads();
    	} else {
    		AlertHandler.showErrorMessage("JTegraNX", "JTegraNX is in offline mode", "This feature can't be used in offline mode.");
    	}
    }

    @FXML
    private void clearLog() {
        UIGlobal.clearLog();
    }

    @FXML
    private void prepareSDCard() {
    	if (!GlobalSettings.OFFLINE_MODE) {
    		DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select SD Card root");
            File dir = chooser.showDialog(JTegraNX.getStage());

            if (dir != null && dir.exists()) {
                //SDPrepare2.prepareSDCard(dir);
            	ProgressAlert alert = AlertHandler.createProgressAlert("JTegraNX", "Preparing");
            	alert.show();
            	
            	SDPrepare worker = new SDPrepare(dir);
            	
            	alert.getBar().progressProperty().unbind();
            	alert.getBar().progressProperty().bind(worker.progressProperty());
            	
            	alert.getAlert().titleProperty().unbind();
            	alert.getAlert().titleProperty().bind(worker.titleProperty());
            	
                alert.getAlert().headerTextProperty().unbind();
                alert.getAlert().headerTextProperty().bind(worker.messageProperty());
            	
            	worker.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
        			@Override
        			public void handle(WorkerStateEvent event) {
        				alert.close();
        			}
            	});
            	
            	worker.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
        			@Override
        			public void handle(WorkerStateEvent event) {
        				alert.close();
        			}
            	});
            	
            	new Thread(worker).start();
            }
            
            
    	} else {
    		AlertHandler.showErrorMessage("JTegraNX", "JTegraNX is in offline mode", "This feature can't be used in offline mode.");
    	}
    }

    @FXML
    private void installAPXDriver() {
        int result = DriverInstaller.installDriver();

        switch (result) {
            case DriverInstaller.CANCELED:
                UIGlobal.appendLog("APX Driver install canceled by user");
                break;
            case DriverInstaller.DEVICE_UPDATED:
                UIGlobal.appendLog("APX Driver installed\nRCM device needs to be reconnected");
                GlobalSettings.driverUpdatedNeedsReconnect = true;

                if (UIGlobal.getRCMStatus().equals("RCM_DETECTED")) {
                    RCM.promptDeviceReconnect();
                    GlobalSettings.driverUpdatedNeedsReconnect = false;
                }

                break;
            case DriverInstaller.READY_FOR_USE:
                UIGlobal.appendLog("APX Driver installed\nRCM device ready for use");
                break;
            case DriverInstaller.UAC_CANCEL:
                UIGlobal.appendLog("APX Driver install canceled from UAC");
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
        configName.getParent().requestFocus();

        if (GlobalSettings.enableTrayIcon) {
            Tray.updateMenuItems();
        }
    }

    @FXML
    private void clearSelectConfigText() {
        configName.clear();
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
    
    @FXML
    private void gptRestore() {
    	boolean load = AlertHandler.showGPTRestoreDialog();
    	
    	if (load) {
    		PayloadHandler.prepareGPTRestore();
    		UIGlobal.appendLog("Loading gptrestore");
    		
    		UIGlobal.clearLog();
    		UIGlobal.appendLog("Loading gptrestore");
    		RCM.injectPayload(GlobalSettings.gptRestorePath, false);
    	}
    }

    private void setUndecoratedFrameDragEvent() {
        pane.setOnMousePressed((MouseEvent event) -> {
            xOffset = JTegraNX.getStage().getX() - event.getScreenX();
            yOffset = JTegraNX.getStage().getY() - event.getScreenY();
        });

        pane.setOnMouseDragged((MouseEvent event) -> {
            JTegraNX.getStage().setX(event.getScreenX() + xOffset);
            JTegraNX.getStage().setY(event.getScreenY() + yOffset);
        });
        
        menuBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = JTegraNX.getStage().getX() - event.getScreenX();
            yOffset = JTegraNX.getStage().getY() - event.getScreenY();
        });

        menuBar.setOnMouseDragged((MouseEvent event) -> {
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

    @SuppressWarnings("rawtypes")
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
        return includeFusee;
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

    public CheckMenuItem getIncludeIncognitoRCMItem() {
        return includeIncognitoRCM;
    }

    public CheckMenuItem getStandardModeMenuItem() {
        return standardMode;
    }

    public CheckMenuItem getPortableModeMenuItem() {
        return portableMode;
    }

    public Button getInjectButton() {
        return inject;
    }

    public MenuItem getInstallAPXDriverMenuItem() {
        return installDriver;
    }

    public MenuItem getJTegraNXUpdateMenuItem() {
        return checkJTegraNXUpdates;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUndecoratedFrameDragEvent();
    }
}
