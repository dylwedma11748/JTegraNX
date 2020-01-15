package jtegranx.gui;

import java.awt.Frame;
import javax.swing.JDialog;
import jtegranx.util.ConfigManager;

public class SaveConfigDialog extends JDialog {

    public SaveConfigDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
