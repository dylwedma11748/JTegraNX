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

import java.awt.Component;
import jtegranx.gui.MainGUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import static jtegranx.util.ResourceLoader.*;
import static javax.swing.JOptionPane.*;

public class TegraRCM {

    private static final short vendorID = 0x0955;
    private static final short productID = 0x7321;

    public static boolean detectRCMDevice() {
        int result = LibUsb.init(null);

        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, vendorID, productID);

        if (handle != null) {
            LibUsb.close(handle);
            return true;
        }

        return false;
    }

    public static void injectPayload(Component p, String payload, String args) {
        if (TegraRcmSmash.exists() && TegraRcmSmash.canExecute()) {
            if (new File(payload).exists()) {
                try {
                    String command = "\"" + TegraRcmSmash.getAbsolutePath() + "\" " + "\"" + payload + "\" " + args;

                    Process process = Runtime.getRuntime().exec(command);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            String l = line.replaceAll("\\W", "");

                            if (l.contains("Smashing")) {
                                MainGUI.Log.append("\nSmashing the stack!");
                            } else if (l.contains("Smashed")) {
                                MainGUI.Log.append("\nSmashed the stack with a SETUP request!");
                                MainGUI.injected = true;
                                MainGUI.Inject.setEnabled(false);
                                MainGUI.RCMStatus.setIcon(new ImageIcon(TegraRCM.class.getClass().getResource("/jtegranx/gui/images/loaded.png")));
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TegraRCM.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String m = "Specified payload not found";
                String t = "JTegraNX Error";
                showMessageDialog(p, m, t, 0);
            }
        } else {
            String m = "Unable to find or execute TegraRcmSmash. Re-extract?";
            String t = "JTegraNX Error";
            int omt = 0;
            String[] o = {"Yes", "No"};
            int c = showOptionDialog(p, m, t, omt, omt, null, o, null);

            if (c == omt) {
                TegraRcmSmash = extract("TegraRcmSmash.exe");
            }
        }
    }
}
