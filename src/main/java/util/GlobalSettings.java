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
package util;

import java.io.File;

import org.kohsuke.github.GitHub;

public class GlobalSettings {
    
	public static final String GITHUB_ACCESS_TOKEN = "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";
	
	public static final String JRE_ARCH = System.getProperty("sun.arch.data.model");

    public static final String STANDARD_MODE_JTEGRANX_DIR_PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "JTegraNX";
    public static final File STANDARD_MODE_JTEGRANX_DIR = new File(STANDARD_MODE_JTEGRANX_DIR_PATH);
    public static final String STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH = STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "Payloads";
    public static final File STANDARD_MODE_JTEGRANX_PAYLOAD_DIR = new File(STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
    public static final File STANDARD_MODE_JTEGRANX_CONFIG_FILE = new File(STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "JTegraNX.ini");

    public static final String PORTABLE_MODE_JTEGRANX_DIR_PATH = System.getProperty("user.dir");
    public static final File PORTABLE_MODE_JTEGRANX_DIR = new File(PORTABLE_MODE_JTEGRANX_DIR_PATH);
    public static final String PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH = PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "Payloads";
    public static final File PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR = new File(PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
    public static final File PORTABLE_MODE_JTEGRANX_CONFIG_FILE = new File(PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "JTegraNX.ini");
    
    public static boolean OFFLINE_MODE = false;
    public static boolean CTEGRANX_ACTIVE = false;
    
    public static GitHub gitHub;
    
    public static String gptRestorePath;
    public static String lastSelectedBundledPayload;

    public static String savedFolderPath;
    public static String savedPayloadPath;
    public static Object selectedConfig;

    public static int selectedPayloadCount;

    public static String fuseeTag;
    public static String hekateTag;
    public static String lockpickRCMTag;
    public static String tegraExplorerTag;

    public static boolean autoInject = false;
    public static boolean checkJTegraNXUpdates = true;
    public static boolean checkPayloadUpdates = true;
    public static boolean enableTrayIcon = false;
    public static boolean includeFusee = true;
    public static boolean includeHekate = true;
    public static boolean includeLockpickRCM = true;
    public static boolean includeTegraExplorer = true;
    public static boolean minimizeToTray = false;
    public static boolean payloadsUpdatedThisSession = false;
    public static boolean portableMode = false;
    public static boolean driverUpdatedNeedsReconnect = false;
    public static boolean restartPending = false;
    public static boolean commandLineMode = false;
}
