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
package handlers;

import git.GitHandler;
import git.Release;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import ui.UIGlobal;
import ui.fx.JTegraNX;

public class UpdateHandler {

    private static Release jtegranx;
    private static final String currentVersion = "1.6";

    public static void checkForUpdates() {
        Platform.runLater(() -> {
            jtegranx = GitHandler.generateLatestRelease("https://github.com/dylwedma11748/JTegraNX/releases");

            if (!jtegranx.getTag().equals(currentVersion)) {
                boolean update = AlertHandler.showConfirmationDialog("JTegraNX updater", "A new release of JTegraNX has been found. Download now?\n\nCurrent release: JTegraNX v" + currentVersion + "\nLatest release: " + jtegranx.getReleaseName(), "");

                if (update) {
                    FileChooser chooser = new FileChooser();

                    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Executable Jar File (*.jar)", "*jar");
                    chooser.getExtensionFilters().add(filter);

                    File file = chooser.showSaveDialog(JTegraNX.getStage());

                    if (file != null) {
                        jtegranx.getAssets().stream().filter((asset) -> (asset.getAssetName().equals("JTegraNX.jar"))).forEachOrdered((asset) -> {
                            String destination = file.getAbsolutePath();

                            if (!destination.endsWith(".jar")) {
                                destination = destination.concat(".jar");
                            }

                            GitHandler.downloadAsset(asset, destination);
                        });

                        if (file.exists()) {
                            UIGlobal.appendLog("Download complete");
                        }
                    }
                }
            } else {
                UIGlobal.appendLog("JTegraNX is up to date");
            }
        });
    }
}
