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
package ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.kohsuke.github.GitHubBuilder;

import handlers.AlertHandler;
import handlers.PayloadHandler;
import handlers.ProgressAlert;
import handlers.UpdateHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import rcm.RCM;
import tasks.Preloader;
import util.GlobalSettings;
import util.NativeLoader;
import util.Tray;
import windows.DriverInstaller;

public class JTegraNX extends Application {

    private static MainUIController controller;
    private static Stage stage;
    private static Image icon;

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainUI.fxml"));
        Pane base = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(base);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("JTegraNX");
        icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
        stage.getIcons().add(icon);
        stage.setResizable(false);
        
		/*
		 * Screen screen = Screen.getPrimary(); Rectangle2D bounds =
		 * screen.getVisualBounds(); stage.setX(bounds.getMinX());
		 * stage.setY(bounds.getMinY()); stage.setWidth(bounds.getWidth());
		 * stage.setHeight(bounds.getHeight());
		 */
        
        
        JTegraNX.stage = stage;
        
        controller.getPayloadPathField().textProperty().addListener((observable, oldValue, newValue) -> {
            UIGlobal.checkIfSpecifiedPayloadExists();

            if (GlobalSettings.enableTrayIcon) {
                Tray.updateMenuItems();
            }
        });

        stage.show();
        
        boolean configFileFoundAndRead = false;

        if (UIGlobal.readMainConfigFile()) {
            configFileFoundAndRead = true;
            UIGlobal.applyGlobalSettings();
            prepare();
        }

        controller.configName.getParent().requestFocus();

        if (!System.getProperty("os.name").contains("Windows")) {
            controller.getInstallAPXDriverMenuItem().setDisable(true);
            controller.getInstallAPXDriverMenuItem().setVisible(false);
        }

        if (System.getProperty("os.name").contains("Linux")) {
            if (!System.getProperty("user.name").equals("root")) {
                UIGlobal.appendLog("Linux support requires either root access or udev rules configured");
                AlertHandler.showInformationMessage("JTegraNX on Linux", "Linux support requires either root access or udev rules configured", "You are currently running as " + System.getProperty("user.name"));
            }
        }

        if (System.getProperty("os.name").contains("Mac OS X")) {
            UIGlobal.appendLog("Mac OS X support is not perfect.\nRCM functionality should work, but there may be issues with other features.");
            AlertHandler.showInformationMessage("JTegraNX on Mac OS X", "Mac OS X support is not perfect", "RCM functionality should work, but there may be some issues with other features.");
        }

        if (configFileFoundAndRead) {
            if (GlobalSettings.portableMode) {
                UIGlobal.appendLog("Using portable mode");
                controller.getPortableModeMenuItem().setSelected(true);
                controller.getStandardModeMenuItem().setSelected(false);
            } else {
                UIGlobal.appendLog("Using standard mode");
                controller.getPortableModeMenuItem().setSelected(false);
                controller.getStandardModeMenuItem().setSelected(true);
            }
        } else {
            List<String> choices = new ArrayList<>();
            choices.add("Standard mode");
            choices.add("Portable mode");
            String mode = AlertHandler.createChoiceDialog("JTegraNX", "Choose configuration mode", "Mode: ", choices);

            if (mode != null && mode.equals("Portable mode")) {
                GlobalSettings.portableMode = true;
                UIGlobal.appendLog("Using portable mode");
                controller.getPortableModeMenuItem().setSelected(true);
                controller.getStandardModeMenuItem().setSelected(false);
            } else {
                GlobalSettings.portableMode = false;
                UIGlobal.appendLog("Using standard mode");
                controller.getPortableModeMenuItem().setSelected(false);
                controller.getStandardModeMenuItem().setSelected(true);
            }
            
            prepare();
        }
    }
    
    private static void prepare() {
    	ProgressAlert alert = AlertHandler.createProgressAlert("JTegraNX", "Preparing");
    	alert.show();
    	
    	Preloader loader = new Preloader();
    	
    	alert.getBar().progressProperty().unbind();
    	alert.getBar().progressProperty().bind(loader.progressProperty());
    	
        alert.getAlert().headerTextProperty().unbind();
        alert.getAlert().headerTextProperty().bind(loader.messageProperty());
    	
    	loader.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				alert.close();
				
				if (GlobalSettings.checkJTegraNXUpdates) {
		    		UpdateHandler.checkForUpdates(null);
		    	}
			}
    	});
    	
    	loader.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				alert.close();
			}
    	});
    	
    	new Thread(loader).start();
    }
    
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("-cml")) {
        	GlobalSettings.commandLineMode = true;
        	Scanner scanner = new Scanner(System.in);
            
        	if (!NativeLoader.loadNatives()) {
        		System.err.println("Failed to load natives");
        		System.exit(-1);
        	}
        	
            System.out.println("JTegraNX - Another RCM payload injector\nCopyright (C) 2019-2022 Dylan Wedman");

            if (UIGlobal.readMainConfigFile()) {
            	try {
            		System.out.println("Connecting to GitHub");
        			GlobalSettings.gitHub = new GitHubBuilder().withOAuthToken(GlobalSettings.GITHUB_ACCESS_TOKEN).build();
        			
        			if (!GlobalSettings.gitHub.isCredentialValid()) {
        				System.err.println("Failed to connect to GitHub: invalid credential");
        				GlobalSettings.OFFLINE_MODE = true;
        			} else {
        				System.out.println("Connected to GitHub");
        			}
        		} catch (IOException e) {
        			System.err.println("Failed to connect to GitHub: IOException happened during GitHub builder");
        			e.printStackTrace();
        			GlobalSettings.OFFLINE_MODE = true;
        		}
            	
                if (GlobalSettings.checkPayloadUpdates) {
                    System.out.println("Checking for payload updates");
                    PayloadHandler.updatePayloads();
                }

                if (GlobalSettings.checkJTegraNXUpdates) {
                    System.out.println("Checking for JTegraNX updates");
                    UpdateHandler.checkForUpdates(scanner);
                }
            } else {
            	System.out.println("Select a configuration mode\nS - Standard Mode\nP - Portable mode");
            	System.out.append("> ");
            	String command = scanner.nextLine();
            	
            	if (command.equalsIgnoreCase("S")) {
            		System.out.println("Standard mode selected");
            		GlobalSettings.portableMode = false;
            	} else if (command.equalsIgnoreCase("P")) {
            		System.out.println("Portable mode selected");
            		GlobalSettings.portableMode = true;
            	}
            }

            System.out.println("Command line mode ready\nFor help, type \"help\"");
            commandLineModeLoop(scanner);
        } else {
        	try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                System.err.println("Failed to set UI look and feel: " + ex.getClass().getName() + " happened");
                ex.printStackTrace();
                System.exit(-1);
            }
        	
        	if (!NativeLoader.loadNatives()) {
        		System.err.println("Failed to load natives");
        		System.exit(-1);
        	}
        	
            launch();
        }
    }

    private static void commandLineModeLoop(Scanner scanner) {
        System.out.append("> ");
        String command = scanner.nextLine();

        if (command.equals("help")) {
            System.out.println("inject      	Inject a payload, if used with -bp flag, will use specified bundled payload");
            System.out.println("update      	Runs the specifed updater, available updaters are -payloads and -jtegranx");
            System.out.println("apx         	Runs the APX driver installer (Windows only)");
            System.out.println("set         	Changes specified setting, available settings are -bp and -autoupdate");
            System.out.println("gptrestore  	Injects gptrestore");
            System.out.println("exit        	Closes JTegraNX");
            commandLineModeLoop(scanner);
        } else if (command.contains("inject")) {
            if (command.contains("-bp")) {
                if (!GlobalSettings.payloadsUpdatedThisSession) {
                    System.out.println("Bundled payloads haven't been updated this session.\nType \"update -payloads\" to update them.");
                }
                
                String bp = "";
                
                try {
                    bp = command.substring(command.indexOf("-bp") + 4);
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Invalid use of inject -bp; must include target payload");
                }
                
                if (bp.equalsIgnoreCase("fusee")) {
                    if (GlobalSettings.includeFusee) {
                        if (GlobalSettings.portableMode) {
                            UIGlobal.injectPayload(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin", false);
                        } else {
                            UIGlobal.injectPayload(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "fusee-primary.bin", false);
                        }
                    } else {
                        System.out.println("fusee-primary bundled payload is disabled");
                    }
                } else if (bp.equalsIgnoreCase("Hekate")) {
                    if (GlobalSettings.includeHekate) {
                    	if (GlobalSettings.portableMode) {
                    		for (File file : GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            	if (file.getName().contains("hekate_ctcaer")) {
                            		UIGlobal.injectPayload(file.getAbsolutePath(), false);
                            	}
                            }
                        } else {
                        	for (File file : GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR.listFiles()) {
                            	if (file.getName().contains("hekate_ctcaer")) {
                            		UIGlobal.injectPayload(file.getAbsolutePath(), false);
                            	}
                            }
                        }
                    } else {
                        System.out.println("Hekate bundled payload is disabled");
                    }
                } else if (bp.equalsIgnoreCase("Lockpick_RCM")) {
                    if (GlobalSettings.includeLockpickRCM) {
                        if (GlobalSettings.portableMode) {
                            UIGlobal.injectPayload(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin", false);
                        } else {
                            UIGlobal.injectPayload(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "Lockpick_RCM.bin", false);
                        }
                    } else {
                        System.out.println("Lockpick_RCM bundled payload is disabled");
                    }
                } else if (bp.equalsIgnoreCase("TegraExplorer")) {
                    if (GlobalSettings.includeTegraExplorer) {
                        if (GlobalSettings.portableMode) {
                            UIGlobal.injectPayload(GlobalSettings.PORTABLE_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin", false);
                        } else {
                            UIGlobal.injectPayload(GlobalSettings.STANDARD_MODE_JTEGRANX_PAYLOAD_DIR_PATH + File.separator + "TegraExplorer.bin", false);
                        }
                    } else {
                        System.out.println("TegraExplorer bundled payload is disabled");
                    }
                } else {
                	System.out.println("Invalid -bp specification");
                }

                commandLineModeLoop(scanner);
            } else {
                String payloadPath = command.substring(command.indexOf(" ") + 1);
                UIGlobal.injectPayload(payloadPath, false);
                commandLineModeLoop(scanner);
            }
        } else if (command.contains("update") && !command.contains("-autoupdate")) {
            if (command.contains("-payloads")) {
                PayloadHandler.updatePayloads();
                commandLineModeLoop(scanner);
            } else if (command.contains("-jtegranx")) {
                UpdateHandler.checkForUpdates(scanner);
                commandLineModeLoop(scanner);
            } else {
                System.out.println("Invalid use of command\n\"update\" requires a flag");
                commandLineModeLoop(scanner);
            }
        } else if (command.equals("apx")) {
            if (System.getProperty("os.name").contains("Windows")) {
                int result = DriverInstaller.installDriver();

                switch (result) {
                    case DriverInstaller.CANCELED:
                        UIGlobal.appendLog("APX Driver install canceled by user");
                        break;
                    case DriverInstaller.DEVICE_UPDATED:
                        UIGlobal.appendLog("APX Driver installed\nRCM device needs to be reconnected");
                        break;
                    case DriverInstaller.READY_FOR_USE:
                        UIGlobal.appendLog("APX Driver installed\nRCM device ready for use");
                        break;
                    case DriverInstaller.UAC_CANCEL:
                        UIGlobal.appendLog("APX Driver install canceled from UAC");
                        break;
                }
            } else {
                System.out.println("APX driver can only be installed on Windows");
            }

            commandLineModeLoop(scanner);
        } else if (command.contains("set")) {
            String setting = "";

            try {
                setting = command.substring(command.indexOf("set") + 4);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Invalid use of set; must include a flag");
                commandLineModeLoop(scanner);
            }

            if (setting.contains("-bp")) {
                String payloadSetting = "";

                try {
                    payloadSetting = command.substring(command.indexOf("-bp") + 4);
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Invalid use of set -bp; must include target payload");
                    commandLineModeLoop(scanner);
                }

                String targetPayload = "";
                boolean setTrue = payloadSetting.contains("true");
                boolean setFalse = payloadSetting.contains("false");

                if (setTrue && setFalse) {
                    System.out.println("Invalid use of set -bp; can't specify true and false at the same time\nThat's just common sense");
                } else if (setTrue) {
                    try {
                        targetPayload = payloadSetting.substring(0, payloadSetting.indexOf("true") - 1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid use of set; must contain -bp or -autoupdate");
                        commandLineModeLoop(scanner);
                    }
                } else if (setFalse) {
                    try {
                        targetPayload = payloadSetting.substring(0, payloadSetting.indexOf("false") - 1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid use of set -bp; must include target payload");
                        commandLineModeLoop(scanner);
                    }
                } else {
                    System.out.println("Invalid use of set -bp; must include true or false");
                    commandLineModeLoop(scanner);
                }

                if (targetPayload.equalsIgnoreCase("fusee-primary")) {
                    GlobalSettings.includeFusee = setTrue;
                    PayloadHandler.updatePayloads();
                } else if (targetPayload.equalsIgnoreCase("hekate")) {
                    GlobalSettings.includeHekate = setTrue;
                    PayloadHandler.updatePayloads();
                } else if (targetPayload.equalsIgnoreCase("lockpick_rcm")) {
                    GlobalSettings.includeLockpickRCM = setTrue;
                    PayloadHandler.updatePayloads();
                } else if (targetPayload.equalsIgnoreCase("tegraexplorer")) {
                    GlobalSettings.includeTegraExplorer = setTrue;
                    PayloadHandler.updatePayloads();
                }
            } else if (setting.contains("-autoupdate")) {
                String target = "";

                try {
                    target = setting.substring(setting.indexOf("-autoupdate") + 12);
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("Invalid use of set -autoupdate; must include target updater");
                    commandLineModeLoop(scanner);
                }

                String updater = "";
                boolean setTrue = target.contains("true");
                boolean setFalse = target.contains("false");

                if (setTrue && setFalse) {
                    System.out.println("Invalid use of set -autoupdate; can't specify true and false at the same time\nThat's just common sense");
                    commandLineModeLoop(scanner);
                } else if (setTrue) {
                    try {
                        updater = target.substring(0, target.indexOf("true") - 1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid use of set -bp; must include target payload");
                        commandLineModeLoop(scanner);
                    }
                } else if (setFalse) {
                    try {
                        updater = target.substring(0, target.indexOf("false") - 1);
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("Invalid use of set -bp; must include target payload");
                        commandLineModeLoop(scanner);
                    }
                } else {
                    System.out.println("Invalid use of set -autoupdate; must include true or false");
                    commandLineModeLoop(scanner);
                }

                if (updater.equalsIgnoreCase("payloads")) {
                    GlobalSettings.checkPayloadUpdates = setTrue;
                    System.out.println("Payload update checks set to " + setTrue);
                } else if (updater.equalsIgnoreCase("jtegranx")) {
                    GlobalSettings.checkJTegraNXUpdates = setTrue;
                    System.out.println("JTegraNX update checks set to " + setTrue);
                } else {
                    System.out.println("Invalid use of set -autoupdate; invalid updater specifed; available updaters are payloads and jtegranx; these cannot be specified at the same time");
                }
            } else {
                System.out.println("Invalid use of set; must contain -bp or -autoupdate");
                commandLineModeLoop(scanner);
            }

            commandLineModeLoop(scanner);
        } else if (command.equals("gptrestore")) {
        	System.out.println("Loading gptrestore");
        	PayloadHandler.prepareGPTRestore();
        	RCM.injectPayload(GlobalSettings.gptRestorePath, false);
        	commandLineModeLoop(scanner);
        } else if (command.equals("exit")) {
            UIGlobal.saveMainConfigFile();
            System.exit(0);
        } else {
            System.out.println("Invalid command \"" + command + "\"");
            commandLineModeLoop(scanner);
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static MainUIController getController() {
        return controller;
    }
}
