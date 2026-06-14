package com.examcontrol.ui;

import com.examcontrol.model.User;
import com.examcontrol.service.AuthService;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final JTextField     usernameField = UITheme.textField();
    private final JPasswordField passwordField = UITheme.passwordField();
    private final JLabel         errorLabel    = UITheme.label("", UITheme.FONT_SMALL, UITheme.ERROR);
    private final JButton        loginButton   = UITheme.primaryButton("Sign In");

    public LoginFrame() {
        setTitle("Exam Control System — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.BG_DARK);
        root.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0; gbc.weightx = 1;

        // Logo / Title
        JLabel logo = UITheme.label("ECS", new Font("Segoe UI", Font.BOLD, 44), UITheme.ACCENT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        root.add(logo, gbc);

        JLabel title = UITheme.label("Exam Control System", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 6, 0);
        root.add(title, gbc);

        JLabel sub = UITheme.label("Sign in to continue", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 24, 0);
        root.add(sub, gbc);

        gbc.insets = new Insets(6, 0, 2, 0);
        gbc.gridy = 3;
        root.add(UITheme.label("Username", UITheme.FONT_BODY, UITheme.TEXT_MUTED), gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        usernameField.setPreferredSize(new Dimension(300, 38));
        root.add(usernameField, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(6, 0, 2, 0);
        root.add(UITheme.label("Password", UITheme.FONT_BODY, UITheme.TEXT_MUTED), gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 6, 0);
        passwordField.setPreferredSize(new Dimension(300, 38));
        root.add(passwordField, gbc);

        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 12, 0);
        root.add(errorLabel, gbc);

        loginButton.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 8; gbc.insets = new Insets(4, 0, 0, 0);
        root.add(loginButton, gbc);

        JLabel hint = UITheme.label("Default admin: admin / Admin@123", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 9; gbc.insets = new Insets(20, 0, 0, 0);
        root.add(hint, gbc);

        setContentPane(root);

        loginButton.addActionListener(e -> doLogin());
        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        errorLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");

        SwingWorker<Optional<User>, Void> worker = new SwingWorker<>() {
            protected Optional<User> doInBackground() {
                return AuthService.getInstance().login(username, password);
            }
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In");
                try {
                    Optional<User> opt = get();
                    if (opt.isPresent()) {
                        dispose();
                        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    errorLabel.setText("Connection error. Check database.");
                }
            }
        };
        worker.execute();
    }
}
