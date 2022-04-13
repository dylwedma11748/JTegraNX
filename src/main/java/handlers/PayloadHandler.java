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

package handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;

import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import ui.JTegraNX;
import ui.UIGlobal;
import util.GlobalSettings;

public class PayloadHandler {
	private static GHRepository fusee;
	private static GHRepository hekate;
	private static GHRepository lockpickRCM;
	private static GHRepository tegraExplorer;
	
	private static GHRelease latest_fusee;
	private static GHRelease latest_hekate;
	private static GHRelease latest_lockpickRCM;
	private static GHRelease latest_tegraExplorer;
	
	private static GHAsset fusee_payload;
	private static GHAsset hekate_zip;
	private static GHAsset lockpickRCM_payload;
	private static GHAsset tegraExplorer_payload;
	
	private static File hekate_zip_file;
	private static File hekate_payload_file;
	
	private static boolean fuseeUpdate;
	private static boolean hekateUpdate;
	private static boolean lockpickRCMUpdate;
	private static boolean tegraExplorerUpdate;
	
	public static void updatePayloads() {
		if (!GlobalSettings.OFFLINE_MODE) {
			checkSelectedPayloads();
			updateRepositories();
			updateReleases();
			determineUpdates();
			updateAssets();
			downloadAssets();
		}
		
		if (!GlobalSettings.commandLineMode) {
			addPayloadsToMenu(true);
		}
	}

	private static void updateRepositories() {
		try {
			fusee = GlobalSettings.gitHub.getRepository("Atmosphere-NX/Atmosphere");
			hekate = GlobalSettings.gitHub.getRepository("CTCaer/hekate");
			lockpickRCM = GlobalSettings.gitHub.getRepository("shchmue/Lockpick_RCM");
			tegraExplorer = GlobalSettings.gitHub.getRepository("suchmememanyskill/TegraExplorer");
		} catch (IOException e) {
			UIGlobal.appendLog("Failed to get payload repositories");
			return;
		}
	}
	
	private static void updateReleases() {
		try {
			latest_fusee = fusee.getLatestRelease();
			latest_hekate = hekate.getLatestRelease();
			latest_lockpickRCM = lockpickRCM.getLatestRelease();
			latest_tegraExplorer = tegraExplorer.getLatestRelease();
		} catch (IOException e) {
			UIGlobal.appendLog("Failed to get latest payload releases");
			return;
		}
	}
	
	private static void determineUpdates() {
		if (GlobalSettings.includeFusee && GlobalSettings.fuseeTag.equals(null)) {
			GlobalSettings.fuseeTag = latest_fusee.getTagName();
			fuseeUpdate = true;
		}
		
		if (GlobalSettings.includeHekate && GlobalSettings.hekateTag.equals(null)) {
			GlobalSettings.hekateTag = latest_hekate.getTagName();
			hekateUpdate = true;
		}
		
		if (GlobalSettings.includeLockpickRCM && GlobalSettings.lockpickRCMTag.equals(null)) {
			GlobalSettings.lockpickRCMTag = latest_lockpickRCM.getTagName();
			lockpickRCMUpdate = true;
		}
		
		if (GlobalSettings.includeTegraExplorer && GlobalSettings.tegraExplorerTag.equals(null)) {
			GlobalSettings.tegraExplorerTag = latest_tegraExplorer.getTagName();
			tegraExplorerUpdate = true;
		}
		
		if (GlobalSettings.includeFusee && !GlobalSettings.fuseeTag.equals(latest_fusee.getTagName())) {
			GlobalSettings.fuseeTag = latest_fusee.getTagName();
			fuseeUpdate = true;
		}
		
		if (GlobalSettings.includeHekate && !GlobalSettings.hekateTag.equals(latest_hekate.getTagName())) {
			GlobalSettings.hekateTag = latest_hekate.getTagName();
			hekateUpdate = true;
		}
		
		if (GlobalSettings.includeLockpickRCM && !GlobalSettings.lockpickRCMTag.equals(latest_lockpickRCM.getTagName())) {
			GlobalSettings.lockpickRCMTag = latest_lockpickRCM.getTagName();
			lockpickRCMUpdate = true;
		}
		
		if (GlobalSettings.includeTegraExplorer && !GlobalSettings.tegraExplorerTag.equals(latest_tegraExplorer.getTagName())) {
			GlobalSettings.tegraExplorerTag = latest_tegraExplorer.getTagName();
			tegraExplorerUpdate = true;
		}
		
		System.out.println("Payload updates checked");
		
		if (GlobalSettings.includeFusee) {
			if (fuseeUpdate) {
				System.out.println("fusee: Update found");
			} else {
				System.out.println("fusee: Up to date");
			}
		}
		
		if (GlobalSettings.includeHekate) {
			if (hekateUpdate) {
				System.out.println("Hekate: Update found");
			} else {
				System.out.println("Hekate: Up to date");
			}
		}
		
		if (GlobalSettings.includeLockpickRCM) {
			if (lockpickRCMUpdate) {
				System.out.println("Lockpick_RCM: Update found");
			} else {
				System.out.println("Lockpick_RCM: Up to date");
			}
		}
		
		if (GlobalSettings.includeTegraExplorer) {
			if (tegraExplorerUpdate) {
				System.out.println("TegraExplorer: Update found");
			} else {
				System.out.println("TegraExplorer: Up to date");
			}
		}
	}
	
	private static void updateAssets() {
		try {
			for (GHAsset asset : latest_fusee.listAssets()) {
				if (asset.getName().equals("fusee.bin")) {
					fusee_payload = asset;
					break;
				}
			}
			
			for (GHAsset asset : latest_hekate.listAssets()) {
				if (asset.getName().contains("hekate_ctcaer") && asset.getName().endsWith(".zip")) {
					hekate_zip = asset;
					break;
				}
			}
			
			for (GHAsset asset : latest_lockpickRCM.listAssets()) {
				if (asset.getName().equals("Lockpick_RCM.bin")) {
					lockpickRCM_payload = asset;
					break;
				}
			}
			
			for (GHAsset asset : latest_tegraExplorer.listAssets()) {
				if (asset.getName().equals("TegraExplorer.bin")) {
					tegraExplorer_payload = asset;
					break;
				}
			}
		} catch (IOException e) {
			UIGlobal.appendLog("Failed to get payload assets");
			return;
		}
	}
	
	private static void downloadAssets() {
		if (GlobalSettings.selectedPayloadCount > 0) {	
			if (GlobalSettings.portableMode) {
				if (!GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
					GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.mkdirs();
				}
			} else {
				if (!GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.exists()) {
					GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.mkdirs();
				}
			}
			
			if (GlobalSettings.includeFusee) {
				File file = null;
				
				if (GlobalSettings.portableMode) {
					file = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee.bin");
				} else {
					file = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee.bin");
				}
				
				if (!file.exists()) {
					downloadAsset(fusee_payload);
				} else if (file.exists() && fuseeUpdate) {
					downloadAsset(fusee_payload);
				}
			}
			
			if (GlobalSettings.includeHekate) {
				File file = null;
				
				if (GlobalSettings.portableMode) {
					for (File payloadFile : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
						if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
							file = payloadFile;
							break;
						}
					}
				} else {
					for (File payloadFile : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
						if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
							file = payloadFile;
							break;
						}
					}
				}
				
				if (file == null) {
					hekate_zip_file = downloadAsset(hekate_zip);
				} else if (file.exists() && hekateUpdate) {
					try {
						FileUtils.forceDelete(file);
					} catch (IOException e) {
						System.err.println("Failed to delete existing Hekate bin");
					}
					
					hekate_zip_file = downloadAsset(hekate_zip);
				}
				
				if (hekate_zip_file != null) {
					if (GlobalSettings.portableMode) {
						ZipHandler.unzip(hekate_zip_file.getAbsolutePath(), GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
						
						for (File payloadFile : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
							if (payloadFile.getName().equals("bootloader") && payloadFile.isDirectory()) {
								try {
									FileUtils.deleteDirectory(payloadFile);
								} catch (IOException e) {
									System.err.println("Failed to delete bootloader from hekate");
									break;
								}
							}
							
							if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
								hekate_payload_file = payloadFile;
							}
						}
					} else {
						ZipHandler.unzip(hekate_zip_file.getAbsolutePath(), GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH);
						
						for (File payloadFile : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
							if (payloadFile.getName().equals("bootloader") && payloadFile.isDirectory()) {
								try {
									FileUtils.deleteDirectory(payloadFile);
								} catch (IOException e) {
									System.err.println("Failed to delete bootloader from hekate");
									break;
								}
							}
							
							if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
								hekate_payload_file = payloadFile;
							}
						}
					}
					
					try {
						FileUtils.forceDelete(hekate_zip_file);
					} catch (IOException e) {
						System.err.println("Failed to delete Hekate zip");
					}
				} else {
					if (GlobalSettings.portableMode) {
						for (File payloadFile : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
							if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
								hekate_payload_file = payloadFile;
							}
						}
					} else {
						for (File payloadFile : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
							if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
								hekate_payload_file = payloadFile;
							}
						}
					}
				}
			}
			
			if (GlobalSettings.includeLockpickRCM) {
				File file = null;
				
				if (GlobalSettings.portableMode) {
					file = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
				} else {
					file = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin");
				}
				
				if (!file.exists()) {
					downloadAsset(lockpickRCM_payload);
				} else if (file.exists() && lockpickRCMUpdate) {
					downloadAsset(lockpickRCM_payload);
				}
			}
			
			if (GlobalSettings.includeTegraExplorer) {
				File file = null;
				
				if (GlobalSettings.portableMode) {
					file = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
				} else {
					file = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin");
				}
				
				if (!file.exists()) {
					downloadAsset(tegraExplorer_payload);
				} else if (file.exists() && tegraExplorerUpdate) {
					downloadAsset(tegraExplorer_payload);
				}
			}
		}
	}
	
	private static File downloadAsset(GHAsset asset) {
        File output = null;
        String destination = null;

        try {
            URL download = new URL(asset.getBrowserDownloadUrl());

            if (GlobalSettings.portableMode) {
            	destination = GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + asset.getName();
            } else {
            	destination = GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + asset.getName();
            }
            
            try (BufferedInputStream bis = new BufferedInputStream(download.openStream()); FileOutputStream fos = new FileOutputStream((output = new File(destination)))) {
                byte[] buffer = new byte[1024];
                int bytes;

                while ((bytes = bis.read(buffer, 0, 1024)) != -1) {
                    fos.write(buffer, 0, bytes);
                }
            }
        } catch (IOException ex) {
            UIGlobal.appendLog("Failed to download " + asset.getName());
            return null;
        }

        return output;
    }
	
	public static void addPayloadsToMenu(boolean updated) {
		JTegraNX.getController().getPayloadMenu().getItems().clear();
		
		if (updated) {
			if (GlobalSettings.includeFusee) {
				addPayloadToMenu("fusee v", GlobalSettings.fuseeTag, "fusee.bin");
			}
			
			if (GlobalSettings.includeHekate) {
				addPayloadToMenu("Hekate ", GlobalSettings.hekateTag, hekate_payload_file.getName());
			}
			
			if (GlobalSettings.includeLockpickRCM) {
				addPayloadToMenu("Lockpick_RCM ", GlobalSettings.lockpickRCMTag, "Lockpick_RCM.bin");
			}
			
			if (GlobalSettings.includeTegraExplorer) {
				addPayloadToMenu("TegraExplorer v", GlobalSettings.tegraExplorerTag, "TegraExplorer.bin");
			}
		} else {
			if (GlobalSettings.includeFusee) {
				addPayloadToMenu("fusee v", GlobalSettings.fuseeTag, "fusee.bin");
			}
			
			if (GlobalSettings.includeHekate) {
				if (GlobalSettings.portableMode) {
					for (File payloadFile : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
						if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
							hekate_payload_file = payloadFile;
							break;
						}
					}
				} else {
					for (File payloadFile : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
						if (payloadFile.isFile() && payloadFile.getName().startsWith("hekate_ctcaer") && payloadFile.getName().endsWith(".bin")) {
							hekate_payload_file = payloadFile;
							break;
						}
					}
				}
				
				addPayloadToMenu("Hekate ", GlobalSettings.hekateTag, hekate_payload_file.getName());
			}
			
			if (GlobalSettings.includeLockpickRCM) {
				addPayloadToMenu("Lockpick_RCM ", GlobalSettings.lockpickRCMTag, "Lockpick_RCM.bin");
			}
			
			if (GlobalSettings.includeTegraExplorer) {
				addPayloadToMenu("TegraExplorer v", GlobalSettings.tegraExplorerTag, "TegraExplorer.bin");
			}
		}
	}
	
	private static void addPayloadToMenu(String name, String version, String fileName) {
		MenuItem item = new MenuItem(name + version);
        ImageView image = new ImageView(PayloadHandler.class.getResource("/images/payload.png").toString());
        item.setGraphic(image);
        
        item.setOnAction((ActionEvent event) -> {
        	if (GlobalSettings.portableMode) {
        		JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + fileName);
        	} else {
        		JTegraNX.getController().getPayloadPathField().setText(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + fileName);
        	}
        });
        
        JTegraNX.getController().getPayloadMenu().getItems().add(item);
	}
	
	public static void checkSelectedPayloads() {
        GlobalSettings.selectedPayloadCount = 0;

        if (GlobalSettings.includeFusee) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (GlobalSettings.includeHekate) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (GlobalSettings.includeLockpickRCM) {
            GlobalSettings.selectedPayloadCount++;
        }

        if (GlobalSettings.includeTegraExplorer) {
            GlobalSettings.selectedPayloadCount++;
        }
    }
	
	public static StringBuilder getPayloadInfoAsString() {
        checkSelectedPayloads();
        StringBuilder payloadInfo = new StringBuilder();

        if (GlobalSettings.selectedPayloadCount > 0) {
            payloadInfo.append("[PAYLOAD RELEASE INFO]\n");

            if (GlobalSettings.includeFusee) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("fusee=").append(latest_fusee.getTagName()).append("\n");
                } else {
                    payloadInfo.append("fusee=").append(GlobalSettings.fuseeTag).append("\n");
                }
            }

            if (GlobalSettings.includeHekate) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("hekate=").append(latest_hekate.getTagName()).append("\n");
                } else {
                    payloadInfo.append("hekate=").append(GlobalSettings.hekateTag).append("\n");
                }
            }

            if (GlobalSettings.includeLockpickRCM) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("lockpickRCM=").append(latest_lockpickRCM.getTagName()).append("\n");
                } else {
                    payloadInfo.append("lockpickRCM=").append(GlobalSettings.lockpickRCMTag).append("\n");
                }
            }

            if (GlobalSettings.includeTegraExplorer) {
                if (GlobalSettings.payloadsUpdatedThisSession) {
                    payloadInfo.append("tegraExplorer=").append(latest_tegraExplorer.getTagName()).append("\n");
                } else {
                    payloadInfo.append("tegraExplorer=").append(GlobalSettings.tegraExplorerTag).append("\n");
                }
            }
        }

        return payloadInfo;
    }

	public static void prepareGPTRestore() {
    	File payload;
        
        if (GlobalSettings.portableMode) {
			payload = new File(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "gptrestore.bin");
			
			if (payload.exists()) {
				try {
					FileUtils.forceDelete(payload);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
        	payload = ResourceHandler.rename(ResourceHandler.load("/tools/gptrestore.bin"), GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "gptrestore.bin");
		} else {
			payload = new File(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "gptrestore.bin");
			
			if (payload.exists()) {
				try {
					FileUtils.forceDelete(payload);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			payload = ResourceHandler.rename(ResourceHandler.load("/tools/gptrestore.bin"), GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "gptrestore.bin");
		}
        
        GlobalSettings.gptRestorePath = payload.getAbsolutePath();
    }
}
