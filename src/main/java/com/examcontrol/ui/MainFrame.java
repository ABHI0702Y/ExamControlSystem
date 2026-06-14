package com.examcontrol.ui;

import com.examcontrol.model.Role;
import com.examcontrol.model.User;
import com.examcontrol.service.AuthService;
import com.examcontrol.ui.admin.AdminPanel;
import com.examcontrol.ui.student.StudentPanel;
import com.examcontrol.ui.teacher.TeacherPanel;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final AuthService auth = AuthService.getInstance();

    public MainFrame() {
        User user = auth.getCurrentUser();
        setTitle("Exam Control System — " + user.getFullName() + " (" + user.getRole() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        buildUI(user);
    }

    private void buildUI(User user) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        // ── Top bar ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG_MID);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel appName = UITheme.label("Exam Control System", UITheme.FONT_HEADER, UITheme.ACCENT);
        topBar.add(appName, BorderLayout.WEST);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBar.setBackground(UITheme.BG_MID);

        String roleColor = switch (user.getRole()) {
            case ADMIN   -> "#F38BA8";
            case TEACHER -> "#FAB387";
            case STUDENT -> "#A6E3A1";
        };
        JLabel roleLabel = new JLabel("<html><span style='color:" + roleColor + "'>"
            + user.getRole() + "</span></html>");
        roleLabel.setFont(UITheme.FONT_SMALL);

        JLabel userLabel = UITheme.label(user.getFullName(), UITheme.FONT_BODY, UITheme.TEXT_PRIMARY);
        JButton logoutBtn = UITheme.secondaryButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        rightBar.add(userLabel);
        rightBar.add(roleLabel);
        rightBar.add(logoutBtn);
        topBar.add(rightBar, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);
        root.add(UITheme.separator(), BorderLayout.SOUTH);

        // ── Role panel ──────────────────────────────────────────
        JPanel content;
        if (user.getRole() == Role.ADMIN)        content = new AdminPanel();
        else if (user.getRole() == Role.TEACHER)  content = new TeacherPanel();
        else                                      content = new StudentPanel();

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            auth.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
