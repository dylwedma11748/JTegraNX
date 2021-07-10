/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2021 Dylan Wedman

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
package linux;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.HotplugCallback;
import org.usb4java.HotplugCallbackHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import ui.UIGlobal;

public class LinuxDeviceListener {

    private static LinuxDeviceListener.EventHandlingThread globalThread;
    private static HotplugCallbackHandle callbackHandle;

    private static final short vendorID = 0x0955;
    private static final short productID = 0x7321;

    static class EventHandlingThread extends Thread {

        private volatile boolean abort;

        public void abort() {
            this.abort = true;
        }

        @Override
        public void run() {
            while (!this.abort) {
                int result = LibUsb.handleEventsTimeout(null, 1000000);

                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to handle hotplug events", result);
                }
            }
        }
    }

    static class Callback implements HotplugCallback {

        @Override
        public int processEvent(Context context, Device device, int event, Object userData) {
            if (event == LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED) {
                UIGlobal.setRCMStatus("RCM_DETECTED");
            } else if (event == LibUsb.HOTPLUG_EVENT_DEVICE_LEFT) {
                UIGlobal.setRCMStatus("RCM_UNDETECTED");
            }

            return 0;
        }
    }

    public static void startDeviceListener() {
        int result = LibUsb.init(null);

        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        if (!LibUsb.hasCapability(LibUsb.CAP_HAS_HOTPLUG)) {
            UIGlobal.appendLog("Hotplug not supported on this system\nUnable to start Linux Device Listener\nRCM functionality is unavailable");
        } else {
            globalThread = new LinuxDeviceListener.EventHandlingThread();
            globalThread.start();

            callbackHandle = new HotplugCallbackHandle();
            result = LibUsb.hotplugRegisterCallback(null, LibUsb.HOTPLUG_EVENT_DEVICE_ARRIVED | LibUsb.HOTPLUG_EVENT_DEVICE_LEFT, LibUsb.HOTPLUG_ENUMERATE, vendorID, productID, LibUsb.HOTPLUG_MATCH_ANY, new LinuxDeviceListener.Callback(), null, callbackHandle);

            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to register hotplug callback", result);
            }
        }
    }

    public static void closeLinuxDeviceListener() {
        try {
            globalThread.abort();
            LibUsb.hotplugDeregisterCallback(null, callbackHandle);
            globalThread.join();
            LibUsb.exit(null);
        } catch (InterruptedException ex) {
            Logger.getLogger(LinuxDeviceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
