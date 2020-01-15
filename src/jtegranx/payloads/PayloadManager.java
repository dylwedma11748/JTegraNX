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

    public static final Payload[] payloads = {null};

    public static void initPayloads() {
        new Thread() {
            @Override
            public void run() {
                TegraExplorer.checkForUpdates();
                initPayloadFolder();
                tegraExplorer = TegraExplorer.update();
                payloads[0] = tegraExplorer;

                if (tegraExplorer != null) {
                    if (!payloadExists(tegraExplorer)) {
                        downloadPayload(tegraExplorer);
                    }
                } else {
                    if (tegraExplorer != null) {
                        if (!getPayloadVersion(tegraExplorer).equals(tegraExplorer.getVersion()) && !tegraExplorer.getVersion().equals("")) {
                            new File(payloadDir.getAbsolutePath() + "\\TegraExplorer.bin").delete();
                            downloadPayload(tegraExplorer);
                        }
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
                    writer.append(payload.getName() + "Version=" + payload.getVersion());
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
