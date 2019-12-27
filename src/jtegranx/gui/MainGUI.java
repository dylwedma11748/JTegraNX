package jtegranx.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import jtegranx.util.TegraRCM;

public class MainGUI extends javax.swing.JFrame {

    public MainGUI() {
        initComponents();
        Log.append("Waiting for input");
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JTegraNX");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Log.setEditable(false);
        Log.setColumns(20);
        Log.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        Log.setRows(5);
        ScrollPane.setViewportView(Log);

        getContentPane().add(ScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 590, 160));

        PayloadPathLabel.setText("Payload Path:");
        getContentPane().add(PayloadPathLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, 20));

        TopLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TopLabel.setText("JTegraNX - TegraRcmSmash GUI by Dylan Wedman");
        getContentPane().add(TopLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 580, -1));
        getContentPane().add(PayloadPath, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 50, 410, -1));

        Browse.setText("Browse");
        Browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseActionPerformed(evt);
            }
        });
        getContentPane().add(Browse, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

        Inject.setText("Inject");
        Inject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InjectActionPerformed(evt);
            }
        });
        getContentPane().add(Inject, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 80, 110, 30));

        VersionLabel.setText("v1.0");
        getContentPane().add(VersionLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, -1, -1));

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

        int returnValue = chooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            PayloadPath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_BrowseActionPerformed

    private void InjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InjectActionPerformed
        if (Inject.getText().equals("Inject")) {
            PayloadPath.setEditable(false);
            Browse.setEnabled(false);

            if (TegraRCM.detectRCMDevice()) {
                TegraRCM.injectPayload(this, PayloadPath.getText(), null);
                PayloadPath.setEditable(true);
                Browse.setEnabled(true);
            } else {
                Log.append("\nWaiting for RCM device");
                Inject.setText("Cancel");
            }

            new Thread() {
                @Override
                public void run() {
                    while (Inject.getText().equals("Cancel")) {
                        if (TegraRCM.detectRCMDevice()) {
                            TegraRCM.injectPayload(MainGUI.this, PayloadPath.getText(), null);
                            PayloadPath.setEditable(true);
                            Browse.setEnabled(true);
                            Inject.setText("Inject");
                        }
                    }
                }
            }.start();
        } else {
            Log.append("\nCanceled by user");
            Inject.setText("Inject");
            PayloadPath.setEditable(true);
            Browse.setEnabled(true);
        }
    }//GEN-LAST:event_InjectActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Browse;
    private javax.swing.JButton Inject;
    public static javax.swing.JTextArea Log;
    private javax.swing.JFormattedTextField PayloadPath;
    private javax.swing.JLabel PayloadPathLabel;
    private javax.swing.JScrollPane ScrollPane;
    private javax.swing.JLabel TopLabel;
    private javax.swing.JLabel VersionLabel;
    // End of variables declaration//GEN-END:variables
}
