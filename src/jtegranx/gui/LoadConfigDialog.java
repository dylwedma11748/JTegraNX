package jtegranx.gui;

import javax.swing.JDialog;
import jtegranx.util.ConfigManager;

public class LoadConfigDialog extends JDialog {

    public LoadConfigDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
