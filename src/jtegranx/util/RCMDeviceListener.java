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

import java.util.List;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHub;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import jtegranx.fx.JTegraNX;

public class RCMDeviceListener implements UsbServicesListener {

    // Nintendo Switch's ID's
    private static final short VENDOR_ID = 0x0955;
    private static final short PRODUCT_ID = 0x7321;

    public RCMDeviceListener(UsbHub hub) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();

            if (descriptor.idVendor() == VENDOR_ID && descriptor.idProduct() == PRODUCT_ID) {
                JTegraNX.getController().setRcmStatusImage(StatusImages.RCM_DETECTED);
                JTegraNX.getController().setRcmStatus(true);

                if (JTegraNX.getController().autoInjectEnabled()) {
                    JTegraNX.getController().injectAction();
                }

                break;
            } else {
                JTegraNX.getController().setRcmStatusImage(StatusImages.RCM_UNDETECTED);
                JTegraNX.getController().setRcmStatus(false);
            }
        }
    }

    @Override
    public void usbDeviceAttached(UsbServicesEvent event) {
        UsbDevice device = event.getUsbDevice();
        UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();

        if (descriptor.idVendor() == VENDOR_ID && descriptor.idProduct() == PRODUCT_ID) {
            JTegraNX.getController().setRcmStatusImage(StatusImages.RCM_DETECTED);
            JTegraNX.getController().setRcmStatus(true);

            if (JTegraNX.getController().autoInjectEnabled()) {
                JTegraNX.getController().injectAction();
            }
        }
    }

    @Override
    public void usbDeviceDetached(UsbServicesEvent event) {
        UsbDevice device = event.getUsbDevice();
        UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();

        if (descriptor.idVendor() == VENDOR_ID && descriptor.idProduct() == PRODUCT_ID) {
            JTegraNX.getController().setRcmStatusImage(StatusImages.RCM_UNDETECTED);
            JTegraNX.getController().setRcmStatus(false);
        }
    }
}
