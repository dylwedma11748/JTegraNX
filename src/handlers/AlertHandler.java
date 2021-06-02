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

import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import ui.fx.JTegraNX;

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
}
