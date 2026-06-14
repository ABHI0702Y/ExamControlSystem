package com.examcontrol.ui.teacher;

import com.examcontrol.service.AuthService;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TeacherPanel extends JPanel {

    private final ExamService   examService   = new ExamService();
    private final ResultService resultService = new ResultService();
    private final JPanel contentArea = new JPanel(new CardLayout());

    public TeacherPanel() {
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_MID);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(16, 8, 16, 8));

        JLabel navTitle = UITheme.label("TEACHER MENU", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(new EmptyBorder(0, 8, 12, 0));
        sidebar.add(navTitle);

        String[] labels = {"Dashboard", "My Exams", "Question Bank", "Results"};
        String[] cards  = {"DASH",      "EXAMS",    "QUESTIONS",     "RESULTS"};
        for (int i = 0; i < labels.length; i++) {
            sidebar.add(navButton(labels[i], cards[i]));
            sidebar.add(Box.createVerticalStrut(4));
        }
        sidebar.add(Box.createVerticalGlue());

        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.add(buildDashboard(),                       "DASH");
        contentArea.add(new ExamManagementPanel(),              "EXAMS");
        contentArea.add(new com.examcontrol.ui.admin.QuestionManagementPanel(), "QUESTIONS");
        contentArea.add(new ResultsViewPanel(0, false),         "RESULTS");

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

        JLabel title = UITheme.label("Teacher Dashboard", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel sub   = UITheme.label("Welcome, " + AuthService.getInstance().getCurrentUser().getFullName(),
                                      UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JPanel top = new JPanel(new BorderLayout(0, 4)); top.setBackground(UITheme.BG_DARK);
        top.add(title, BorderLayout.NORTH); top.add(sub, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 0));
        cards.setBackground(UITheme.BG_DARK);
        cards.setBorder(new EmptyBorder(20, 0, 0, 0));
        cards.add(statCard("Total Exams",   String.valueOf(examService.getAllExams().size()),   UITheme.ACCENT));
        cards.add(statCard("Total Results", String.valueOf(resultService.countTotalResults()), UITheme.SUCCESS));
        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());
        JLabel v = UITheme.label(value, new Font("Segoe UI", Font.BOLD, 40), accent);
        v.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel l = UITheme.label(label, UITheme.FONT_HEADER, UITheme.TEXT_MUTED);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(v, BorderLayout.CENTER);
        card.add(l, BorderLayout.SOUTH);
        return card;
    }
}
