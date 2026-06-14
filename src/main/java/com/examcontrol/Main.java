package com.examcontrol;

import com.examcontrol.config.DatabaseConfig;
import com.examcontrol.ui.LoginFrame;
import com.formdev.flatlaf.FlatDarkLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Install FlatLaf dark theme before any Swing component is created
        try {
            FlatDarkLaf.setup();
            // Override specific defaults to match our custom palette
            UIManager.put("Component.arc", 6);
            UIManager.put("Button.arc", 6);
            UIManager.put("TextComponent.arc", 6);
        } catch (Exception e) {
            log.warn("FlatLaf failed, using system L&F", e);
        }

        // Eagerly test DB connection on startup
        SwingWorker<Boolean, Void> dbCheck = new SwingWorker<>() {
            protected Boolean doInBackground() {
                try {
                    DatabaseConfig.getInstance().getConnection().close();
                    return true;
                } catch (Exception e) {
                    log.error("Database connection failed", e);
                    return false;
                }
            }
            protected void done() {
                try {
                    if (!get()) {
                        JOptionPane.showMessageDialog(null,
                            "<html><b>Cannot connect to the database.</b><br>" +
                            "Please check config.properties and ensure MySQL is running.<br>" +
                            "Then restart the application.</html>",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                    new LoginFrame().setVisible(true);
                } catch (Exception e) {
                    log.error("Startup error", e);
                    System.exit(1);
                }
            }
        };

        SwingUtilities.invokeLater(dbCheck::execute);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseConfig.getInstance().close()));
    }
}
