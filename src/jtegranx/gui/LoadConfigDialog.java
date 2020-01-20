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

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import jtegranx.util.ConfigManager;

public class LoadConfigDialog extends JDialog {

    public LoadConfigDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setIcon();
    }
    
    private void setIcon() {
        URL url = getClass().getResource("/jtegranx/gui/images/icon.png");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollPane = new javax.swing.JScrollPane();
        ConfigList = new javax.swing.JList<>();
        TopLabel = new javax.swing.JLabel();
        Load = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Load Config");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ScrollPane.setViewportView(ConfigList);

        getContentPane().add(ScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 380, 80));

        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("Available Configs");
        getContentPane().add(TopLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 380, -1));

        Load.setText("Load");
        Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadActionPerformed(evt);
            }
        });
        getContentPane().add(Load, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, -1, -1));

        setSize(new java.awt.Dimension(416, 188));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void LoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadActionPerformed
        if (ConfigList.getSelectedIndex() != -1) {
            ConfigManager.loadConfig(ConfigList.getSelectedValue());
            setVisible(false);
        }
    }//GEN-LAST:event_LoadActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JList<String> ConfigList;
    private javax.swing.JButton Load;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JLabel TopLabel;
    // End of variables declaration//GEN-END:variables
}
