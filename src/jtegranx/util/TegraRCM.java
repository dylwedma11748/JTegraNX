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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jtegranx.util.ResourceLoader.*;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import jtegranx.fx.JTegraNX;

public class TegraRCM {

    private static RCMDeviceListener listener;
    private static UsbServices services;

    public static void initRCMDeviceListener() {
        try {
            services = UsbHostManager.getUsbServices();
            listener = new RCMDeviceListener(services.getRootUsbHub());
            services.addUsbServicesListener(listener);
        } catch (UsbException | SecurityException ex) {
            Logger.getLogger(TegraRCM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void closeRCMDeviceListener() {
        services.removeUsbServicesListener(listener);
    }

    public static void injectPayload(String payload, String args) {
        if (TegraRcmSmash.exists() && TegraRcmSmash.canExecute()) {
            JTegraNX.getController().clearLog();

            if (new File(payload).exists()) {
                try {
                    String command = "\"" + TegraRcmSmash.getAbsolutePath() + "\" " + "\"" + payload + "\" " + args;

                    Process process = Runtime.getRuntime().exec(command);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            JTegraNX.getController().appendLog(line);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TegraRCM.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JTegraNX.getController().appendLog("Payload not found!");
            }
        } else {
            JTegraNX.getController().appendLog("TegraRcmSmash not found or cannot be executed! \nRe-extracting TegraRCMSmash.");
            loadResources();
            injectPayload(payload, args);
        }
    }
}
