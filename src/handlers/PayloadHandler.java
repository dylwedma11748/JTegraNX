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
package handlers;

import git.GitHandler;
import git.Release;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;
import ui.UIGlobal;
import ui.fx.JTegraNX;
import util.GlobalSettings;

public class PayloadHandler {

    private static Release fuseePrimary;
    private static Release hekate;
    private static Release lockpickRCM;
    private static Release tegraExplorer;

    public static void updatePayloads() {
        checkSelectedPayloads();
        UIGlobal.clearPayloadMenu();

        if (GlobalSettings.selectedPayloadCount > 0) {
            if (!GlobalSettings.portableMode) {
                if (!GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
                    GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.mkdirs();
                }
            } else {
                if (!GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
                    GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.mkdirs();
                }
            }

            if (GlobalSettings.includeFuseePrimary) {
                fuseePrimary = GitHandler.generateLatestRelease("https://github.com/Atmosphere-NX/Atmosphere/releases");
                GlobalSettings.fuseePrimaryTag = fuseePrimary.getTag();

                if (GlobalSettings.fuseePrimaryTag != null && !GlobalSettings.fuseePrimaryTag.equals(fuseePrimary.getTag()) && !GlobalSettings.portableMode) {
                    fuseePrimary.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("fusee-primary.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                    });
                } else if (!new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin").exists() && !GlobalSettings.portableMode) {
                    fuseePrimary.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("fusee-primary.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                    });
                }

                if (GlobalSettings.fuseePrimaryTag != null && !GlobalSettings.fuseePrimaryTag.equals(fuseePrimary.getTag()) && GlobalSettings.portableMode) {
                    fuseePrimary.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("fusee-primary.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                    });
                } else if (!new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin").exists() && GlobalSettings.portableMode) {
                    fuseePrimary.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("fusee-primary.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                    });
                }

                MenuItem item = new MenuItem("fusee-primary v" + fuseePrimary.getTag());
                ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
                image.setEffect(new DropShadow());
                item.setGraphic(image);

                item.setOnAction((ActionEvent event) -> {
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

                JTegraNX.getController().getPayloadMenu().getItems().add(item);
            } else {
                File payload;

                if (!GlobalSettings.portableMode) {
                    payload = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                } else {
                    payload = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin");
                }

                if (payload.exists()) {
                    try {
                        FileUtils.forceDelete(payload);
                    } catch (IOException ex) {
                        Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (GlobalSettings.includeHekate) {
                hekate = GitHandler.generateLatestRelease("https://github.com/CTCaer/hekate/releases");
                GlobalSettings.hekateTag = hekate.getTag();

                if (GlobalSettings.hekateTag != null && !GlobalSettings.hekateTag.equals(hekate.getTag()) && !GlobalSettings.portableMode) {
                    hekate.getAssets().stream().filter((asset) -> (asset.getAssetName().contains("hekate_ctcaer"))).map((asset) -> GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "hekate.zip")).map((zip) -> {
                        ZipHandler.unzip(zip.getAbsolutePath(), GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
                        return zip;
                    }).forEachOrdered((_item) -> {
                        for (File file : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            if (file.getName().equals("bootloader")) {
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (file.getName().equals("hekate.zip")) {
                                file.delete();
                            }

                            if (file.getName().contains("hekate_ctcaer")) {
                                file.renameTo(new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin"));
                                file.delete();
                            }
                        }
                    });
                } else if (!new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin").exists() && !GlobalSettings.portableMode) {
                    hekate.getAssets().stream().filter((asset) -> (asset.getAssetName().contains("hekate_ctcaer"))).map((asset) -> GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "hekate.zip")).map((zip) -> {
                        ZipHandler.unzip(zip.getAbsolutePath(), GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
                        return zip;
                    }).forEachOrdered((_item) -> {
                        for (File file : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            if (file.getName().equals("bootloader")) {
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (file.getName().equals("hekate.zip")) {
                                file.delete();
                            }

                            if (file.getName().contains("hekate_ctcaer")) {
                                file.renameTo(new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin"));
                                file.delete();
                            }
                        }
                    });
                }

                if (GlobalSettings.hekateTag != null && !GlobalSettings.hekateTag.equals(hekate.getTag()) && GlobalSettings.portableMode) {
                    hekate.getAssets().stream().filter((asset) -> (asset.getAssetName().contains("hekate_ctcaer"))).map((asset) -> GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "hekate.zip")).map((zip) -> {
                        ZipHandler.unzip(zip.getAbsolutePath(), GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
                        return zip;
                    }).forEachOrdered((_item) -> {
                        for (File file : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            if (file.getName().equals("bootloader")) {
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (file.getName().equals("hekate.zip")) {
                                file.delete();
                            }

                            if (file.getName().contains("hekate_ctcaer")) {
                                file.renameTo(new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin"));
                                file.delete();
                            }
                        }
                    });
                } else if (!new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin").exists() && GlobalSettings.portableMode) {
                    hekate.getAssets().stream().filter((asset) -> (asset.getAssetName().contains("hekate_ctcaer"))).map((asset) -> GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "hekate.zip")).map((zip) -> {
                        ZipHandler.unzip(zip.getAbsolutePath(), GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
                        return zip;
                    }).forEachOrdered((_item) -> {
                        for (File file : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            if (file.getName().equals("bootloader")) {
                                try {
                                    FileUtils.deleteDirectory(file);
                                } catch (IOException ex) {
                                    Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (file.getName().equals("hekate.zip")) {
                                file.delete();
                            }

                            if (file.getName().contains("hekate_ctcaer")) {
                                file.renameTo(new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin"));
                                file.delete();
                            }
                        }
                    });
                }

                MenuItem item = new MenuItem("Hekate " + hekate.getTag());
                ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
                image.setEffect(new DropShadow());
                item.setGraphic(image);

                item.setOnAction((ActionEvent event) -> {
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

                JTegraNX.getController().getPayloadMenu().getItems().add(item);
            } else {
                File payload;
                
                if (!GlobalSettings.portableMode) {
                    payload = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin");
                } else {
                    payload = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Hekate.bin");
                }

                if (payload.exists()) {
                    try {
                        FileUtils.forceDelete(payload);
                    } catch (IOException ex) {
                        Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (GlobalSettings.includeLockpickRCM) {
                lockpickRCM = GitHandler.generateLatestRelease("https://github.com/shchmue/Lockpick_RCM/releases");
                GlobalSettings.lockpickRCMTag = lockpickRCM.getTag();

                if (GlobalSettings.lockpickRCMTag != null && !GlobalSettings.lockpickRCMTag.equals(lockpickRCM.getTag()) && !GlobalSettings.portableMode) {
                    lockpickRCM.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Lockpick_RCM.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                    });
                } else if (!new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin").exists() && !GlobalSettings.portableMode) {
                    lockpickRCM.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Lockpick_RCM.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                    });
                }
                
                if (GlobalSettings.lockpickRCMTag != null && !GlobalSettings.lockpickRCMTag.equals(lockpickRCM.getTag()) && GlobalSettings.portableMode) {
                    lockpickRCM.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Lockpick_RCM.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                    });
                } else if (!new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin").exists() && GlobalSettings.portableMode) {
                    lockpickRCM.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Lockpick_RCM.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                    });
                }

                MenuItem item = new MenuItem("Lockpick_RCM " + lockpickRCM.getTag());
                ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
                image.setEffect(new DropShadow());
                item.setGraphic(image);

                item.setOnAction((ActionEvent event) -> {
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

                JTegraNX.getController().getPayloadMenu().getItems().add(item);
            } else {
                File payload;
                
                if (!GlobalSettings.portableMode) {
                    payload = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                } else {
                    payload = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
                }

                if (payload.exists()) {
                    try {
                        FileUtils.forceDelete(payload);
                    } catch (IOException ex) {
                        Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            if (GlobalSettings.includeTegraExplorer) {
                tegraExplorer = GitHandler.generateLatestRelease("https://github.com/suchmememanyskill/TegraExplorer/releases");
                GlobalSettings.tegraExplorerTag = tegraExplorer.getTag();

                if (GlobalSettings.tegraExplorerTag != null && !GlobalSettings.tegraExplorerTag.equals(tegraExplorer.getTag()) && !GlobalSettings.portableMode) {
                    tegraExplorer.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("TegraExplorer.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                    });
                } else if (!new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin").exists() && !GlobalSettings.portableMode) {
                    tegraExplorer.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("TegraExplorer.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                    });
                }
                
                if (GlobalSettings.tegraExplorerTag != null && !GlobalSettings.tegraExplorerTag.equals(tegraExplorer.getTag()) && GlobalSettings.portableMode) {
                    tegraExplorer.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("TegraExplorer.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                    });
                } else if (!new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin").exists() && GlobalSettings.portableMode) {
                    tegraExplorer.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("TegraExplorer.bin"))).forEachOrdered((asset) -> {
                        GitHandler.downloadAsset(asset, GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                    });
                }

                MenuItem item = new MenuItem("TegraExplorer v" + tegraExplorer.getTag());
                ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
                image.setEffect(new DropShadow());
                item.setGraphic(image);

                item.setOnAction((ActionEvent event) -> {
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

                JTegraNX.getController().getPayloadMenu().getItems().add(item);
            } else {
                File payload;
                
                if (!GlobalSettings.portableMode) {
                    payload = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                } else {
                    payload = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
                }

                if (payload.exists()) {
                    try {
                        FileUtils.forceDelete(payload);
                    } catch (IOException ex) {
                        Logger.getLogger(PayloadHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        GlobalSettings.payloadsUpdatedThisSession = true;
    }

    public static void addSelectedPayloadsToMenu() {
        JTegraNX.getController().getPayloadMenu().getItems().removeAll();

        if (GlobalSettings.includeFuseePrimary) {
            MenuItem item = new MenuItem("fusee-primary v" + GlobalSettings.fuseePrimaryTag);
            ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
            image.setEffect(new DropShadow());
            item.setGraphic(image);

            item.setOnAction((ActionEvent event) -> {
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

            JTegraNX.getController().getPayloadMenu().getItems().add(item);
        }

        if (GlobalSettings.includeHekate) {
            MenuItem item = new MenuItem("Hekate " + GlobalSettings.hekateTag);
            ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
            image.setEffect(new DropShadow());
            item.setGraphic(image);

            item.setOnAction((ActionEvent event) -> {
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

            JTegraNX.getController().getPayloadMenu().getItems().add(item);
        }

        if (GlobalSettings.includeLockpickRCM) {
            MenuItem item = new MenuItem("Lockpick_RCM " + GlobalSettings.lockpickRCMTag);
            ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
            image.setEffect(new DropShadow());
            item.setGraphic(image);

            item.setOnAction((ActionEvent event) -> {
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

            JTegraNX.getController().getPayloadMenu().getItems().add(item);
        }

        if (GlobalSettings.includeTegraExplorer) {
            MenuItem item = new MenuItem("TegraExplorer v" + GlobalSettings.tegraExplorerTag);
            ImageView image = new ImageView(PayloadHandler.class.getResource("/ui/images/payload.png").toString());
            image.setEffect(new DropShadow());
            item.setGraphic(image);

            item.setOnAction((ActionEvent event) -> {
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

            JTegraNX.getController().getPayloadMenu().getItems().add(item);
        }
    }

    public static void checkSelectedPayloads() {
        GlobalSettings.selectedPayloadCount = 0;

        if (JTegraNX.getController().getIncludeLockpickRCMMenuItem().isSelected()) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (JTegraNX.getController().getIncludeTegraExplorerItem().isSelected()) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (JTegraNX.getController().getIncludeHekateMenuItem().isSelected()) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (JTegraNX.getController().getIncludeFuseePrimaryMenuItem().isSelected()) {
            GlobalSettings.selectedPayloadCount++;
        }
    }

    public static StringBuilder getPayloadInfoAsString() {
        checkSelectedPayloads();
        StringBuilder payloadInfo = new StringBuilder();

        if (GlobalSettings.selectedPayloadCount > 0) {
            payloadInfo.append("[PAYLOAD RELEASE INFO]\n");

            if (GlobalSettings.includeFuseePrimary) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("fuseePrimary=").append(fuseePrimary.getTag()).append("\n");
                } else {
                    payloadInfo.append("fuseePrimary=").append(GlobalSettings.fuseePrimaryTag).append("\n");
                }
            }

            if (GlobalSettings.includeHekate) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("hekate=").append(hekate.getTag()).append("\n");
                } else {
                    payloadInfo.append("hekate=").append(GlobalSettings.hekateTag).append("\n");
                }
            }

            if (GlobalSettings.includeLockpickRCM) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("lockpickRCM=").append(lockpickRCM.getTag()).append("\n");
                } else {
                    payloadInfo.append("lockpickRCM=").append(GlobalSettings.lockpickRCMTag).append("\n");
                }
            }

            if (GlobalSettings.includeTegraExplorer) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("tegraExplorer=").append(tegraExplorer.getTag()).append("\n");
                } else {
                    payloadInfo.append("tegraExplorer=").append(GlobalSettings.tegraExplorerTag).append("\n");
                }
            }
        }

        return payloadInfo;
    }
}
