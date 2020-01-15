/*

JTegraNX - Another GUI for TegraRcmSmash

Copyright (C) 2020 Dylan Wedman

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
package jtegranx;

import jtegranx.gui.MainGUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static jtegranx.util.ConfigManager.*;
import jtegranx.payloads.PayloadManager;
import static jtegranx.util.ResourceLoader.*;

public class Main {

    public static void main(String[] args) {
        if (!System.getProperty("os.name").contains("Windows")) {
            JOptionPane.showMessageDialog(null, "JTegraNX is only supported on Windows", "Unsupported Platform", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                loadResources();
                generateSDConfig();
                PayloadManager.initPayloads();
                MainGUI gui = new MainGUI();
                gui.setVisible(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
