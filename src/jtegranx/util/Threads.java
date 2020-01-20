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
