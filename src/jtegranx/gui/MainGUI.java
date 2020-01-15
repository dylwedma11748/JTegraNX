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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtegranx.util.TegraRCM;
import static jtegranx.util.ResourceLoader.*;
import static jtegranx.util.ConfigManager.*;
import static jtegranx.util.Updater.checkForUpdates;

public class MainGUI extends JFrame {

    @SuppressWarnings("LeakingThisInConstructor")
    public MainGUI() {
        initComponents();
        setIcon();
        checkForUpdates(this);
    }
    
    private void setIcon() {
        URL url = getClass().getResource("/jtegranx/gui/icon.png");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());
    }

    public static LoadConfigDialog load = new LoadConfigDialog(null, true);

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollPane = new javax.swing.JScrollPane();
        Log = new javax.swing.JTextArea();
        PayloadPathLabel = new javax.swing.JLabel();
        TopLabel = new javax.swing.JLabel();
        PayloadPath = new javax.swing.JFormattedTextField();
        Browse = new javax.swing.JButton();
        Inject = new javax.swing.JButton();
        VersionLabel = new javax.swing.JLabel();
        ArgumentsLabel = new javax.swing.JLabel();
        Arguments = new javax.swing.JFormattedTextField();
        SaveConfig = new javax.swing.JButton();
        LoadConfig = new javax.swing.JButton();
        Reset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JTegraNX");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Log.setEditable(false);
        Log.setColumns(20);
        Log.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        Log.setRows(5);
        ScrollPane.setViewportView(Log);

        getContentPane().add(ScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 590, 140));

        PayloadPathLabel.setText("Payload Path:");
        getContentPane().add(PayloadPathLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 20));

        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("JTegraNX - Another TegraRcmSmash GUI");
        getContentPane().add(TopLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 580, -1));
        getContentPane().add(PayloadPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 410, -1));

        Browse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jtegranx/gui/browse.png"))); // NOI18N
        Browse.setToolTipText("Browse for payload.");
        Browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseActionPerformed(evt);
            }
        });
        getContentPane().add(Browse, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

        Inject.setText("Inject");
        Inject.setToolTipText("Inject specified payload with specified arguments.");
        Inject.setEnabled(false);
        Inject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InjectActionPerformed(evt);
            }
        });
        getContentPane().add(Inject, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 100, 110, 30));

        VersionLabel.setText("v1.2");
        getContentPane().add(VersionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

        ArgumentsLabel.setText("Arguments:");
        getContentPane().add(ArgumentsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 60, 20));
        getContentPane().add(Arguments, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 410, -1));

        SaveConfig.setText("Save Config");
        SaveConfig.setToolTipText("Save current settings to a config file.");
        SaveConfig.setEnabled(false);
        SaveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveConfigActionPerformed(evt);
            }
        });
        getContentPane().add(SaveConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 110, 30));

        LoadConfig.setText("Load Config");
        LoadConfig.setToolTipText("Load a config file.");
        LoadConfig.setEnabled(false);
        LoadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadConfigActionPerformed(evt);
            }
        });
        getContentPane().add(LoadConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, 110, 30));

        Reset.setText("Reset");
        Reset.setToolTipText("Reset for a fresh start.");
        Reset.setEnabled(false);
        Reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetActionPerformed(evt);
            }
        });
        getContentPane().add(Reset, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 100, 110, 30));

        setSize(new java.awt.Dimension(616, 338));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void BrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrowseActionPerformed
        FileFilter filter = new FileNameExtensionFilter("Bin Files", "bin");

        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(filter);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Open Payload");
        

        int returnValue = chooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            PayloadPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_BrowseActionPerformed

    private void InjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InjectActionPerformed
        if (PayloadPath.getText().replaceAll("\\W", "").equals("")) {
            JOptionPane.showMessageDialog(this, "You need to choose a payload to inject, silly.", "What are you thinking?", JOptionPane.INFORMATION_MESSAGE);
        } else {
            if (Inject.getText().equals("Inject")) {
                PayloadPath.setEditable(false);
                Arguments.setEditable(false);
                Browse.setEnabled(false);
                LoadConfig.setEnabled(false);
                Reset.setEnabled(false);

                String[] args = Arguments.getText().split(" ");

                if (TegraRCM.detectRCMDevice()) {
                    TegraRCM.injectPayload(this, PayloadPath.getText(), args);
                    PayloadPath.setEditable(true);
                    Arguments.setEditable(true);
                    Browse.setEnabled(true);
                    LoadConfig.setEnabled(true);
                    Reset.setEnabled(true);
                } else {
                    Log.append("\nWaiting for RCM device");
                    Inject.setText("Cancel");
                }

                new Thread() {
                    @Override
                    public void run() {
                        while (Inject.getText().equals("Cancel")) {
                            if (TegraRCM.detectRCMDevice()) {
                                TegraRCM.injectPayload(MainGUI.this, PayloadPath.getText(), args);
                                PayloadPath.setEditable(true);
                                Arguments.setEditable(true);
                                Browse.setEnabled(true);
                                Inject.setText("Inject");
                            }
                        }
                    }
                }.start();
            } else {
                Log.append("\nCanceled by user");
                PayloadPath.setEditable(true);
                Arguments.setEditable(true);
                Browse.setEnabled(true);
                LoadConfig.setEnabled(true);
                Reset.setEnabled(true);
                Inject.setText("Inject");
            }
        }
    }//GEN-LAST:event_InjectActionPerformed

    private void SaveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveConfigActionPerformed
        if (PayloadPath.getText().replaceAll("\\W", "").equals("")) {
            JOptionPane.showMessageDialog(this, "Why create a config if you don't even choose a payload, silly?", "What are you thinking?", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SaveConfigDialog save = new SaveConfigDialog(this, true);
            save.setVisible(true);
        }
    }//GEN-LAST:event_SaveConfigActionPerformed

    private void LoadConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadConfigActionPerformed
        updateConfigList();
        load.setVisible(true);
    }//GEN-LAST:event_LoadConfigActionPerformed

    private void ResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetActionPerformed
        setVisible(false);
        dispose();
        loadResources();
        generateSDConfig();
        MainGUI gui = new MainGUI();
        gui.setVisible(true);
    }//GEN-LAST:event_ResetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JFormattedTextField Arguments;
    private javax.swing.JLabel ArgumentsLabel;
    private javax.swing.JButton Browse;
    public static javax.swing.JButton Inject;
    public static javax.swing.JButton LoadConfig;
    public static javax.swing.JTextArea Log;
    public static javax.swing.JFormattedTextField PayloadPath;
    private javax.swing.JLabel PayloadPathLabel;
    public static javax.swing.JButton Reset;
    public static javax.swing.JButton SaveConfig;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JLabel TopLabel;
    public static javax.swing.JLabel VersionLabel;
    // End of variables declaration//GEN-END:variables
}
