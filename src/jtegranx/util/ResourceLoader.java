package jtegranx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourceLoader {

    private static InputStream input;
    private static BufferedInputStream bInput;
    private static FileOutputStream fos;
    private static BufferedOutputStream bos;
    private static File output;

    private static final int bufferSize = 1024;

    // JTegraNX directory for resources
    public static File jtegranxdir;

    // Directory for memloader resources
    public static File memloaderdir;

    // Stored Resources
    public static File TegraRcmSmash;
    public static File memloader_usb;
    public static File uboot;
    public static File[] ums_sd = {null, null, null};

    public static final String dir = System.getProperty("user.dir");

    public static void loadResources() {
        jtegranxdir = new File(dir + "\\jtegranx");
        memloaderdir = new File(jtegranxdir.getAbsolutePath() + "\\memloader");

        if (!jtegranxdir.exists()) {
            jtegranxdir.mkdir();
            TegraRcmSmash = extract("TegraRcmSmash.exe");
        } else {
            TegraRcmSmash = new File(jtegranxdir.getAbsolutePath() + "\\TegraRcmSmash.exe");

            if (!TegraRcmSmash.exists()) {
                TegraRcmSmash = extract("TegraRcmSmash.exe");
            }
        }

        if (!memloaderdir.exists()) {
            memloaderdir.mkdir();
            memloader_usb = extract2("memloader_usb.bin");
            uboot = extract2("u-boot.elf");
            ums_sd[0] = extract2("ums_sd.ini");
            ums_sd[1] = extract2("ums_sd.scr");
            ums_sd[2] = extract2("ums_sd.scr.img");
        } else {
            memloader_usb = new File(memloaderdir.getAbsolutePath() + "\\memloader_usb.bin");
            uboot = new File(memloaderdir.getAbsolutePath() + "\\u-boot.elf");
            ums_sd[0] = new File(memloaderdir.getAbsolutePath() + "\\ums_sd.ini");
            ums_sd[1] = new File(memloaderdir.getAbsolutePath() + "\\ums_sd.scr");
            ums_sd[2] = new File(memloaderdir.getAbsolutePath() + "\\ums_sd.scr.img");

            if (!memloader_usb.exists()) {
                memloader_usb = extract2("memloader_usb.bin");
            }

            if (!uboot.exists()) {
                uboot = extract2("u-boot.elf");
            }

            if (!ums_sd[0].exists()) {
                ums_sd[0] = extract2("ums_sd.ini");
            }

            if (!ums_sd[1].exists()) {
                ums_sd[1] = extract2("ums_sd.scr");
            }

            if (!ums_sd[2].exists()) {
                ums_sd[2] = extract2("ums_sd.scr.img");
            }
        }
    }

    public static File extract(String file) {
        return rename(load(file), dir + "\\jtegranx\\" + file);
    }

    public static File extract2(String file) {
        return rename(load(file), dir + "\\jtegranx\\memloader\\" + file);
    }

    private static File load(String resource) {
        try {
            input = ResourceLoader.class.getResourceAsStream("/jtegranx/res/" + resource);
            bInput = new BufferedInputStream(input);
            output = File.createTempFile(resource, null);
            fos = new FileOutputStream(output);
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[bufferSize];

            while (bInput.read(buffer) != -1) {
                bos.write(buffer, 0, bufferSize);
            }

            bInput.close();
            bos.close();
        } catch (IOException ex) {
            Logger.getLogger(ResourceLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    private static File rename(File input, String dest) {
        if (input.renameTo(new File(dest))) {
            return new File(dest);
        }

        return null;
    }
}
