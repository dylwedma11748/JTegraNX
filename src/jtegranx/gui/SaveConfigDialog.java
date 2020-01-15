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
package jtegranx.gui;

import java.awt.Frame;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import jtegranx.util.ConfigManager;

public class SaveConfigDialog extends JDialog {

    public SaveConfigDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setIcon();
    }
    
    private void setIcon() {
        URL url = getClass().getResource("/jtegranx/gui/icon.png");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConfigName = new javax.swing.JFormattedTextField();
        TopLabel = new javax.swing.JLabel();
        ConfigNameLabel = new javax.swing.JLabel();
        Save = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save Config");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(ConfigName, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 300, -1));

        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("Save config as");
        getContentPane().add(TopLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 380, -1));

        ConfigNameLabel.setText("Config Name:");
        getContentPane().add(ConfigNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 20));

        Save.setText("Save");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        getContentPane().add(Save, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 70, -1));

        setSize(new java.awt.Dimension(416, 138));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        if (!ConfigName.getText().replaceAll("\\W", "").equals("")) {
            ConfigManager.saveConfig(ConfigName.getText());
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_SaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField ConfigName;
    private javax.swing.JLabel ConfigNameLabel;
    private javax.swing.JButton Save;
    private javax.swing.JLabel TopLabel;
    // End of variables declaration//GEN-END:variables
}
