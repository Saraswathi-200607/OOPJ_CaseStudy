package com.banking;
import com.banking.ui.LoginFrame;
import com.banking.util.DatabaseConnection;
 
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
    	 
        // Set system look-and-feel (optional — comment out for default Swing L&F)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
 
        // Verify DB connectivity before showing UI
        try {
            DatabaseConnection.getInstance();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database.\n\n" + ex.getCause().getMessage()
                    + "\n\nPlease check:\n"
                    + " • MySQL is running\n"
                    + " • Credentials in DatabaseConnection.java are correct\n"
                    + " • schema.sql has been executed",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
 
        // Launch on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
 
        // Graceful shutdown — close DB connection on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                DatabaseConnection.getInstance().close()));
    }

}
