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
package jtegranx.payloads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import jtegranx.fx.JTegraNX;
import jtegranx.util.Directories;
import jtegranx.util.Downloader;
import jtegranx.util.Tray;

public class PayloadManager {

    public static File payloadDir;
    public static File payloadConfig;

    private static Payload tegraExplorer;
    private static Payload lockpick_RCM;
    private static Payload fuseePrimary;
    private static Payload hekate;

    public static final Payload[] PAYLOADS = {null, null, null, null};

    public static void initPayloads() {
        TegraExplorer.checkForUpdates();
        Lockpick_RCM.checkForUpdates();
        fusee_primary.checkForUpdates();
        Hekate.checkForUpdates();
        payloadDir = Directories.getPayloadDir();
        payloadConfig = new File(payloadDir.getAbsolutePath() + "\\info.ini");

        tegraExplorer = TegraExplorer.update();
        lockpick_RCM = Lockpick_RCM.update();
        fuseePrimary = fusee_primary.update();
        hekate = Hekate.update();

        PAYLOADS[0] = hekate;
        PAYLOADS[1] = tegraExplorer;
        PAYLOADS[2] = lockpick_RCM;
        PAYLOADS[3] = fuseePrimary;

        if (tegraExplorer != null) {
            if (!payloadExists(tegraExplorer)) {
                downloadPayload(tegraExplorer);
            } else {
                if (!getPayloadVersion(tegraExplorer).equals(tegraExplorer.getVersion()) && !tegraExplorer.getVersion().equals("")) {
                    System.out.println("Updating TegraExplorer.");
                    new File(payloadDir.getAbsolutePath() + "\\TegraExplorer.bin").delete();
                    downloadPayload(tegraExplorer);
                }
            }
        }

        if (lockpick_RCM != null) {
            if (!payloadExists(lockpick_RCM)) {
                downloadPayload(lockpick_RCM);
            } else {
                if (!getPayloadVersion(lockpick_RCM).equals(lockpick_RCM.getVersion()) && !lockpick_RCM.getVersion().equals("")) {
                    System.out.println("Updating Lockpick_RCM.");
                    new File(payloadDir.getAbsolutePath() + "\\Lockpick_RCM.bin").delete();
                    downloadPayload(lockpick_RCM);
                }
            }
        }

        if (lockpick_RCM != null) {
            if (!payloadExists(fuseePrimary)) {
                downloadPayload(fuseePrimary);
            } else {
                if (!getPayloadVersion(fuseePrimary).equals(fuseePrimary.getVersion()) && !fuseePrimary.getVersion().equals("")) {
                    System.out.println("Updating fusee-primary.");
                    new File(payloadDir.getAbsolutePath() + "\\fusee-primary.bin").delete();
                    downloadPayload(fuseePrimary);
                }
            }
        }

        if (hekate != null) {
            if (!payloadExists(hekate)) {
                downloadPayload(hekate);
            } else {
                if (!getPayloadVersion(hekate).equals(hekate.getVersion()) && !hekate.getVersion().equals("")) {
                    System.out.println("Updating Hekate");
                    new File(payloadDir.getAbsolutePath() + "\\Hekate.bin").delete();
                    downloadPayload(hekate);
                }
            }
        }

        for (Payload payload : PAYLOADS) {
            JTegraNX.getController().addPayloadToMenu(payload);
        }

        savePayloadUpdateInfo();
        Tray.showTrayIcon();
        addPayloadsToTray();
    }

    public static boolean payloadExists(Payload payload) {
        return new File(payloadDir.getAbsolutePath() + "\\" + payload.getName() + ".bin").exists();
    }

    private static void downloadPayload(Payload payload) {
        try {
            URL download = new URL(payload.getDownloadURL());

            if (payload.getName().equals("Hekate")) {
                Downloader.downloadFile(download, payloadDir.getAbsolutePath() + "\\" + payload.getName() + ".zip", true, true);
                File h = new File(payloadDir.getAbsolutePath() + "\\hekate_ctcaer_" + payload.getVersion() + ".bin");

                while (!h.renameTo(new File(payloadDir.getAbsolutePath() + "\\Hekate.bin"))) {
                    if (h.renameTo(new File(payloadDir.getAbsolutePath() + "\\Hekate.bin"))) {
                        break;
                    }
                }
            } else {
                Downloader.downloadFile(download, payloadDir.getAbsolutePath() + "\\" + payload.getName() + ".bin", true, false);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(PayloadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getPayloadVersion(Payload payload) {
        if (payloadConfig.exists()) {
            try (FileReader fr = new FileReader(payloadConfig); BufferedReader reader = new BufferedReader(fr)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(payload.getName())) {
                        return line.substring(line.indexOf("=") + 1);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PayloadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "";
    }

    public static void addPayloadsToTray() {
        for (Payload payload : PAYLOADS) {
            Tray.addPayloadToTray(payload);
        }
    }

    private static void savePayloadUpdateInfo() {
        if (payloadConfig.exists()) {
            new File(payloadConfig.getAbsolutePath()).delete();
        }

        try (PrintWriter writer = new PrintWriter(payloadConfig)) {
            writer.println("[Payload Info]");

            for (Payload payload : PAYLOADS) {
                if (payload != null) {
                    writer.println(payload.getName() + "Version=" + payload.getVersion());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PayloadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
