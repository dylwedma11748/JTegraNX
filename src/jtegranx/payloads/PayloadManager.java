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
import static jtegranx.util.ConfigManager.configDir;
import jtegranx.util.Downloader;
import static jtegranx.util.ResourceLoader.*;

public class PayloadManager {

    public static File payloadDir;
    public static File payloadConfig;

    private static Payload tegraExplorer;
    private static Payload lockpick_RCM;
    
    private static boolean tegraExplorerUpdate = false;
    private static boolean lockpick_RCMupdate = false;

    public static final Payload[] payloads = {null, null};

    public static void initPayloads() {
        new Thread() {
            @Override
            public void run() {
                TegraExplorer.checkForUpdates();
                Lockpick_RCM.checkForUpdates();
                initPayloadFolder();
                
                tegraExplorer = TegraExplorer.update();
                lockpick_RCM = Lockpick_RCM.update();

                payloads[0] = tegraExplorer;
                payloads[1] = lockpick_RCM;

                if (tegraExplorer != null) {
                    if (!payloadExists(tegraExplorer)) {
                        System.out.println("TegraExplorer not found in payloads directory. Downloading.");
                        downloadPayload(tegraExplorer);
                    } else {
                        if (!getPayloadVersion(tegraExplorer).equals(tegraExplorer.getVersion()) && !tegraExplorer.getVersion().equals("")) {
                            System.out.println("Updating TegraExplorer.");
                            tegraExplorerUpdate = true;
                            new File(payloadDir.getAbsolutePath() + "\\TegraExplorer.bin").delete();
                            downloadPayload(tegraExplorer);
                        }
                    }
                } else {
                    System.out.println("TegraExplorer update check returned null. Moving on.");
                }

                if (lockpick_RCM != null) {
                    if (!payloadExists(lockpick_RCM)) {
                        System.out.println("Lockpick_RCM not found in payloads directory. Downloading.");
                        downloadPayload(lockpick_RCM);
                    } else {
                        if (!getPayloadVersion(lockpick_RCM).equals(lockpick_RCM.getVersion()) && !lockpick_RCM.getVersion().equals("")) {
                            System.out.println("Updating Lockpick_RCM.");
                            lockpick_RCMupdate = true;
                            new File(payloadDir.getAbsolutePath() + "\\Lockpick_RCM.bin").delete();
                            downloadPayload(tegraExplorer);
                        }
                    }
                } else {
                    System.out.println("Lockpick_RCM update check returned null. Moving on.");
                }
                
                if (tegraExplorerUpdate) {
                    System.out.println("Updated TegraExplorer.");
                } else {
                    if (tegraExplorer != null) {
                        System.out.println("TegraExplorer is up to date.");
                    }
                }
                
                if (lockpick_RCMupdate) {
                    System.out.println("Updated Lockpick_RCM.");
                } else {
                    if (lockpick_RCM != null) {
                        System.out.println("Lockpick_RCM is up to date.");
                    }
                }

                savePayloadUpdateInfo(payloads);
                generatePayloadConfigs(payloads);
            }
        }.start();
    }

    private static boolean payloadExists(Payload payload) {
        return new File(payloadDir.getAbsolutePath() + "\\" + payload.getName() + ".bin").exists();
    }

    private static void initPayloadFolder() {
        payloadDir = new File(jtegranxdir.getAbsolutePath() + "\\payloads");
        payloadConfig = new File(payloadDir.getAbsolutePath() + "\\info.ini");

        if (!payloadDir.exists()) {
            payloadDir.mkdir();
        }
    }

    private static void downloadPayload(Payload payload) {
        try {
            URL download = new URL(payload.getDownloadURL());
            Downloader.downloadFile(download, payloadDir.getAbsolutePath() + "\\" + payload.getName() + ".bin", true);
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

    private static void savePayloadUpdateInfo(Payload[] payloads) {
        if (payloadConfig.exists()) {
            new File(payloadConfig.getAbsolutePath()).delete();
        }

        try (PrintWriter writer = new PrintWriter(payloadConfig)) {
            writer.println("Warning: Modifying this file may cause problems with JTegraNX.");
            writer.println("Do not modify this file unless you know what you're doing.");
            writer.println("");
            writer.println("[Payload Info]");

            for (Payload payload : payloads) {
                if (payload != null) {
                    writer.println(payload.getName() + "Version=" + payload.getVersion());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PayloadManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generatePayloadConfigs(Payload[] payloads) {
        for (Payload payload : payloads) {
            if (payload != null) {
                File config = new File(configDir.getAbsolutePath() + "\\Config_" + payload.getName().replaceAll(" ", "_") + ".ini");

                if (!config.exists()) {
                    try (PrintWriter writer = new PrintWriter(config)) {
                        writer.println("[JTegraNX Config]");
                        writer.println("configName=" + payload.getName());
                        writer.println("payloadPath=jtegranx\\payloads\\" + payload.getName() + ".bin");
                    } catch (IOException ex) {
                        Logger.getLogger(PayloadManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
}
