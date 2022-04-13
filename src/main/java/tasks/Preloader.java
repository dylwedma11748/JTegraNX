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

package tasks;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import configs.ConfigManager;
import handlers.PayloadHandler;
import javafx.concurrent.Task;
import ui.UIGlobal;
import util.GlobalSettings;
import util.Tray;

public class Preloader extends Task<Object> {

	@Override
	protected Object call() throws InterruptedException {
		updateMessage("Starting device listener");
		Thread.sleep(500);
    	UIGlobal.startDeviceListener();
    	
    	
    	if (GlobalSettings.portableMode) {
    		updateMessage("Loading config (Portable Mode)");
    	} else {
    		updateMessage("Loading config (Standard Mode)");
    	}
    	
    	Thread.sleep(500);
    	ConfigManager.updateConfigList();
		
		try {
			updateMessage("Connecting to GitHub");
			GlobalSettings.gitHub = GitHub.connectAnonymously();
			
			if (!GlobalSettings.gitHub.isCredentialValid()) {
				System.err.println("Failed to connect to GitHub: invalid credential");
				GlobalSettings.OFFLINE_MODE = true;
			}
		} catch (IOException e) {
			System.err.println("Failed to connect to GitHub: IOException happened during GitHub builder");
			e.printStackTrace();
			GlobalSettings.OFFLINE_MODE = true;
		}
		
		if (GlobalSettings.checkPayloadUpdates) {
			updateMessage("Checking for payload updates");
			PayloadHandler.updatePayloads();
    	} else {
    		updateMessage("Adding selected payloads");
    		Thread.sleep(500);
    		PayloadHandler.addPayloadsToMenu(false);
    	}
		
		if (GlobalSettings.enableTrayIcon) {
            Tray.enableTrayIcon();
        }

    	updateMessage("Finishing");
    	Thread.sleep(500);
		return true;
	}
	
}
