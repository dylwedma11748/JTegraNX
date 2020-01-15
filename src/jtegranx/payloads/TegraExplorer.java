package jtegranx.payloads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class TegraExplorer {

    private static String latestVersion;
    private static final String github = "https://github.com/suchmememanyskill/TegraExplorer/releases";
    private static boolean success = false;

    public static Payload update() {
        if (success) {
            return new Payload("TegraExplorer", latestVersion, github + "/download/" + latestVersion + "/TegraExplorer.bin");
        }
        
        return null;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static void checkForUpdates() {
        try {
            URL url = new URL(github);
            URLConnection connection = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                String line;

                while ((line = bReader.readLine()) != null) {
                    if (line.contains("<a href=\"/suchmememanyskill/TegraExplorer/releases/tag/")) {
                        latestVersion = line.substring(line.indexOf("tag/") + 4, line.indexOf("\">"));
                        success = true;
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to check for updates on TegraExplorer. Reason: " + ex.getClass().getName() + " was thrown!");
            latestVersion = "";
        }
    }
}
