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
import java.io.File;
import javax.swing.ImageIcon;
import jtegranx.gui.MainGUI;

public class Threads {

    public static class AutoInjectorThread extends Thread {

        private volatile boolean abort, isRunning = false;
        private volatile Component parent;

        public AutoInjectorThread(Component parent) {
            this.parent = parent;
        }

        public void abort() {
            this.isRunning = false;
            this.abort = true;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            this.isRunning = true;

            while (!this.abort) {
                if (MainGUI.rcmDetected && MainGUI.AutoInject.isSelected()) {
                    if (new File(MainGUI.PayloadPath.getText()).exists()) {
                        TegraRCM.injectPayload(this.parent, MainGUI.PayloadPath.getText(), MainGUI.Arguments.getText());
                    }
                }
            }
        }
    }

    public static class StatusUpdaterThread extends Thread {

        private volatile boolean abort;

        public void abort() {
            this.abort = true;
        }

        @Override
        public void run() {
            while (!this.abort) {
                boolean deviceDetected = TegraRCM.detectRCMDevice();

                if (deviceDetected) {
                    if (!MainGUI.injected) {
                        MainGUI.RCMStatus.setIcon(new ImageIcon(getClass().getResource("/jtegranx/gui/images/rcm_detected.png")));
                        MainGUI.rcmDetected = true;

                        if (!MainGUI.AutoInject.isSelected() && !MainGUI.injected) {
                            MainGUI.Inject.setEnabled(true);
                        }
                    }
                } else {
                    MainGUI.RCMStatus.setIcon(new ImageIcon(getClass().getResource("/jtegranx/gui/images/rcm_undetected.png")));
                    MainGUI.injected = false;
                    MainGUI.rcmDetected = false;
                    MainGUI.Inject.setEnabled(false);
                }
            }
        }
        
        
    }
}
