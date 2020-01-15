package jtegranx.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Downloader {

    public static void downloadFile(URL download, String filePath, boolean silent) {
        new Thread() {
            @Override
            public void run() {
                try {
                    try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                        byte[] buffer = new byte[1024];
                        int bytes;

                        while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                            fos.write(buffer, 0, bytes);
                        }

                        if (!silent) {
                            showMessageDialog(null, "Download Complete", "Downloader", INFORMATION_MESSAGE);
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Unable to download \"" + download + "\". Reason: " + ex.getClass().getName() + " was thrown!");
                }
            }
        }.start();
    }
}
