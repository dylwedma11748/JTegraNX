package jtegranx.payloads;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Hekate {

    private static String latestVersion;
    private static String latestNyxVersion;
    private static final String GITHUB = "https://github.com/CTCaer/hekate/releases";
    private static boolean success = false;

    public static Payload update() {
        if (success) {
            return new Payload("Hekate", latestVersion, GITHUB + "/download/v" + latestVersion + "/hekate_ctcaer_" + latestVersion + "_Nyx_" + latestNyxVersion + ".zip");
        }

        return null;
    }

    public static void checkForUpdates() {
        try {
            URL url = new URL(GITHUB);
            URLConnection connection = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                String line;

                while ((line = bReader.readLine()) != null) {
                    if (line.contains("<a href=\"/CTCaer/hekate/releases/tag/")) {
                        latestVersion = line.substring(line.indexOf("tag/") + 5, line.indexOf("\">"));
                        latestNyxVersion = line.substring(line.indexOf("Nyx") + 5, line.length() - 4);
                        success = true;
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Unable to check for updates on Hekate. Reason: " + ex.getClass().getName() + " was thrown!");
            latestVersion = "";
        }
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
    }
}
