/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2022 Dylan Wedman

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
package rcm;

import handlers.AlertHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;

import ui.JTegraNX;
import ui.UIGlobal;
import util.GlobalSettings;
import util.Tray;

public class RCM {

    private static boolean writeFailed = false;

    private static final short vendorID = 0x0955;
    private static final short productID = 0x7321;

    private static final byte[] initSequence = {(byte) 0x98, (byte) 0x02, (byte) 0x03};

    private static final byte[] intermezzo = {
        (byte) 0x5c, (byte) 0x00, (byte) 0x9f, (byte) 0xe5, (byte) 0x5c, (byte) 0x10, (byte) 0x9f, (byte) 0xe5, (byte) 0x5c, (byte) 0x20, (byte) 0x9f, (byte) 0xe5, (byte) 0x01, (byte) 0x20, (byte) 0x42, (byte) 0xe0,
        (byte) 0x0e, (byte) 0x00, (byte) 0x00, (byte) 0xeb, (byte) 0x48, (byte) 0x00, (byte) 0x9f, (byte) 0xe5, (byte) 0x10, (byte) 0xff, (byte) 0x2f, (byte) 0xe1, (byte) 0x00, (byte) 0x00, (byte) 0xa0, (byte) 0xe1,
        (byte) 0x48, (byte) 0x00, (byte) 0x9f, (byte) 0xe5, (byte) 0x48, (byte) 0x10, (byte) 0x9f, (byte) 0xe5, (byte) 0x01, (byte) 0x29, (byte) 0xa0, (byte) 0xe3, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0xeb,
        (byte) 0x38, (byte) 0x00, (byte) 0x9f, (byte) 0xe5, (byte) 0x01, (byte) 0x19, (byte) 0xa0, (byte) 0xe3, (byte) 0x01, (byte) 0x00, (byte) 0x80, (byte) 0xe0, (byte) 0x34, (byte) 0x10, (byte) 0x9f, (byte) 0xe5,
        (byte) 0x03, (byte) 0x28, (byte) 0xa0, (byte) 0xe3, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xeb, (byte) 0x20, (byte) 0x00, (byte) 0x9f, (byte) 0xe5, (byte) 0x10, (byte) 0xff, (byte) 0x2f, (byte) 0xe1,
        (byte) 0x04, (byte) 0x30, (byte) 0x91, (byte) 0xe4, (byte) 0x04, (byte) 0x30, (byte) 0x80, (byte) 0xe4, (byte) 0x04, (byte) 0x20, (byte) 0x52, (byte) 0xe2, (byte) 0xfb, (byte) 0xff, (byte) 0xff, (byte) 0x1a,
        (byte) 0x1e, (byte) 0xff, (byte) 0x2f, (byte) 0xe1, (byte) 0x00, (byte) 0xf0, (byte) 0x00, (byte) 0x40, (byte) 0x20, (byte) 0x00, (byte) 0x01, (byte) 0x40, (byte) 0x7c, (byte) 0x00, (byte) 0x01, (byte) 0x40,
        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x40, (byte) 0x40, (byte) 0x0e, (byte) 0x01, (byte) 0x40, (byte) 0x00, (byte) 0x70, (byte) 0x01, (byte) 0x40
    };

    private static final byte[] spreadPattern = {0x00, 0x00, 0x01, 0x40};
    
    // RCM Device Stored for easy access
    public static DeviceHandle rcmDevice = null;

    // Device listener for Windows
    public static native void startDeviceListener();
    public static native void closeDeviceListener();

    // Smash stack for Windows
    private static native boolean smashTheStack();

    // Smash stack for Linux
    private static native boolean smashTheStack(final int bus_id, final int device_address);

    // Smash stack for macOS
    private static int smashTheStack(DeviceHandle handle) {
        ByteBuffer writeBuffer = ByteBuffer.allocateDirect(28672);
        return LibUsb.controlTransfer(handle, (byte) 0x82, LibUsb.REQUEST_GET_STATUS, (short) 0, (short) 0, writeBuffer, 1000);
    }

    public static void setRCMStatus(String status) {
        if (!GlobalSettings.commandLineMode) {
            UIGlobal.setRCMStatus(status);
        }
    }

    public static void appendLog(String line) {
        UIGlobal.appendLog(line);
    }

    public static void promptDriverInstall() {
        System.out.println("called");
        
        if (!GlobalSettings.commandLineMode) {
            Platform.runLater(() -> {
                boolean install = AlertHandler.showConfirmationDialog("APX driver missing", "An RCM device was detected but the APX driver is not installed. Install now?", "This driver is required to inject a payload.");

                if (install) {
                    JTegraNX.getController().getInstallAPXDriverMenuItem().fire();
                }
            });
        } else {
            System.out.println("Type \"install apx\" to install the driver");
        }
    }

    public static void promptDeviceReconnect() {
        Platform.runLater(() -> {
            UIGlobal.setDeviceAlert(AlertHandler.createAlert("APX Driver installed", "Please reconnect the RCM device.", "This will prevent any errors while injecting a payload."));
            UIGlobal.getDeviceAlert().show();
        });
    }

    public static void closeDeviceReconnectPrompt() {
        Platform.runLater(() -> {
            if (UIGlobal.getDeviceAlert() != null) {
                UIGlobal.getDeviceAlert().close();
            }
        });
    }

    public static void injectPayload(String payloadPath, boolean ctegranx) {
        if (!GlobalSettings.commandLineMode) {
            new Thread("RCM") {
                @Override
                public void run() {
                    inject(payloadPath, ctegranx);
                }
            }.start();
        } else {
            inject(payloadPath, ctegranx);
        }
    }

    public static void inject(String payloadPath, boolean ctegranx) {
        setRCMStatus("RCM_LOADING");
        byte[] payload = createRCMPayload(payloadPath);

        if (payload != null) {
            Device device = findRCMDevice();

            if (device != null) {
                DeviceHandle handle = openDevice(device);

                if (handle != null) {
                    int returnValue = LibUsb.claimInterface(handle, 0);

                    if (returnValue != LibUsb.SUCCESS) {
                        setRCMStatus("ERROR");
                        appendLog("Failed to get claim interface");
                        appendLog(getErrorCode(returnValue));

                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                            Tray.showNotification(new File(payloadPath).getName() + " injection failed.\n" + getErrorCode(returnValue), Tray.error);
                        }
                    } else {
                        StringBuilder deviceID = readDeviceID(handle);

                        int targetBlockCount = payload.length / 4096;
                        int writeBlockCount = 0;

                        if (deviceID != null) {
                            appendLog(deviceID.toString());
                            appendLog("Sending payload to device");

                            for (int i = 0; i < targetBlockCount; i++) {
                                if (write(handle, Arrays.copyOfRange(payload, i * 4096, (i + 1) * 4096))) {
                                    writeBlockCount++;
                                } else {
                                    writeFailed = true;
                                    break;
                                }
                            }
                        }

                        if (writeFailed) {
                            if (writeBlockCount == targetBlockCount) {
                                appendLog("Sent " + writeBlockCount + " out of " + targetBlockCount + " target blocks");
                                appendLog("Payload sent to device\nSmashing the stack");

                                if (System.getProperty("os.name").contains("Windows")) {
                                    if (smashTheStack()) {
                                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                            Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                        }
                                    } else {
                                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                            Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                        }
                                    }
                                } else if (System.getProperty("os.name").contains("Linux")) {
                                    if (smashTheStack(LibUsb.getBusNumber(device), LibUsb.getDeviceAddress(device))) {
                                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                            Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                        }
                                    } else {
                                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                            Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                        }
                                    }
                                } else if (System.getProperty("os.name").contains("Mac OS X")) {
                                    if (getErrorCode(smashTheStack(handle)).equals("LIBUSB_ERROR_TIMEOUT")) {
                                        setRCMStatus("RCM_LOADED");
                                        appendLog("Smashed the stack");

                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                            }
                                        }
                                    } else {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                            }
                                        }
                                    }
                                }
                            } else {
                                setRCMStatus("ERROR");

                                appendLog("Sent " + writeBlockCount + " out of " + targetBlockCount + " target blocks");
                                appendLog("Failed to send payload to device");

                                if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                    Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to send payload to device", Tray.error);
                                }
                            }
                        } else {
                            if (writeBlockCount == targetBlockCount) {
                                appendLog("Sent " + writeBlockCount + " out of " + targetBlockCount + " target blocks");
                                appendLog("Payload sent to device\nSmashing the stack");

                                if (System.getProperty("os.name").contains("Windows")) {
                                    if (smashTheStack()) {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                            }
                                        }
                                    } else {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                            }
                                        }
                                    }
                                } else if (System.getProperty("os.name").contains("Linux")) {
                                    if (smashTheStack(LibUsb.getBusNumber(device), LibUsb.getDeviceAddress(device))) {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                            }
                                        }
                                    } else {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                            }
                                        }
                                    }
                                } else if (System.getProperty("os.name").contains("Mac OS X")) {
                                    if (getErrorCode(smashTheStack(handle)).equals("LIBUSB_ERROR_TIMEOUT")) {
                                        setRCMStatus("RCM_LOADED");
                                        appendLog("Smashed the stack");

                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injected", Tray.info);
                                            }
                                        }
                                    } else {
                                        if (!GlobalSettings.commandLineMode) {
                                            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                                Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to smash to smash the stack", Tray.error);
                                            }
                                        }
                                    }
                                }
                            } else {
                                setRCMStatus("ERROR");

                                appendLog("Sent " + writeBlockCount + " out of " + targetBlockCount + " target blocks");
                                appendLog("Failed to send payload to device");

                                if (!GlobalSettings.commandLineMode) {
                                    if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                                        Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to send payload to device", Tray.error);
                                    }
                                }
                            }
                        }
                    }
                    
                    closeDevice(handle);
                } else {
                    setRCMStatus("ERROR");

                    if (!GlobalSettings.commandLineMode) {
                        if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                            Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to open device", Tray.error);
                        }
                    }
                }
                
            } else {
                setRCMStatus("ERROR");
                appendLog("Failed to get RCM device");

                if (!GlobalSettings.commandLineMode) {
                    if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                        Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to get RCM device", Tray.error);
                    }
                }
            }
        } else {
            setRCMStatus("ERROR");
            appendLog("Failed to create RCM payload");

            if (!GlobalSettings.commandLineMode) {
                if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                    Tray.showNotification(new File(payloadPath).getName() + " injection failed\nFailed to create RCM payload", Tray.error);
                }
            }
        }
    }

    private static byte[] createRCMPayload(String payloadPath) {
        byte[] payload;

        File payloadFile = new File(payloadPath);

        if (!payloadFile.exists()) {
            appendLog("Specified payload doesn't exist");
            return null;
        }

        if (payloadFile.length() > 0x1ed58 || payloadFile.length() < 0x4000) {
            appendLog("Invalid payload file size");
            return null;
        }

        int payloadFileSize = (int) payloadFile.length();
        int totalSize = 0x10e8 + payloadFileSize + 0x21c0;
        totalSize += 0x1000 - (totalSize % 0x1000);

        if ((totalSize / 0x1000 % 0x2) == 0x0) {
            totalSize += 0x1000;
        }

        if (totalSize > 0x30298) {
            appendLog("Payload size is too big");
            return null;
        }

        payload = new byte[totalSize];
        byte[] payloadBytes = new byte[payloadFileSize];

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(payloadFile))) {
            int readSize;

            if ((readSize = bis.read(payloadBytes)) != payloadFileSize) {
                appendLog("Failed to read full payload\nRead " + readSize + " out of " + payloadFileSize + " bytes.");
                return null;
            } else {
                appendLog("Creating RCM payload");
            }

        } catch (IOException ex) {
            Logger.getLogger(RCM.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.arraycopy(initSequence, 0x0, payload, 0x0, 0x3);
        System.arraycopy(intermezzo, 0x0, payload, 0x2a8, 0x7c);
        System.arraycopy(payloadBytes, 0x0, payload, 0x10e8, 0x4000);

        for (int i = 0x0; i < 0x870; i++) {
            System.arraycopy(spreadPattern, 0x0, payload, 0x50e8 + i * 0x4, 0x4);
        }

        System.arraycopy(payloadBytes, 0x4000, payload, 0x72a8, payloadFileSize - 0x4000);

        return payload;
    }

    private static Device findRCMDevice() {
        int returnValue = LibUsb.init(null);

        if (returnValue != LibUsb.SUCCESS) {
            appendLog("Failed to init libusb context");
            appendLog("Error code: " + getErrorCode(returnValue));

            if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                Tray.showNotification("Failed to init libusb context\n" + getErrorCode(returnValue), Tray.error);
            }

            return null;
        } else {
            DeviceList list = new DeviceList();
            returnValue = LibUsb.getDeviceList(null, list);

            if (returnValue < 0) {
                appendLog("Failed to get device list");
                appendLog("Error code: " + getErrorCode(returnValue));

                if (GlobalSettings.enableTrayIcon && JTegraNX.getStage().isIconified()) {
                    Tray.showNotification("Failed to get device list\n" + getErrorCode(returnValue), Tray.error);
                }

                return null;
            } else {
                DeviceDescriptor descriptor = new DeviceDescriptor();

                for (Device device : list) {
                    returnValue = LibUsb.getDeviceDescriptor(device, descriptor);

                    if (returnValue != LibUsb.SUCCESS) {
                        appendLog("Failed to get device descriptor");
                        appendLog("Error code: " + getErrorCode(returnValue));
                        return null;
                    } else {
                        if ((descriptor.idVendor() == vendorID) && descriptor.idProduct() == productID) {
                        	return device;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static DeviceHandle openDevice(Device device) {
        DeviceHandle handle = new DeviceHandle();
        int returnValue = LibUsb.open(device, handle);

        if (returnValue != LibUsb.SUCCESS) {
            appendLog("Failed to open device");
            appendLog("Error code: " + getErrorCode(returnValue));
            return null;
        }

        rcmDevice = handle;
        return handle;
    }
    
    private static void closeDevice(DeviceHandle handle) {
    	LibUsb.releaseInterface(handle, 0);
    	LibUsb.close(handle);
    }

    private static StringBuilder readDeviceID(DeviceHandle handle) {
        StringBuilder deviceID = null;
        ByteBuffer buffer = ByteBuffer.allocateDirect(16);
        IntBuffer bytesTransfered = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(handle, (byte) 0x81, buffer, bytesTransfered, 1000);

        if (result != LibUsb.SUCCESS) {
            setRCMStatus("ERROR");
            appendLog("Failed to read device ID");
            appendLog("Error code: " + getErrorCode(result));
        } else {
            int transfer = bytesTransfered.get();
            byte[] receivedBytes = new byte[transfer];
            buffer.get(receivedBytes);
            deviceID = new StringBuilder("Device ID: ");

            for (byte b : receivedBytes) {
                deviceID.append(String.format("%02X", b));
            }
        }

        return deviceID;
    }

    private static boolean write(DeviceHandle handle, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        buffer.put(data);
        IntBuffer transfered = IntBuffer.allocate(1);
        int result = LibUsb.bulkTransfer(handle, (byte) 0x01, buffer, transfered, 5000);

        if (result == LibUsb.SUCCESS) {
            if (transfered.get() == 4096) {
                return true;
            }
        }

        return false;
    }

    public static String getErrorCode(int value) {
        return LibUsb.errorName(value);
    }
}
