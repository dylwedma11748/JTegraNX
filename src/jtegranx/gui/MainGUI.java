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

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtegranx.util.TegraRCM;
import static jtegranx.util.ConfigManager.*;
import jtegranx.util.Threads;
import static jtegranx.util.Updater.checkForUpdates;

public class MainGUI extends JFrame {

    public static LoadConfigDialog load = new LoadConfigDialog(null, true);
    private final Threads.StatusUpdaterThread statusUpdater = new Threads.StatusUpdaterThread();
    private Threads.AutoInjectorThread autoInjector;
    public static boolean rcmDetected, injected = false;

    @SuppressWarnings({"LeakingThisInConstructor", "CallToThreadStartDuringObjectConstruction"})
    public MainGUI() {
        initComponents();
        setIcon();
        statusUpdater.start();
        checkForUpdates(this);
    }

    private void setIcon() {
        setIconImage(new ImageIcon(getClass().getResource("/jtegranx/gui/images/icon.png")).getImage());
    }

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
        RCMStatus = new javax.swing.JLabel();
        MenuBar = new javax.swing.JMenuBar();
        Options = new javax.swing.JMenu();
        AutoInject = new javax.swing.JCheckBoxMenuItem();
        HideLog = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JTegraNX");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Log.setEditable(false);
        Log.setColumns(20);
        Log.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        Log.setRows(5);
        ScrollPane.setViewportView(Log);

        getContentPane().add(ScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 390, 140));

        PayloadPathLabel.setText("Payload Path:");
        getContentPane().add(PayloadPathLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 20));

        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("JTegraNX - Another TegraRcmSmash GUI");
        getContentPane().add(TopLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 580, -1));
        getContentPane().add(PayloadPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 410, -1));

        Browse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jtegranx/gui/images/browse.png"))); // NOI18N
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
        getContentPane().add(Inject, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, 110, 30));

        VersionLabel.setText("v1.3");
        getContentPane().add(VersionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

        ArgumentsLabel.setText("Arguments:");
        getContentPane().add(ArgumentsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 60, 20));
        getContentPane().add(Arguments, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 70, 410, -1));

        SaveConfig.setText("Save Config");
        SaveConfig.setToolTipText("Save current settings to a config file.");
        SaveConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveConfigActionPerformed(evt);
            }
        });
        getContentPane().add(SaveConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 100, 110, 30));

        LoadConfig.setText("Load Config");
        LoadConfig.setToolTipText("Load a config file.");
        LoadConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadConfigActionPerformed(evt);
            }
        });
        getContentPane().add(LoadConfig, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 100, 110, 30));

        RCMStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jtegranx/gui/images/init.png"))); // NOI18N
        getContentPane().add(RCMStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 170, -1, 90));

        Options.setText("Options");

        AutoInject.setText("Auto-inject");
        AutoInject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoInjectActionPerformed(evt);
            }
        });
        Options.add(AutoInject);

        HideLog.setText("Hide log");
        HideLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HideLogActionPerformed(evt);
            }
        });
        Options.add(HideLog);

        MenuBar.add(Options);

        setJMenuBar(MenuBar);

        setSize(new java.awt.Dimension(616, 357));
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
        if (rcmDetected) {
            Inject.setEnabled(false);
            TegraRCM.injectPayload(this, PayloadPath.getText(), Arguments.getText());
        }
    }//GEN-LAST:event_InjectActionPerformed

    private void SaveConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveConfigActionPerformed
        SaveConfigDialog save = new SaveConfigDialog(this, true);
        save.setVisible(true);
    }//GEN-LAST:event_SaveConfigActionPerformed

    private void LoadConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadConfigActionPerformed
        updateConfigList();
        load.setVisible(true);
    }//GEN-LAST:event_LoadConfigActionPerformed

    private void AutoInjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoInjectActionPerformed
        if (!new File(PayloadPath.getText()).exists()) {
            System.out.println("You need to select a payload before enabling auto-inject.");
            Log.append("\nSelect payload or config before enabling auto-inject");
            AutoInject.setSelected(false);
        } else {
            if (AutoInject.isSelected()) {
                Inject.setEnabled(false);
                autoInjector = new Threads.AutoInjectorThread(this);
                autoInjector.start();
            } else {
                autoInjector.abort();
            }
        }
    }//GEN-LAST:event_AutoInjectActionPerformed

    private void HideLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HideLogActionPerformed
        if (HideLog.isSelected()) {
            ScrollPane.setVisible(false);
            RCMStatus.setLocation(210, 170);
        } else {
            ScrollPane.setVisible(true);
            RCMStatus.setLocation(410, 170);
        }
    }//GEN-LAST:event_HideLogActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        statusUpdater.abort();
        
        if (autoInjector != null) {
            autoInjector.abort();
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JFormattedTextField Arguments;
    private javax.swing.JLabel ArgumentsLabel;
    public static javax.swing.JCheckBoxMenuItem AutoInject;
    private javax.swing.JButton Browse;
    private javax.swing.JCheckBoxMenuItem HideLog;
    public static javax.swing.JButton Inject;
    public static javax.swing.JButton LoadConfig;
    public static javax.swing.JTextArea Log;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenu Options;
    public static javax.swing.JFormattedTextField PayloadPath;
    private javax.swing.JLabel PayloadPathLabel;
    public static javax.swing.JLabel RCMStatus;
    public static javax.swing.JButton SaveConfig;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JLabel TopLabel;
    public static javax.swing.JLabel VersionLabel;
    // End of variables declaration//GEN-END:variables
}
