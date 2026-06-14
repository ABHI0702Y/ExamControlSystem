package com.examcontrol.ui.admin;

import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.service.UserService;
import com.examcontrol.model.Role;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPanel extends JPanel {

    private final UserService   userService   = new UserService();
    private final ExamService   examService   = new ExamService();
    private final ResultService resultService = new ResultService();

    private final JPanel contentArea = new JPanel(new CardLayout());

    public AdminPanel() {
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // ── Sidebar ────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_MID);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 8, 16, 8));

        JLabel navTitle = UITheme.label("ADMIN MENU", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(new EmptyBorder(0, 8, 12, 0));
        sidebar.add(navTitle);

        String[] sections = {"Dashboard", "Users", "Questions", "Exams", "Results"};
        String[] cards    = {"DASHBOARD", "USERS", "QUESTIONS", "EXAMS", "RESULTS"};
        for (int i = 0; i < sections.length; i++) {
            sidebar.add(navButton(sections[i], cards[i]));
            sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());

        // ── Content panels ─────────────────────────────────────
        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.add(buildDashboard(),          "DASHBOARD");
        contentArea.add(new UserManagementPanel(), "USERS");
        contentArea.add(new QuestionManagementPanel(), "QUESTIONS");
        contentArea.add(new ExamManagementAdminPanel(), "EXAMS");
        contentArea.add(buildResultsPanel(),       "RESULTS");

        add(sidebar,     BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private JButton navButton(String label, String card) {
        JButton btn = new JButton("  " + label);
        btn.setBackground(UITheme.BG_MID);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setFont(UITheme.FONT_BODY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(UITheme.BG_CARD); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(UITheme.BG_MID); }
        });
        btn.addActionListener(e -> ((CardLayout) contentArea.getLayout()).show(contentArea, card));
        return btn;
    }

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = UITheme.label("Dashboard", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setBackground(UITheme.BG_DARK);
        cards.setBorder(new EmptyBorder(20, 0, 0, 0));

        cards.add(statCard("Students",  String.valueOf(userService.countByRole(Role.STUDENT)), UITheme.ACCENT));
        cards.add(statCard("Teachers",  String.valueOf(userService.countByRole(Role.TEACHER)), UITheme.WARNING));
        cards.add(statCard("Exams",     String.valueOf(examService.getAllExams().size()),       UITheme.SUCCESS));
        cards.add(statCard("Attempts",  String.valueOf(resultService.countTotalResults()),      UITheme.ERROR));

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());
        JLabel valLabel = UITheme.label(value, new Font("Segoe UI", Font.BOLD, 40), accent);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbl = UITheme.label(label, UITheme.FONT_HEADER, UITheme.TEXT_MUTED);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valLabel, BorderLayout.CENTER);
        card.add(lbl,      BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildResultsPanel() {
        var results = new com.examcontrol.ui.teacher.ResultsViewPanel(0, true);
        return results;
    }
}
