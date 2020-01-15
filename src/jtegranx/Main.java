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
