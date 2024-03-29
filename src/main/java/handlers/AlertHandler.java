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

import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import ui.JTegraNX;

public class AlertHandler {

    public static boolean showConfirmationDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.initOwner(JTegraNX.getStage());

        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }

    public static int showSwingConformationDialog(String title, String message, int messageType) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, messageType);
    }

    public static void showInformationMessage(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        if (JTegraNX.getStage() != null) {
            alert.initOwner(JTegraNX.getStage());
        }

        alert.showAndWait();
    }

    public static void showErrorMessage(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        if (JTegraNX.getStage() != null) {
            alert.initOwner(JTegraNX.getStage());
        }

        alert.showAndWait();
    }

    public static Alert createAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.initOwner(JTegraNX.getStage());
        return alert;
    }

    public static String createChoiceDialog(String title, String headerText, String contentText, List<String> choices) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        dialog.initOwner(JTegraNX.getStage());

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }
    
    public static ProgressAlert createProgressAlert(String title, String headerText) {
    	ProgressAlert progress;
    	
    	Alert alert = new Alert(AlertType.NONE);
    	alert.setTitle(title);
    	alert.setHeaderText(headerText);
    	
    	GridPane pane = new GridPane();
    	ProgressBar bar = new ProgressBar();
    	
    	bar.setMinSize(600, 0);
    	pane.add(bar, 0, 1);
    	pane.setMinSize(600, 0);
    	alert.getDialogPane().setContent(pane);
    	
    	alert.initOwner(JTegraNX.getStage());
    	
    	progress = new ProgressAlert(alert, bar);
    	return progress;
    }

    public static void showAboutDialog() {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("About JTegraNX");
        alert.setHeaderText("");
        alert.setContentText("");

        TextArea aboutTextBox = new TextArea();
        aboutTextBox.setEditable(false);
        aboutTextBox.setWrapText(false);
        aboutTextBox.setText("JTegraNX is a fully cross-platform Nintendo Switch RCM payload injector."
                + "\nI started this project back in 2019 when I tried using TegraRCMGUI and it didn't "
                + "work because of corrupted Visual C++ files.\nBack then JTegraNX started "
                + "off as just a simple TegraRCMSmash GUI with no special features of any kind."
                + "\nLater I then started adding more features on par with some of the features that TegraRCMGUI has and then "
                + "JTegraNX became what it is today."
                + "\n\nCurrent release: " + UpdateHandler.getCurrentVersion() + "\n\nChangelog:"
                + "\n\u2022 Added offline mode"
                + "\n\u2022 Renamed fusee-primary to fusee"
                + "\n\u2022 Replaced GitHandler with kohsuke/github-api"
                + "\n\u2022 Added a preloader so that JTegraNX doesn't hang while preparing"
                + "\n\u2022 Updated the SDPrepare tool and replaced Checkpoint with JKSV."
                + "\n\nCopyright (C) 2019-2022 Dylan Wedman\n"
                + "\n"
                + "This program is free software; you can redistribute it and/or modify\n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation; either version 2 of the License, or\n"
                + "(at your option) any later version.\n"
                + "\n"
                + "This program is distributed in the hope that it will be useful,\n"
                + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                + "GNU General Public License for more details.\n"
                + "\n"
                + "You should have received a copy of the GNU General Public License along\n"
                + "with this program; If not, see http://www.gnu.org/licenses.");
        alert.getDialogPane().setContent(aboutTextBox);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.initOwner(JTegraNX.getStage());
        alert.show();
    }
    
    public static boolean showGPTRestoreDialog() {
    	Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("JTegraNX");
        alert.setHeaderText("The Restore GPT feature uses gptrestore by Rajko Stojadinovic (rajkosto)");
        alert.setContentText("It is strongly recommended that you have a NAND backup ready before you proceed.");
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        alert.initOwner(JTegraNX.getStage());
        
        Optional<ButtonType> returnValue = alert.showAndWait();
        
        return returnValue.isPresent() && returnValue.get().equals(ButtonType.OK);
    }
}
