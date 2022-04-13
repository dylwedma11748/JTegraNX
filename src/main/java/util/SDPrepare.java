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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;

import handlers.ZipHandler;
import javafx.concurrent.Task;

public class SDPrepare extends Task<Object> {
	
	private GHRepository hekate;
    private GHRepository atmosphere;
    private GHRepository lockpickRCM;
    private GHRepository jksv;
    private GHRepository ftpd;
    private GHRepository nxThemesInstaller;
    private GHRepository nxShell;
    private GHRepository hbAppStore;
    
    private GHRelease latest_hekate;
    private GHRelease latest_atmosphere;
    private GHRelease latest_lockpickRCM;
    private GHRelease latest_jksv;
    private GHRelease latest_ftpd;
    private GHRelease latest_nxThemesInstaller;
    private GHRelease latest_nxShell;
    private GHRelease latest_hbAppStore;
    
    private GHAsset hekate_zip;
    private GHAsset atmosphere_zip;
    private GHAsset lockpickRCM_bin;
    private GHAsset jksv_nro;
    private GHAsset ftpd_nro;
    private GHAsset nxThemesInstaller_nro;
    private GHAsset nxShell_nro;
    private GHAsset hbAppStore_nro;
    
    private File hekate_zip_file;
    private File atmosphere_zip_file;
    private File lockpickRCM_bin_file;
    private File jksv_nro_file;
    private File ftpd_nro_file;
    private File nxThemesInstaller_nro_file;
    private File nxShell_nro_file;
    private File hbAppStore_nro_file;
    
    private File tempDir;
    
    private final String HEKATE_IPL = "https://nh-server.github.io/switch-guide/files/emu/hekate_ipl.ini";
    private final String EMUMMC = "https://nh-server.github.io/switch-guide/files/emummc.txt";
    private final String BOOTLOGOS = "https://nh-server.github.io/switch-guide/files/bootlogos.zip";
    
    private File hekate_ipl_file;
    private File emummc_txt_file;
    private File bootlogos_zip;
    
    private File sdRoot;
    
    public SDPrepare(File sdRoot) {
    	this.sdRoot = sdRoot;
    }
    
    private void prepareSDCard(File sdDir) {
    	getRepositories();
    	updateReleases();
    	updateAssets();
    	downloadAssets();
    	extractArchives();
    	copyFiles(sdDir);
    	cleanUp();
    }
    
    private void getRepositories() {
    	try {
			updateTitle("Getting repositories");
			
			updateMessage("Getting Hekate repository");
			updateProgress(0, 47);
    		hekate = GlobalSettings.gitHub.getRepository("CTCaer/hekate");
    		
    		updateMessage("Getting Atmosphere repository");
    		updateProgress(1, 47);
			atmosphere = GlobalSettings.gitHub.getRepository("Atmosphere-NX/Atmosphere");
			
			updateMessage("Getting Lockpick_RCM repository");
			updateProgress(2, 47);
			lockpickRCM = GlobalSettings.gitHub.getRepository("shchmue/Lockpick_RCM");
			
			updateMessage("Getting JKSV repository");
			updateProgress(3, 47);
			jksv = GlobalSettings.gitHub.getRepository("J-D-K/JKSV");
			
			updateMessage("Getting ftpd repository");
			updateProgress(4, 47);
			ftpd = GlobalSettings.gitHub.getRepository("mtheall/ftpd");
			
			updateMessage("Getting NXThemesInstaller repository");
			updateProgress(5, 47);
			nxThemesInstaller = GlobalSettings.gitHub.getRepository("exelix11/SwitchThemeInjector");
			
			updateMessage("Getting NX-Shell repository");
			updateProgress(6, 47);
			nxShell = GlobalSettings.gitHub.getRepository("joel16/NX-Shell");
			
			updateMessage("Getting Homebrew App Store repository");
			updateProgress(7, 47);
			hbAppStore = GlobalSettings.gitHub.getRepository("fortheusers/hb-appstore");
			
			updateProgress(8, 47);
		} catch (IOException e) {
			System.err.println("Failed to update repositories");
		}
    }
    
    private void updateReleases() {
    	try {
    		updateTitle("Getting releases");
    		
    		updateMessage("Getting latest Hekate release");
    		latest_hekate = hekate.getLatestRelease();
    		updateProgress(9, 47);
    		
    		updateMessage("Getting latest Atmosphere release");
			latest_atmosphere = atmosphere.getLatestRelease();
			updateProgress(10, 47);
			
			updateMessage("Getting latest Lockpick_RCM release");
	    	latest_lockpickRCM = lockpickRCM.getLatestRelease();
	    	updateProgress(11, 47);
	    	
	    	updateMessage("Getting latest JKSV release");
	    	latest_jksv = jksv.getLatestRelease();
	    	updateProgress(12, 47);
	    	
	    	updateMessage("Getting latest ftpd release");
	    	latest_ftpd = ftpd.getLatestRelease();
	    	updateProgress(13, 47);
	    	
	    	updateMessage("Getting latest NXThemesInstaller release");
	    	latest_nxThemesInstaller = nxThemesInstaller.getLatestRelease();
	    	updateProgress(14, 47);
	    	
	    	updateMessage("Getting latest NX-Shell release");
	    	latest_nxShell = nxShell.getLatestRelease();
	    	updateProgress(15, 47);
	    	
	    	updateMessage("Getting latest Homebrew App Store release");
	    	latest_hbAppStore = hbAppStore.getLatestRelease();
	    	updateProgress(16, 47);
		} catch (IOException e) {
			System.err.println("Failed to update releases");
		}
    }
    
    private void updateAssets() {
    	updateTitle("Updating assets");
    	
    	try {
			for (GHAsset asset : latest_hekate.listAssets()) {
				if (asset.getName().contains("hekate_ctcaer") && asset.getName().endsWith(".zip")) {
					updateMessage("Getting latest Hekate asset");
					hekate_zip = asset;
					updateProgress(17, 47);
					break;
				}
			}
			
			for (GHAsset asset : latest_atmosphere.listAssets()) {
				if (asset.getName().contains("atmosphere") && asset.getName().endsWith(".zip")) {
					updateMessage("Getting latest Atmosphere asset");
					atmosphere_zip = asset;
					updateProgress(18, 47);
					break;
				}
			}
			
			for (GHAsset asset : latest_lockpickRCM.listAssets()) {
				if (asset.getName().equals("Lockpick_RCM.bin")) {
					updateMessage("Getting latest Lockpick_RCM asset");
					lockpickRCM_bin = asset;
					updateProgress(19, 47);
					break;
				}
			}
			
			for (GHAsset asset : latest_jksv.listAssets()) {
				if (asset.getName().equals("JKSV.nro")) {
					updateMessage("Getting latest JKSV asset");
					jksv_nro = asset;
					updateProgress(20, 47);
				}
			}
			
			for (GHAsset asset : latest_ftpd.listAssets()) {
				if (asset.getName().equals("ftpd.nro")) {
					updateMessage("Getting latest ftpd asset");
					ftpd_nro = asset;
					updateProgress(21, 47);
				}
			}
			
			for (GHAsset asset : latest_nxThemesInstaller.listAssets()) {
				if (asset.getName().equals("NXThemesInstaller.nro")) {
					updateMessage("Getting latest NXThemesInstaller asset");
					nxThemesInstaller_nro = asset;
					updateProgress(22, 47);
				}
			}
			
			for (GHAsset asset : latest_nxShell.listAssets()) {
				if (asset.getName().equals("NX-Shell.nro")) {
					updateMessage("Getting latest NX-Shell asset");
					nxShell_nro = asset;
					updateProgress(23, 47);
				}
			}
			
			for (GHAsset asset : latest_hbAppStore.listAssets()) {
				if (asset.getName().equals("appstore.nro")) {
					updateMessage("Getting latest Homebrew App Store asset");
					hbAppStore_nro = asset;
					updateProgress(24, 47);
				}
			}
		} catch (IOException e) {
			System.err.println("Failed to get assets");
			return;
		}
    }
    
    private void downloadAssets() {
    	updateTitle("Downloading assets");
    	
    	if (!GlobalSettings.portableMode) {
            tempDir = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_DIR_PATH + File.separator + "SD");
        } else {
            tempDir = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_DIR_PATH + File.separator + "SD");
        }

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        updateMessage("Downloading Hekate");
        hekate_zip_file = downloadAsset(hekate_zip);
        updateProgress(25, 47);
        
        updateMessage("Downloading Atmosphere");
        atmosphere_zip_file = downloadAsset(atmosphere_zip);
        updateProgress(26, 47);
        
        updateMessage("Downloading Lockpick_RCM");
        lockpickRCM_bin_file = downloadAsset(lockpickRCM_bin);
        updateProgress(27, 47);
        
        updateMessage("Downloading JKSV");
        jksv_nro_file = downloadAsset(jksv_nro);
        updateProgress(28, 47);
        
        updateMessage("Downloading ftpd");
        ftpd_nro_file = downloadAsset(ftpd_nro);
        updateProgress(29, 47);
        
        updateMessage("Downloading NXThemesInstaller");
        nxThemesInstaller_nro_file = downloadAsset(nxThemesInstaller_nro);
        updateProgress(30, 47);
        
        updateMessage("Downloading NX-Shell");
        nxShell_nro_file = downloadAsset(nxShell_nro);
        updateProgress(31, 47);
        
        updateMessage("Downloading Homebrew App Store");
        hbAppStore_nro_file = downloadAsset(hbAppStore_nro);
        updateProgress(32, 47);
        
        updateMessage("Downloading Hekate config file");
        hekate_ipl_file = downloadDirectLinkFile(HEKATE_IPL, tempDir.getAbsolutePath() + File.separator + "hekate_ipl.ini");
        updateProgress(33, 47);

        updateMessage("Downloading 90dns DNS redirection config");
        emummc_txt_file = downloadDirectLinkFile(EMUMMC, tempDir.getAbsolutePath() + File.separator + "emummc.txt");
        updateProgress(34, 47);

        updateMessage("Downloading bootlogos");
        bootlogos_zip = downloadDirectLinkFile(BOOTLOGOS, tempDir.getAbsolutePath() + File.separator + "bootlogos.zip");
        updateProgress(35, 47);
    }
    
    private File downloadAsset(GHAsset asset) {
        File output = null;
        String destination = tempDir.getAbsolutePath() + File.separator + asset.getName();

        try {
            URL download = new URL(asset.getBrowserDownloadUrl());
            
            try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream((output = new File(destination)))) {
                byte[] buffer = new byte[1024];
                int bytes;

                while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytes);
                }
            }
        } catch (IOException ex) {
            System.out.println("Failed to download " + asset.getName());
            return null;
        }

        return output;
    }
    
    private File downloadDirectLinkFile(String downloadLink, String desination) {
        File output = new File(desination);

        try (BufferedInputStream bis = new BufferedInputStream(new URL(downloadLink).openStream()); FileOutputStream fos = new FileOutputStream(output)) {
            byte[] buffer = new byte[1024];
            int bytes;

            while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, bytes);
            }
        } catch (IOException ex) {
            System.err.println("Failed to download: " + downloadLink);
            return null;
        }

        return output;
    }
    
    private void extractArchives() {
    	updateTitle("Extracting archives");
    	
    	updateMessage("Extracting Hekate");
    	ZipHandler.unzip(hekate_zip_file.getAbsolutePath(), tempDir.getAbsolutePath());
    	updateProgress(35, 47);
    	
    	updateMessage("Extracting Atmosphere");
        ZipHandler.unzip(atmosphere_zip_file.getAbsolutePath(), tempDir.getAbsolutePath());
        updateProgress(36, 47);
        
        updateMessage("Extracting bootlogos");
        ZipHandler.unzip(bootlogos_zip.getAbsolutePath(), tempDir.getAbsolutePath());
        updateProgress(37, 47);
    }
    
    private void copyFiles(File sdRoot) {
    	updateTitle("Preparing to copy files");
    	updateMessage("Preparing to copy files");
    	
        hekate_ipl_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "bootloader" + File.separator + "hekate_ipl.ini"));
        updateProgress(38, 47);
        
        lockpickRCM_bin_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "bootloader" + File.separator + "payloads" + File.separator + "Lockpick_RCM.bin"));
        updateProgress(39, 47);

        File hostsDir = new File(tempDir.getAbsolutePath() + File.separator + "atmosphere" + File.separator + "hosts");
        hostsDir.mkdir();
        emummc_txt_file.renameTo(new File(hostsDir.getAbsolutePath() + File.separator + "emummc.txt"));
        updateProgress(40, 47);

        File appstoreDir = new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "appstore");
        appstoreDir.mkdir();
        hbAppStore_nro_file.renameTo(new File(appstoreDir.getAbsolutePath() + File.separator + "appstore.nro"));
        updateProgress(41, 47);

        jksv_nro_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "JKSV.nro"));
        updateProgress(41, 47);
        
        ftpd_nro_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "ftpd.nro"));
        updateProgress(42, 47);
        
        nxShell_nro_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "NX-Shell.nro"));
        updateProgress(43, 47);
        
        nxThemesInstaller_nro_file.renameTo(new File(tempDir.getAbsolutePath() + File.separator + "switch" + File.separator + "NXThemesInstaller.nro"));
        updateProgress(44, 47);
        
        updateMessage("Copying directories");
        
        try {
            for (File file: tempDir.listFiles()) {
            	if (file.isDirectory()) {
            		FileUtils.copyDirectory(file, new File(sdRoot.getAbsolutePath() + File.separator + file.getName()));
            	}
            }
        } catch (IOException ex) {
            System.err.println("Failed to copy directories!");
            return;
        }
        
        updateProgress(45, 47);

        updateMessage("Copying hbmenu");
        File hbMenu = new File(tempDir.getAbsolutePath() + File.separator + "hbmenu.nro");
        hbMenu.renameTo(new File(sdRoot.getAbsolutePath() + File.separator + "hbmenu.nro"));
        updateProgress(46, 47);
    }
    
    private void cleanUp() {
        try {
        	updateTitle("Cleaning up");
        	updateMessage("Cleaning up");
            FileUtils.deleteDirectory(tempDir);
            updateProgress(47, 47);
        } catch (IOException ex) {
            System.err.println("Failed to clean up at the end!");
            return;
        }
    }

    @Override
	protected Object call() throws Exception {
		prepareSDCard(this.sdRoot);
		return true;
	}
}
