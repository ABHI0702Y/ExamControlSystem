package com.examcontrol.ui.student;

import com.examcontrol.model.Exam;
import com.examcontrol.service.AuthService;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentPanel extends JPanel {

    private final ExamService   examService   = new ExamService();
    private final ResultService resultService = new ResultService();
    private final JPanel contentArea = new JPanel(new CardLayout());
    private final int studentId = AuthService.getInstance().getCurrentUser().getId();

    private ExamTakingPanel examTakingPanel;

    public StudentPanel() {
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

        JLabel navTitle = UITheme.label("STUDENT MENU", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(new EmptyBorder(0, 8, 12, 0));
        sidebar.add(navTitle);

        sidebar.add(navButton("Dashboard",      "DASH"));    sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("Available Exams","EXAMS"));   sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(navButton("My Results",     "RESULTS")); sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(Box.createVerticalGlue());

        contentArea.setBackground(UITheme.BG_DARK);
        contentArea.add(buildDashboard(),   "DASH");
        contentArea.add(buildExamsPanel(),  "EXAMS");
        contentArea.add(new MyResultsPanel(studentId), "RESULTS");

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

        JLabel title = UITheme.label("Student Dashboard", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel sub   = UITheme.label("Welcome, " + AuthService.getInstance().getCurrentUser().getFullName(),
                                      UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setBackground(UITheme.BG_DARK);
        top.add(title, BorderLayout.NORTH); top.add(sub, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 0));
        cards.setBackground(UITheme.BG_DARK);
        cards.setBorder(new EmptyBorder(20, 0, 0, 0));

        int available = examService.getAvailableForStudent(studentId).size();
        int completed = resultService.getResultsByStudent(studentId).size();
        cards.add(statCard("Available Exams",  String.valueOf(available), UITheme.ACCENT));
        cards.add(statCard("Exams Completed",  String.valueOf(completed), UITheme.SUCCESS));

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildExamsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.add(UITheme.label("Available Exams", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY), BorderLayout.WEST);
        JButton refresh = UITheme.secondaryButton("Refresh");
        header.add(refresh, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        JPanel examListPanel = new JPanel();
        examListPanel.setLayout(new BoxLayout(examListPanel, BoxLayout.Y_AXIS));
        examListPanel.setBackground(UITheme.BG_DARK);

        Runnable loadExams = () -> {
            examListPanel.removeAll();
            java.util.List<Exam> exams = examService.getAvailableForStudent(studentId);
            if (exams.isEmpty()) {
                JLabel empty = UITheme.label("No exams available right now.", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                examListPanel.add(empty);
            }
            for (Exam exam : exams) {
                examListPanel.add(examCard(exam));
                examListPanel.add(Box.createVerticalStrut(12));
            }
            examListPanel.revalidate();
            examListPanel.repaint();
        };

        loadExams.run();
        refresh.addActionListener(e -> loadExams.run());
        p.add(UITheme.scrollPane(examListPanel), BorderLayout.CENTER);
        return p;
    }

    private JPanel examCard(Exam exam) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout(16, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 4));
        info.setBackground(UITheme.BG_CARD);
        info.add(UITheme.label(exam.getTitle(), UITheme.FONT_HEADER, UITheme.TEXT_PRIMARY));
        info.add(UITheme.label(
            "Duration: " + exam.getDurationMinutes() + " min  |  Questions: " + exam.getQuestionCount()
            + "  |  Total Marks: " + exam.getTotalMarks() + "  |  Passing: " + exam.getPassingMarks(),
            UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        String desc = exam.getDescription() != null ? exam.getDescription() : "";
        info.add(UITheme.label(desc, UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(info, BorderLayout.CENTER);

        JButton startBtn = UITheme.primaryButton("Start Exam");
        startBtn.addActionListener(e -> startExam(exam));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnPanel.add(startBtn);
        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }

    private void startExam(Exam exam) {
        if (exam.getQuestionCount() == 0) {
            JOptionPane.showMessageDialog(this, "This exam has no questions yet.", "Cannot Start", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "<html><b>" + exam.getTitle() + "</b><br>" +
            "Duration: " + exam.getDurationMinutes() + " minutes<br>" +
            "Questions: " + exam.getQuestionCount() + "<br>" +
            "Total marks: " + exam.getTotalMarks() + "<br><br>" +
            "Once started, you must complete the exam.<br>Ready to begin?</html>",
            "Start Exam", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            examTakingPanel = new ExamTakingPanel(exam, studentId, this::onExamSubmitted);
            contentArea.add(examTakingPanel, "TAKING");
            ((CardLayout) contentArea.getLayout()).show(contentArea, "TAKING");
        }
    }

    private void onExamSubmitted() {
        ((CardLayout) contentArea.getLayout()).show(contentArea, "RESULTS");
        contentArea.remove(examTakingPanel);
        examTakingPanel = null;
    }

    private JPanel statCard(String label, String value, Color accent) {
        JPanel card = UITheme.card();
        card.setLayout(new BorderLayout());
        JLabel v = UITheme.label(value, new Font("Segoe UI", Font.BOLD, 40), accent);
        v.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel l = UITheme.label(label, UITheme.FONT_HEADER, UITheme.TEXT_MUTED);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(v, BorderLayout.CENTER); card.add(l, BorderLayout.SOUTH);
        return card;
    }
}
