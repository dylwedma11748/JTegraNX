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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import jtegranx.payloads.Payload;
import jtegranx.util.ConfigManager;
import jtegranx.util.Directories;
import jtegranx.util.ResourceLoader;
import jtegranx.util.StatusImages;
import jtegranx.util.TegraRCM;
import jtegranx.util.Tray;

public class GUIController implements Initializable {

    private boolean rcmDetected = false;
    private int i = 0;
    private int trayMinimize = 0;
    private int logToggle = 0;
    private String savedFolderPath = "";

    private Dialog dialog;
    private ChangeListener listener;

    @FXML
    public RadioMenuItem autoInject;
    public RadioMenuItem hideLog;
    public RadioMenuItem minimizeToTray;
    public TextField payloadPath;
    public TextField arguments;
    public TextField configNameEntry;
    public Tooltip tooltip;
    public Button inject;
    public Button saveConfig;
    public ComboBox configList;
    public ImageView rcmStatus;
    public TextArea log;
    public HBox hbox;
    public Menu payloads;

    @FXML
    public void injectAction() {
        if (rcmDetected && !payloadPath.getText().equals("")) {
            clearLog();
            TegraRCM.injectPayload(payloadPath.getText(), arguments.getText());
        } else {
            if (rcmStatus.getImage() == StatusImages.RCM_LOADED) {
                dialog = new Dialog();
                dialog.setTitle("JTegraNX");
                dialog.setHeaderText("Payload already injected. Overwrite the stack?");
                dialog.setGraphic(JTegraNX.getController().getDialogImage());

                ButtonType overwrite = new ButtonType("Overwrite", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(overwrite, ButtonType.CANCEL);

                dialog.initOwner(JTegraNX.getStage());
                dialog.showAndWait();

                if (dialog.getResult().toString().equals("ButtonType [text=Overwrite, buttonData=OK_DONE]")) {
                    clearLog();
                    TegraRCM.injectPayload(payloadPath.getText(), arguments.getText());
                }
            }
        }
    }

    @FXML
    public void autoInjectAction() {
        if (autoInject.isSelected()) {
            inject.setDisable(true);

            if (Tray.isTrayInitialized()) {
                Tray.getAutoInjectMenuItemFromTray().setLabel("Auto-inject (Enabled)");
            }

            injectAction();
        } else {
            inject.setDisable(false);

            if (Tray.isTrayInitialized()) {
                Tray.getAutoInjectMenuItemFromTray().setLabel("Auto-inject (Disabled)");
            }
        }
    }

    @FXML
    private void deleteSelectedConfig() {
        if (configList.getSelectionModel().getSelectedIndex() != -1) {
            String selected = (String) configList.getSelectionModel().getSelectedItem();

            if (!selected.equals("Mount SD Card") && !selected.equals("External Config")) {
                dialog = new Dialog();
                dialog.setTitle("JTegraNX");

                dialog.setHeaderText("Are you sure you want to delete the selected config?");

                dialog.setGraphic(getDialogImage());

                ButtonType delete = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(delete, ButtonType.CANCEL);

                dialog.initOwner(JTegraNX.getStage());
                dialog.showAndWait();

                if (dialog.getResult().toString().equals("ButtonType [text=Delete, buttonData=OK_DONE]")) {
                    ConfigManager.deleteConfig(selected);
                    configList.getSelectionModel().clearSelection(configList.getSelectionModel().getSelectedIndex());
                    updateConfigList();
                    setPayloadPath("");
                    setArguments("");
                }
            } else {
                if (selected.equals("Mount SD Card")) {
                    appendLog("Mount SD Card config can't be deleted.");
                } else if (selected.equals("External Config")) {
                    appendLog("Deleting external configs currently is not allowed.");
                }
            }
        } else {
            appendLog("No config selected.");
        }
    }

    @FXML
    private void clearFields() {
        setPayloadPath("");
        setArguments("");
        configList.getSelectionModel().clearSelection(configList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void clearLog() {
        log.clear();
    }

    @FXML
    private void checkForUpdates() {
        JTegraNX.checkForUpdates();
    }

    @FXML
    public void hideLogAction() {
        switch (logToggle) {
            case 0:
                rcmStatus.setLayoutX(228);
                log.setVisible(false);
                logToggle = 1;
                break;
            case 1:
                rcmStatus.setLayoutX(439);
                log.setVisible(true);
                logToggle = 0;
                break;
        }
    }

    @FXML
    private void loadConfigAction() {
        if (!configList.getSelectionModel().isEmpty()) {
            String selected = (String) configList.getSelectionModel().getSelectedItem();

            switch (selected) {
                case "Mount SD Card":
                    setPayloadPath("jtegranx\\memloader\\memloader_usb.bin");
                    setArguments("-r --dataini=\"jtegranx\\memloader\\ums_sd.ini\"");
                    break;
                case "External Config":
                    setPayloadPath("");
                    setArguments("");

                    FileChooser chooser = new FileChooser();

                    if (new File(savedFolderPath).exists()) {
                        chooser.setInitialDirectory(new File(savedFolderPath));
                    }

                    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JTegraNX Configs (*.ini)", "*ini");
                    chooser.getExtensionFilters().add(filter);

                    File file = chooser.showOpenDialog(JTegraNX.getStage());

                    if (file != null && file.exists()) {
                        if (!new File(Directories.getConfigDir().getAbsolutePath() + "\\Config_" + ConfigManager.getConfigName(file) + ".ini").exists()) {
                            savedFolderPath = file.getParent();

                            if (ConfigManager.configValid(file)) {
                                ConfigManager.loadExternalConfig(file);
                            }
                        } else {
                            dialog = new Dialog();
                            dialog.setTitle("JTegraNX");

                            dialog.setHeaderText("Selected external config exists in \"configs\" directory. \nLoad external config or config from \"configs\"?");

                            dialog.setGraphic(getDialogImage());

                            ButtonType external = new ButtonType("External", ButtonBar.ButtonData.OK_DONE);
                            ButtonType configs = new ButtonType("\"Configs\"", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().addAll(external, configs, ButtonType.CANCEL);

                            dialog.initOwner(JTegraNX.getStage());
                            dialog.showAndWait();

                            switch (dialog.getResult().toString()) {
                                case "ButtonType [text=External, buttonData=OK_DONE]":
                                    if (ConfigManager.configValid(file)) {
                                        ConfigManager.loadExternalConfig(file);
                                    }   break;
                                case "ButtonType [text=\"Configs\", buttonData=OK_DONE]":
                                    ConfigManager.loadConfig(ConfigManager.getConfigName(file));
                                    // IndexOutOfBoundsException gets thrown here but it functions as intended
                                    configList.getSelectionModel().select(ConfigManager.getConfigName(file));
                                    break;
                                default:
                                    // IndexOutOfBoundsException gets thrown here but it functions as intended
                                    configList.getSelectionModel().clearSelection(configList.getSelectionModel().getSelectedIndex());
                                    break;
                            }
                        }
                    } else {
                        // IndexOutOfBoundsException gets thrown here but it functions as intended
                        configList.getSelectionModel().clearSelection(configList.getSelectionModel().getSelectedIndex());
                    }

                    break;
                default:
                    setPayloadPath("");
                    setArguments("");
                    ConfigManager.loadConfig(selected);
                    break;
            }
        }
    }

    @FXML
    public void minimizeToTrayAction() {
        switch (trayMinimize) {
            case 0:
                setMinimizeTrayFlag(true);
                trayMinimize = 1;
                break;

            case 1:
                setMinimizeTrayFlag(false);
                trayMinimize = 0;
                break;
        }
    }

    private void setMinimizeTrayFlag(boolean tray) {
        if (tray) {
            JTegraNX.getStage().iconifiedProperty().addListener(listener);
        } else {
            Platform.setImplicitExit(true);
            JTegraNX.getStage().iconifiedProperty().removeListener(listener);
        }
    }

    @FXML
    private void updateConfigList() {
        ConfigManager.updateConfigList();
    }

    @FXML
    private void saveConfigAction() {
        switch (i) {
            case 0:
                hbox.setLayoutX(84);
                configNameEntry.setVisible(true);
                saveConfig.setText("Cancel");
                tooltip.setText("Cancel saving the config.");
                i = 1;
                break;
            case 1:
                hbox.setLayoutX(135);
                configNameEntry.setVisible(false);
                saveConfig.setText("Save Config");
                tooltip.setText("Save current inout as a config.");
                i = 0;
                break;
        }
    }

    @FXML
    private void configNameEntryKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            if (!configNameEntry.getText().equals("")) {
                ConfigManager.saveConfig(configNameEntry.getText());
                updateConfigList();
                configList.getSelectionModel().select(configNameEntry.getText());
                configNameEntry.setText("");
                hbox.setLayoutX(135);
                configNameEntry.setVisible(false);
                saveConfig.setText("Save Config");
                i = 0;
            }
        }
    }

    @FXML
    private void browseButtonAction() {
        FileChooser chooser = new FileChooser();

        if (new File(savedFolderPath).exists()) {
            chooser.setInitialDirectory(new File(savedFolderPath));
        }

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Bin files (*.bin)", "*bin");
        chooser.getExtensionFilters().add(filter);

        File file = chooser.showOpenDialog(JTegraNX.getStage());

        if (file != null && file.exists()) {
            payloadPath.setText(file.getAbsolutePath());
            savedFolderPath = file.getParent();
            arguments.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (!System.getProperty("os.name").contains("Windows")) {
            Dialog errorDialog = new Dialog();
            errorDialog.setTitle("JTegraNX");
            errorDialog.setHeaderText("Unsupported platform");
            errorDialog.setHeaderText(" JTegraNX currently only supports Windows\n Your OS: " + System.getProperty("os.name"));

            ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.OK_DONE);
            errorDialog.getDialogPane().getButtonTypes().add(exit);

            errorDialog.showAndWait();
            System.exit(0);
        } else {
            ResourceLoader.loadResources();

            listener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
                if (newValue.toString().equals("true")) {
                    Platform.setImplicitExit(false);
                    JTegraNX.getStage().hide();
                }
            };
        }
    }

    public String getPayloadPath() {
        return payloadPath.getText();
    }

    public String getArguments() {
        return arguments.getText();
    }

    public ComboBox getConfigList() {
        return configList;
    }

    public String getSavedFolderPath() {
        return savedFolderPath;
    }

    public boolean autoInjectEnabled() {
        return autoInject.isSelected();
    }

    public void setPayloadPath(String path) {
        payloadPath.setText(path);
    }

    public void setArguments(String args) {
        arguments.setText(args);
    }

    public void setSavedFolderPath(String path) {
        savedFolderPath = path;
    }

    public void setRcmStatus(boolean detected) {
        rcmDetected = detected;
    }

    public void setRcmStatusImage(Image image) {
        rcmStatus.setImage(image);
    }

    public void appendLog(String line) {
        log.appendText(line + "\n");
    }

    public void addConfig(String config) {
        configList.getItems().add(config);

    }

    public void addPayloadToMenu(Payload payload) {
        MenuItem item = new MenuItem(payload.getName() + " v" + payload.getVersion());

        item.setOnAction((ActionEvent event) -> {
            setPayloadPath("jtegranx\\payloads\\" + payload.getName() + ".bin");
            setArguments("");
            configList.getSelectionModel().select(-1);
        });

        payloads.getItems().add(item);
    }

    public void removeAllConfigsFromList() {
        configList.getItems().clear();
    }

    public ImageView getDialogImage() {
        return new ImageView(this.getClass().getResource("/jtegranx/gui/images/dialog_image.png").toString());
    }
}
