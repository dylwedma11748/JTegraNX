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

    public static File TegraRcmSmash;

    public static void loadResources() {
        TegraRcmSmash = load("/jtegranx/res/TegraRcmSmash.exe");
        TegraRcmSmash = rename(TegraRcmSmash, "TegraRcmSmash.exe");
        TegraRcmSmash.deleteOnExit();
    }

    private static File load(String resource) {
        try {
            input = ResourceLoader.class.getResourceAsStream(resource);
            bInput = new BufferedInputStream(input);
            output = File.createTempFile(resource.substring(0, resource.indexOf(".")), resource.substring(resource.indexOf(".")));
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
