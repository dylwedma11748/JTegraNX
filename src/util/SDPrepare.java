/*

JTegraNX - Another RCM payload injector

Copyright (C) 2019-2021 Dylan Wedman

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

import handlers.ZipHandler;
import git.GitHandler;
import git.Release;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import ui.UIGlobal;

public class SDPrepare {

    private static Release hekate;
    private static Release atmosphere;
    private static Release lockpickRCM;
    private static Release checkpoint;
    private static Release ftpd;
    private static Release nxThemeInstaller;
    private static Release nxShell;
    private static Release hbAppStore;

    private static final String HEKATE_IPL = "https://nh-server.github.io/switch-guide/files/emu/hekate_ipl.ini";
    private static final String EMUMMC = "https://nh-server.github.io/switch-guide/files/emummc.txt";
    private static final String BOOTLOGOS = "https://nh-server.github.io/switch-guide/files/bootlogos.zip";

    private static File tempDir;

    private static File sdRoot;
    private static File atmosphere_zip;
    private static File hekate_zip;
    private static File bootlogos_zip;
    private static File hekate_ipl_file;
    private static File lockpickRCM_file;
    private static File emummc_file;
    private static File appstore_file;
    private static File checkpoint_file;
    private static File ftpd_file;
    private static File nxShell_file;
    private static File nxThemeInstaller_file;

    private static void downloadFiles() {
        if (!GlobalSettings.portableMode) {
            tempDir = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "SD");
        } else {
            tempDir = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "SD");
        }

        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        hekate = GitHandler.generateLatestRelease("https://github.com/CTCaer/hekate/releases");

        hekate.getAssets().stream().filter((asset) -> (asset.getAssetName().contains("hekate_ctcaer"))).map((asset) -> {
            UIGlobal.appendLog("Downloading Hekate");
            return asset;
        }).forEachOrdered((asset) -> {
            hekate_zip = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        atmosphere = GitHandler.generateLatestRelease("https://github.com/Atmosphere-NX/Atmosphere/releases");

        atmosphere.getAssets().stream().filter((asset) -> (!asset.getAssetName().contains("WITHOUT_MESOSPHERE") && asset.getAssetName().contains("atmosphere"))).map((asset) -> {
            UIGlobal.appendLog("Downloading Atmosphère");
            return asset;
        }).forEachOrdered((asset) -> {
            atmosphere_zip = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        lockpickRCM = GitHandler.generateLatestRelease("https://github.com/shchmue/Lockpick_RCM/releases");

        lockpickRCM.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Lockpick_RCM.bin"))).map((asset) -> {
            UIGlobal.appendLog("Downloading Lockpick_RCM");
            return asset;
        }).forEachOrdered((asset) -> {
            lockpickRCM_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        checkpoint = GitHandler.generateLatestRelease("https://github.com/FlagBrew/Checkpoint/releases");

        checkpoint.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("Checkpoint.nro"))).map((asset) -> {
            UIGlobal.appendLog("Downloading Checkpoint");
            return asset;
        }).forEachOrdered((asset) -> {
            checkpoint_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        ftpd = GitHandler.generateLatestRelease("https://github.com/mtheall/ftpd/releases");

        ftpd.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("ftpd.nro"))).map((asset) -> {
            UIGlobal.appendLog("Downloading ftpd");
            return asset;
        }).forEachOrdered((asset) -> {
            ftpd_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        nxThemeInstaller = GitHandler.generateLatestRelease("https://github.com/exelix11/SwitchThemeInjector/releases");

        nxThemeInstaller.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("NXThemesInstaller.nro"))).map((asset) -> {
            UIGlobal.appendLog("Downloading NXThemesInstaller");
            return asset;
        }).forEachOrdered((asset) -> {
            nxThemeInstaller_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        nxShell = GitHandler.generateLatestRelease("https://github.com/joel16/NX-Shell/releases");

        nxShell.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("NX-Shell.nro"))).map((asset) -> {
            UIGlobal.appendLog("Downloading NX-Shell");
            return asset;
        }).forEachOrdered((asset) -> {
            nxShell_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        hbAppStore = GitHandler.generateLatestRelease("https://github.com/fortheusers/hb-appstore/releases");

        hbAppStore.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("appstore.nro"))).map((asset) -> {
            UIGlobal.appendLog("Downloading hbappstore");
            return asset;
        }).forEachOrdered((asset) -> {
            appstore_file = GitHandler.downloadAsset(asset, tempDir.getAbsolutePath() + File.separator + asset.getAssetName());
        });

        UIGlobal.appendLog("Downloading Hekate config file");
        hekate_ipl_file = downloadDirectLinkFile(HEKATE_IPL, tempDir.getAbsolutePath() + File.separator + "hekate_ipl.ini");
        UIGlobal.appendLog("Downloading 90dns DNS redirecton config file");
        emummc_file = downloadDirectLinkFile(EMUMMC, tempDir.getAbsolutePath() + File.separator + "emummc.txt");
        UIGlobal.appendLog("Downloading bootlogos");
        bootlogos_zip = downloadDirectLinkFile(BOOTLOGOS, tempDir.getAbsolutePath() + File.separator + "bootlogos.zip");
    }

    private static void extractAndMoveFiles() {
        UIGlobal.appendLog("Extracting archives");
        ZipHandler.unzip(atmosphere_zip.getAbsolutePath(), tempDir.getAbsolutePath());
        ZipHandler.unzip(hekate_zip.getAbsolutePath(), tempDir.getAbsolutePath());
        ZipHandler.unzip(bootlogos_zip.getAbsolutePath(), tempDir.getAbsolutePath());

        UIGlobal.appendLog("Copying files");
        hekate_ipl_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "bootloader" + File.separator + "hekate_ipl.ini"));
        lockpickRCM_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "bootloader" + File.separator + "payloads" + File.separator + "Lockpick_RCM.bin"));

        File hostsDir = new File(tempDir.getAbsolutePath() + File.separator + "atmosphere" + File.separator + "hosts");
        hostsDir.mkdir();
        emummc_file.renameTo(new File(hostsDir.getAbsolutePath() + File.separator + "emummc.txt"));

        File appstoreDir = new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "appstore");
        appstoreDir.mkdir();
        appstore_file.renameTo(new File(appstoreDir.getAbsolutePath() + File.separator + "appstore.nro"));

        checkpoint_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "Checkpoint.nro"));
        ftpd_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "ftpd.nro"));
        nxShell_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "NX-Shell.nro"));
        nxThemeInstaller_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "NXThemesInstaller.nro"));

        try {
            FileUtils.copyDirectory(new File(tempDir.getAbsolutePath() + File.separator + "atmosphere"), new File(sdRoot.getAbsolutePath() + File.separator + "atmosphere"));
            FileUtils.copyDirectory(new File(tempDir.getAbsolutePath() + File.separator + "bootloader"), new File(sdRoot.getAbsolutePath() + File.separator + "bootloader"));
            FileUtils.copyDirectory(new File(tempDir.getAbsolutePath() + File.separator + "sept"), new File(sdRoot.getAbsolutePath() + File.separator + "sept"));
            FileUtils.copyDirectory(new File(tempDir.getAbsolutePath() + File.separator + "switch"), new File(sdRoot.getAbsolutePath() + File.separator + "switch"));
        } catch (IOException ex) {
            Logger.getLogger(SDPrepare.class.getName()).log(Level.SEVERE, null, ex);
        }

        File hbMenu = new File(tempDir.getAbsolutePath() + File.separator + "hbmenu.nro");
        hbMenu.renameTo(new File(sdRoot.getAbsolutePath() + File.separator + "hbmenu.nro"));
    }

    private static void cleanUp() {
        try {
            UIGlobal.appendLog("Cleaning up");
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException ex) {
            Logger.getLogger(SDPrepare.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static File downloadDirectLinkFile(String downloadLink, String desination) {
        File output = new File(desination);

        try (BufferedInputStream bis = new BufferedInputStream(new URL(downloadLink).openStream()); FileOutputStream fos = new FileOutputStream(output)) {
            byte[] buffer = new byte[1024];
            int bytes;

            while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, bytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(SDPrepare.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static void prepareSDCard(File sdDir) {
        new Thread("SD Prepare") {
            @Override
            public void run() {
                sdRoot = sdDir;
                downloadFiles();
                extractAndMoveFiles();
                cleanUp();
                UIGlobal.appendLog("SD preparation complete");
            }
        }.start();
    }
}
