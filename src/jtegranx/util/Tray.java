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
import javafx.application.Platform;
import javax.swing.ImageIcon;
import jtegranx.fx.JTegraNX;
import jtegranx.payloads.Payload;
import jtegranx.payloads.PayloadManager;

public class Tray {

    private static final File configDir = Directories.getConfigDir();
    private static boolean trayInitialized = false;
    private static PopupMenu menu;
    private static SystemTray systemTray;
    private static TrayIcon trayIcon;
    private static MenuItem autoInjectItem;
    private static MenuItem exitItem;

    public static void addSeperator() {
        menu.addSeparator();
    }

    public static void showTrayIcon() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported on your OS!");
        } else {
            menu = new PopupMenu();

            trayIcon = new TrayIcon(createIcon("/jtegranx/gui/images/icon.png", "Tray Icon"));
            trayIcon.setImageAutoSize(true);
            trayIcon.setPopupMenu(menu);
            trayIcon.setToolTip("JTegraNX");

            systemTray = SystemTray.getSystemTray();

            if (!configDir.exists()) {
                configDir.mkdir();
            } else {
                File[] files = configDir.listFiles();

                for (File file : files) {
                    if (file.getAbsolutePath().endsWith(".ini")) {
                        addConfigToTray(file);
                    }
                }
            }

            try {
                trayIcon.addActionListener((ActionEvent e) -> {
                    Platform.runLater(() -> {
                        JTegraNX.getStage().setIconified(false);
                        JTegraNX.getStage().setAlwaysOnTop(true);
                        JTegraNX.getStage().show();
                        JTegraNX.getStage().setAlwaysOnTop(false);
                        Platform.setImplicitExit(true);
                    });
                });

                systemTray.add(trayIcon);
                trayInitialized = true;
            } catch (AWTException ex) {
                Logger.getLogger(Tray.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void removeTrayIcon() {
        systemTray.remove(trayIcon);
    }

    protected static Image createIcon(String path, String desc) {
        URL url = Tray.class.getResource(path);
        return (new ImageIcon(url, desc)).getImage();
    }

    public static MenuItem getAutoInjectMenuItemFromTray() {
        return autoInjectItem;
    }

    public static void addPayloadToTray(Payload payload) {
        MenuItem p = new MenuItem(payload.getName() + " v" + payload.getVersion());

        p.addActionListener((ActionEvent e) -> {
            Platform.runLater(() -> {
                if (PayloadManager.payloadExists(payload)) {
                    JTegraNX.getController().setPayloadPath("jtegranx\\payloads\\" + payload.getName() + ".bin");
                    JTegraNX.getController().setArguments("");
                    JTegraNX.getController().getConfigList().getSelectionModel().select(-1);
                }
            });
        });

        menu.add(p);
    }

    public static void addMountSDCardConfigToTray() {
        MenuItem config = new MenuItem("Mount SD Card");

        config.addActionListener((ActionEvent e) -> {
            Platform.runLater(() -> {
                JTegraNX.getController().setPayloadPath("jtegranx\\memloader\\memloader_usb.bin");
                JTegraNX.getController().setArguments("-r --dataini=\"jtegranx\\memloader\\ums_sd.ini\"");
                JTegraNX.getController().getConfigList().getSelectionModel().select(config.getLabel());
            });
        });

        menu.add(config);
    }

    public static void addConfigToTray(File file) {
        MenuItem config = new MenuItem(ConfigManager.getConfigName(file));

        config.addActionListener((ActionEvent e) -> {
            Platform.runLater(() -> {
                if (file.exists() && ConfigManager.configValid(file)) {
                    JTegraNX.getController().getConfigList().getSelectionModel().select(config.getLabel());
                } else {
                    menu.remove(config);
                }
            });
        });

        menu.add(config);
    }

    public static void addDefaultMenuItems() {
        exitItem = new MenuItem("Exit");
        autoInjectItem = new MenuItem();

        if (JTegraNX.getController().autoInjectEnabled()) {
            autoInjectItem.setLabel("Auto-inject (Enabled)");
        } else {
            autoInjectItem.setLabel("Auto-inject (Disabled)");
        }

        exitItem.addActionListener((ActionEvent e) -> {
            Platform.exit();
        });

        autoInjectItem.addActionListener((ActionEvent e) -> {
            boolean enabled = JTegraNX.getController().autoInjectEnabled();

            if (enabled) {
                autoInjectItem.setLabel("Auto-inject (Disabled)");
                JTegraNX.getController().autoInject.setSelected(false);
                JTegraNX.getController().inject.setDisable(false);
            } else {
                autoInjectItem.setLabel("Auto-inject (Enabled)");
                JTegraNX.getController().autoInject.setSelected(true);
                JTegraNX.getController().inject.setDisable(true);
            }
        });

        menu.addSeparator();
        menu.add(autoInjectItem);
        menu.add(exitItem);
    }

    public static void clearTrayMenu() {
        menu.removeAll();
    }

    public static boolean isTrayInitialized() {
        return trayInitialized;
    }
}
