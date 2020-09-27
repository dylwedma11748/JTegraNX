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

import java.io.File;

public class Directories {

    private static File jtegranxdir;
    private static File memloaderdir;
    private static File configDir;
    private static File payloadDir;

    public static void initDirectories() {
        jtegranxdir = new File(System.getProperty("user.dir") + "\\jtegranx");
        memloaderdir = new File(jtegranxdir.getAbsolutePath() + "\\memloader");
        configDir = new File(jtegranxdir.getAbsolutePath() + "\\configs");
        payloadDir = new File(jtegranxdir.getAbsolutePath() + "\\payloads");

        File[] directories = {jtegranxdir, memloaderdir, configDir, payloadDir};

        for (File dir : directories) {
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
    }

    public static File getJtegranxdir() {
        return jtegranxdir;
    }

    public static File getMemloaderdir() {
        return memloaderdir;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static File getPayloadDir() {
        return payloadDir;
    }
}
