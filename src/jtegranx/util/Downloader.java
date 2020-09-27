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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import jtegranx.fx.JTegraNX;
import static jtegranx.payloads.PayloadManager.payloadDir;

public class Downloader {

    public static void downloadFile(URL download, String filePath, boolean silent, boolean hekate) {
        new Thread() {
            @Override
            public void run() {
                try {
                    try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                        byte[] buffer = new byte[1024];
                        int bytes;

                        while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                            fos.write(buffer, 0, bytes);
                        }

                        if (!silent) {
                            Platform.runLater(() -> {
                                Dialog downloaderDialog = new Dialog();
                                downloaderDialog.setTitle("JTegraNX");
                                downloaderDialog.setHeaderText("Download complete.");
                                downloaderDialog.setGraphic(JTegraNX.getController().getDialogImage());

                                ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                                downloaderDialog.getDialogPane().getButtonTypes().add(close);

                                downloaderDialog.initOwner(JTegraNX.getStage());
                                downloaderDialog.showAndWait();
                            });
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Unable to download \"" + download + "\". Reason: " + ex.getClass().getName() + " was thrown!");
                }

                if (hekate) {
                    Unzip.unzip(payloadDir.getAbsolutePath() + "\\Hekate.zip", payloadDir.getAbsolutePath()); 
                    deleteDirectory(new File(payloadDir.getAbsolutePath() + "\\bootloader"));
                    new File(payloadDir.getAbsolutePath() + "\\Hekate.zip").delete();
                }
            }
        }.start();
    }
    
    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        
        directoryToBeDeleted.delete();
    }
}
