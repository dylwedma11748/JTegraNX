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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Lockpick_RCM {

    private static String latestVersion;
    private static final String GITHUB = "https://github.com/shchmue/Lockpick_RCM/releases";
    private static boolean success = false;

    public static Payload update() {
        if (success) {
            return new Payload("Lockpick_RCM", latestVersion, GITHUB + "/download/" + latestVersion + "/Lockpick_RCM.bin");
        }

        return null;
    }

    public static void checkForUpdates() {
        try {
            System.out.println("Checking for updates on Lockpick_RCM.");
            URL url = new URL(GITHUB);
            URLConnection connection = url.openConnection();

            try (InputStreamReader isr = new InputStreamReader(connection.getInputStream()); BufferedReader bReader = new BufferedReader(isr)) {
                String line;

                while ((line = bReader.readLine()) != null) {
                    if (line.contains("<a href=\"/shchmue/Lockpick_RCM/releases/tag/")) {
                        latestVersion = line.substring(line.indexOf("tag/") + 4, line.indexOf("\">"));
                        success = true;
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Unable to check for updates on Lockpick_RCM. Reason: " + ex.getClass().getName() + " was thrown!");
            latestVersion = "";
        }
    }
}
