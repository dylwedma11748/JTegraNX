package jtegranx.util;

import java.awt.Component;
import jtegranx.gui.MainGUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import static jtegranx.util.ResourceLoader.*;

public class TegraRCM {

    public static boolean detectRCMDevice() {
        Context context = new Context();
        int result = LibUsb.init(context);

        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);

        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        for (Device device : list) {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);

            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to read device descriptor", result);
            }

            DeviceHandle handle = new DeviceHandle();
            result = LibUsb.open(device, handle);

            if (result != LibUsb.SUCCESS) {
                handle = null;
            }

            if (descriptor.dump(handle).contains("APX")) {
                LibUsb.freeDeviceList(list, true);
                LibUsb.exit(context);

                return true;
            }
        }

        LibUsb.freeDeviceList(list, true);
        LibUsb.exit(context);

        return false;
    }

    public static void injectPayload(Component p, String payload, String[] args) {
        if (TegraRcmSmash.exists() && TegraRcmSmash.canExecute()) {
            if (new File(payload).exists()) {
                try {
                    String command = "\"" + TegraRcmSmash.getAbsolutePath() + "\" " + "\"" + payload + "\"";

                    if (args != null) {
                        for (String arg : args) {
                            command = command.concat(" " + arg);
                        }
                    }

                    Process process = Runtime.getRuntime().exec(command);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;

                        while ((line = reader.readLine()) != null) {
                            String l = line.replaceAll("\\W", "");

                            if (l.startsWith("RCM")) {
                                String id = l.substring(15, 47);
                                MainGUI.Log.append("\nRCM Device with id " + id + " initialized successfully!");
                            }

                            if (l.contains("Switched")) {
                                MainGUI.Log.append("\nSwitched to high buffer");
                            } else if (l.contains("Smashing")) {
                                MainGUI.Log.append("\nSmashing the stack!");
                            } else if (l.contains("Smashed")) {
                                MainGUI.Log.append("\nSmashed the stack with a 0x7000 byte SETUP request!");
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(TegraRCM.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String m = "Specified payload not found";
                String t = "JTegraNX Error";
                JOptionPane.showMessageDialog(p, m, t, 0);
            }
        } else {
            String m = "Unable to find or execute TegraRcmSmash. Re-extract?";
            String t = "JTegraNX Error";
            int omt = 0;
            String[] o = {"Yes", "No"};
            int c = JOptionPane.showOptionDialog(p, m, t, omt, omt, null, o, null);

            if (c == omt) {
                loadResources();
            }
        }
    }
}
