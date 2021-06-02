/*

JTegraNX - Another RCM payload injector

Copyright (C) 2021 Dylan Wedman

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
package util;

import java.io.File;

public class GlobalSettings {

    public static final String OS_ARCH = System.getProperty("os.arch");

    public static final String JTEGRANX_DIR_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "JTegraNX";
    public static final File JTEGRANX_DIR = new File(JTEGRANX_DIR_PATH);
    public static final String JTEGRANX_PAYLOAD_DIR_PATH = JTEGRANX_DIR_PATH + File.separator + "Payloads";
    public static final File JTEGRANX_PAYLOAD_DIR = new File(JTEGRANX_PAYLOAD_DIR_PATH);
    public static final File JTEGRANX_CONFIG_FILE = new File(JTEGRANX_DIR_PATH + File.separator + "JTegraNX.ini");

    public static String savedFolderPath;
    public static String savedPayloadPath;
    public static Object selectedConfig;

    public static int selectedPayloadCount;

    public static String fuseePrimaryTag;
    public static String hekateTag;
    public static String lockpickRCMTag;
    public static String tegraExplorerTag;

    public static boolean autoInject = false;
    public static boolean checkJTegraNXUpdates = true;
    public static boolean checkPayloadUpdates = true;
    public static boolean enableTrayIcon = false;
    public static boolean includeFuseePrimary = true;
    public static boolean includeHekate = true;
    public static boolean includeLockpickRCM = true;
    public static boolean includeTegraExplorer = true;
    public static boolean minimizeToTray = false;
    public static boolean payloadsUpdatedThisSession = false;
    public static boolean driverUpdatedNeedsReconnect = false;
}
