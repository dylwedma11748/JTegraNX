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
package util;

import configs.ConfigManager;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import handlers.PayloadHandler;
import javafx.application.Platform;
import ui.JTegraNX;
import ui.UIGlobal;

public class Tray {

    private static PopupMenu menu;
    private static SystemTray systemTray;
    private static TrayIcon trayIcon;

    private static MenuItem autoInjectItem;
    private static MenuItem injectItem;
    private static MenuItem exitItem;

    public static TrayIcon.MessageType error = TrayIcon.MessageType.ERROR;
    public static TrayIcon.MessageType info = TrayIcon.MessageType.INFO;
    public static TrayIcon.MessageType none = TrayIcon.MessageType.NONE;
    public static TrayIcon.MessageType warning = TrayIcon.MessageType.WARNING;

    public static void enableTrayIcon() {
        if (!SystemTray.isSupported()) {
            UIGlobal.appendLog("System tray is not supported on your system");

            GlobalSettings.enableTrayIcon = !GlobalSettings.enableTrayIcon;

            JTegraNX.getController().getEnableTrayIconMenuItem().setSelected(false);
            JTegraNX.getController().getEnableTrayIconMenuItem().setDisable(true);
            JTegraNX.getController().getMinimizeToTrayItem().setSelected(false);
            JTegraNX.getController().getMinimizeToTrayItem().setDisable(true);
        } else {
            menu = new PopupMenu();

            trayIcon = new TrayIcon(createIcon("/images/icon.png", "JTegraNX Tray Icon"));
            trayIcon.setImageAutoSize(true);
            trayIcon.setPopupMenu(menu);
            trayIcon.setToolTip("JTegraNX");

            systemTray = SystemTray.getSystemTray();

            trayIcon.addActionListener((ActionEvent e) -> {
            	Platform.runLater(() -> {
                    if (JTegraNX.getStage().isIconified()) {
                        JTegraNX.getStage().setIconified(false);
                        JTegraNX.getStage().setAlwaysOnTop(true);
                        JTegraNX.getStage().show();
                        JTegraNX.getStage().setAlwaysOnTop(false);
                    }
                });
            });

            try {
                systemTray.add(trayIcon);
                showNotification("JTegraNX tray active", info);
            } catch (AWTException ex) {
                Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
            }

            updateMenuItems();
        }
    }

    public static void addPayloadsToTray() {
        PayloadHandler.checkSelectedPayloads();

        if (GlobalSettings.selectedPayloadCount > 0) {
            if (GlobalSettings.includeFusee) {
                MenuItem fuseePrimary = new MenuItem("fusee-primary (Atmosphere " + GlobalSettings.fuseeTag + ")");

                fuseePrimary.addActionListener((ActionEvent e) -> {
                    Platform.runLater(() -> {
                        if (!GlobalSettings.portableMode) {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                        } else {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                        }

                        try {
                            if (!JTegraNX.getController().getConfigList().getSelectionModel().getSelectedItem().equals("No configs")) {
                                JTegraNX.getController().getConfigList().getSelectionModel().clearSelection();
                            }
                        } catch (NullPointerException ex) {
                        }
                    });
                });

                menu.add(fuseePrimary);
            }

            if (GlobalSettings.includeHekate) {
                MenuItem hekate = new MenuItem("Hekate " + GlobalSettings.hekateTag);

                hekate.addActionListener((ActionEvent e) -> {
                    Platform.runLater(() -> {
                        if (!GlobalSettings.portableMode) {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin");
                        } else {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin");
                        }

                        try {
                            if (!JTegraNX.getController().getConfigList().getSelectionModel().getSelectedItem().equals("No configs")) {
                                JTegraNX.getController().getConfigList().getSelectionModel().clearSelection();
                            }
                        } catch (NullPointerException ex) {
                        }
                    });
                });

                menu.add(hekate);
            }

            if (GlobalSettings.includeLockpickRCM) {
                MenuItem lockpickRCM = new MenuItem("Lockpick_RCM " + GlobalSettings.lockpickRCMTag);

                lockpickRCM.addActionListener((ActionEvent e) -> {
                    Platform.runLater(() -> {
                        if (!GlobalSettings.portableMode) {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                        } else {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                        }

                        try {
                            if (!JTegraNX.getController().getConfigList().getSelectionModel().getSelectedItem().equals("No configs")) {
                                JTegraNX.getController().getConfigList().getSelectionModel().clearSelection();
                            }
                        } catch (NullPointerException ex) {
                        }
                    });
                });

                menu.add(lockpickRCM);
            }

            if (GlobalSettings.includeTegraExplorer) {
                MenuItem tegraExplorer = new MenuItem("TegraExplorer v" + GlobalSettings.tegraExplorerTag);

                tegraExplorer.addActionListener((ActionEvent e) -> {
                    Platform.runLater(() -> {
                        if (!GlobalSettings.portableMode) {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                        } else {
                            JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                        }

                        try {
                            if (!JTegraNX.getController().getConfigList().getSelectionModel().getSelectedItem().equals("No configs")) {
                                JTegraNX.getController().getConfigList().getSelectionModel().clearSelection();
                            }
                        } catch (NullPointerException ex) {
                        }
                    });
                });

                menu.add(tegraExplorer);
            }
        }
    }

    public static void addConfigsToTray() {
        if (ConfigManager.getConfigList().size() > 0) {
            menu.addSeparator();

            ConfigManager.getConfigList().stream().map((config) -> {
                MenuItem item = new MenuItem(config.getConfigName());

                item.addActionListener((ActionEvent e) -> {
                    ConfigManager.selectConfig(config.getConfigName());
                });

                return item;
            }).forEachOrdered((item) -> {
                menu.add(item);
            });
        }
    }

    public static void addMainMenuItems() {
        menu.addSeparator();

        String label;

        if (GlobalSettings.autoInject) {
            label = "Auto Inject: Enabled";
        } else {
            label = "Auto Inject: Disabled";
        }

        autoInjectItem = new MenuItem(label);

        autoInjectItem.addActionListener((ActionEvent e) -> {
            GlobalSettings.autoInject = !GlobalSettings.autoInject;
            toggleAutoInjectLabel();
        });

        injectItem = new MenuItem();

        File payload = new File(JTegraNX.getController().getPayloadPathField().getText());

        if (payload.exists()) {
            injectItem.setLabel("Inject " + payload.getName());
        } else {
            injectItem.setLabel("Specified payload doesn't exist");
        }

        if (UIGlobal.getRCMStatus().equals("DRIVER_MISSING")) {
            injectItem.setLabel("APX driver missing");
        }

        injectItem.addActionListener((ActionEvent e) -> {
            File payloadToInject = new File(JTegraNX.getController().getPayloadPathField().getText());

            if (payloadToInject.exists()) {
                UIGlobal.injectPayload(JTegraNX.getController().getPayloadPathField().getText(), false);
            }
        });

        exitItem = new MenuItem("Exit");

        exitItem.addActionListener((ActionEvent e) -> {
            Platform.runLater(() -> {
                UIGlobal.closeJTegraNX();
            });
        });

        menu.add(autoInjectItem);
        menu.add(injectItem);
        menu.add(exitItem);
    }

    public static void toggleAutoInjectLabel() {
        String label;

        if (GlobalSettings.autoInject) {
            label = "Auto Inject: Enabled";
        } else {
            label = "Auto Inject: Disabled";
        }

        autoInjectItem.setLabel(label);
        JTegraNX.getController().getAutoInjectMenuItem().setSelected(GlobalSettings.autoInject);
    }

    public static void showNotification(String message, TrayIcon.MessageType type) {
        trayIcon.displayMessage("JTegraNX", message, type);
    }

    public static void updateMenuItems() {
        Platform.runLater(() -> {
            menu.removeAll();
            addPayloadsToTray();
            addConfigsToTray();
            addMainMenuItems();
        });
    }

    public static void disableTrayIcon() {
        new Thread() {
            @Override
            public void run() {
                systemTray.remove(trayIcon);
            }
        }.start();
    }

    protected static Image createIcon(String path, String desc) {
        URL url = Tray.class.getResource(path);
        return (new ImageIcon(url, desc)).getImage();
    }
}
