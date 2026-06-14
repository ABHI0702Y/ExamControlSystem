package com.examcontrol.ui.student;

import com.examcontrol.model.Exam;
import com.examcontrol.model.Question;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.service.UserService.ServiceResult;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ExamTakingPanel extends JPanel {

    private final Exam       exam;
    private final int        studentId;
    private final Runnable   onSubmit;
    private final ExamService   examService   = new ExamService();
    private final ResultService resultService = new ResultService();

    private List<Question> questions;
    private final Map<Integer, String> answers = new HashMap<>();
    private int currentIndex = 0;

    // Timer
    private int remainingSeconds;
    private javax.swing.Timer countdownTimer;

    // UI refs
    private JLabel  timerLabel;
    private JLabel  questionNumberLabel;
    private JLabel  questionTextLabel;
    private ButtonGroup optionGroup;
    private JRadioButton[] optionButtons;
    private JButton prevBtn, nextBtn, submitBtn;
    private JLabel  statusLabel;

    public ExamTakingPanel(Exam exam, int studentId, Runnable onSubmit) {
        this.exam      = exam;
        this.studentId = studentId;
        this.onSubmit  = onSubmit;
        this.remainingSeconds = exam.getDurationMinutes() * 60;

        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));

        questions = examService.getQuestionsForExam(exam.getId());
        buildUI();
        showQuestion(0);
        startTimer();
    }

    private void buildUI() {
        // ── Top bar ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG_MID);
        topBar.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel examTitle = UITheme.label(exam.getTitle(), UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        topBar.add(examTitle, BorderLayout.WEST);

        timerLabel = UITheme.label("", UITheme.FONT_HEADER, UITheme.ACCENT);
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topBar.add(timerLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Question area ────────────────────────────────────────
        JPanel questionArea = new JPanel();
        questionArea.setBackground(UITheme.BG_DARK);
        questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.Y_AXIS));
        questionArea.setBorder(new EmptyBorder(20, 0, 20, 0));

        questionNumberLabel = UITheme.label("", UITheme.FONT_SMALL, UITheme.ACCENT);
        questionNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        questionTextLabel = new JLabel();
        questionTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionTextLabel.setForeground(UITheme.TEXT_PRIMARY);
        questionTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionTextLabel.setBorder(new EmptyBorder(8, 0, 20, 0));

        questionArea.add(questionNumberLabel);
        questionArea.add(questionTextLabel);

        optionGroup   = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        String[] labels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton(labels[i]);
            questionArea.add(optionButtons[i]);
            questionArea.add(Box.createVerticalStrut(8));
        }

        statusLabel = UITheme.label("", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(12, 0, 0, 0));
        questionArea.add(statusLabel);

        add(UITheme.scrollPane(questionArea), BorderLayout.CENTER);

        // ── Navigation ───────────────────────────────────────────
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(UITheme.BG_MID);
        navBar.setBorder(new EmptyBorder(12, 16, 12, 16));

        prevBtn   = UITheme.secondaryButton("◀ Previous");
        nextBtn   = UITheme.primaryButton("Next ▶");
        submitBtn = UITheme.dangerButton("Submit Exam");

        JPanel leftNav  = new JPanel(new FlowLayout(FlowLayout.LEFT));  leftNav.setBackground(UITheme.BG_MID);
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT)); rightNav.setBackground(UITheme.BG_MID);
        leftNav.add(prevBtn);
        rightNav.add(nextBtn);
        rightNav.add(submitBtn);

        navBar.add(leftNav,  BorderLayout.WEST);
        navBar.add(rightNav, BorderLayout.EAST);
        add(navBar, BorderLayout.SOUTH);

        prevBtn.addActionListener(e -> { saveCurrentAnswer(); if (currentIndex > 0) showQuestion(currentIndex - 1); });
        nextBtn.addActionListener(e -> { saveCurrentAnswer(); if (currentIndex < questions.size() - 1) showQuestion(currentIndex + 1); });
        submitBtn.addActionListener(e -> confirmSubmit());
    }

    private JRadioButton createOptionButton(String optionLetter) {
        JRadioButton rb = new JRadioButton();
        rb.setBackground(UITheme.BG_DARK);
        rb.setForeground(UITheme.TEXT_PRIMARY);
        rb.setFont(UITheme.FONT_BODY);
        rb.setFocusPainted(false);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        rb.putClientProperty("letter", optionLetter);
        optionGroup.add(rb);
        return rb;
    }

    private void showQuestion(int index) {
        currentIndex = index;
        Question q = questions.get(index);

        questionNumberLabel.setText("Question " + (index + 1) + " of " + questions.size()
            + "   [" + q.getMarks() + " mark" + (q.getMarks() > 1 ? "s" : "") + "]"
            + (q.getSubject() != null ? "   Subject: " + q.getSubject() : ""));

        questionTextLabel.setText("<html><p style='width:600px'>" + q.getQuestionText() + "</p></html>");

        optionGroup.clearSelection();
        String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText("  " + ((char)('A' + i)) + ".  " + opts[i]);
        }

        String saved = answers.get(q.getId());
        if (saved != null) {
            for (JRadioButton rb : optionButtons) {
                if (saved.equals(rb.getClientProperty("letter"))) { rb.setSelected(true); break; }
            }
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);

        long answered = answers.values().stream().filter(Objects::nonNull).count();
        statusLabel.setText("Answered: " + answered + " / " + questions.size());
    }

    private void saveCurrentAnswer() {
        Question q = questions.get(currentIndex);
        for (JRadioButton rb : optionButtons) {
            if (rb.isSelected()) {
                answers.put(q.getId(), (String) rb.getClientProperty("letter"));
                return;
            }
        }
        answers.remove(q.getId());
    }

    private void startTimer() {
        updateTimerLabel();
        countdownTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Time's up! Submitting your exam.", "Time Up", JOptionPane.WARNING_MESSAGE);
                doSubmit();
            }
        });
        countdownTimer.start();
    }

    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("⏱  %02d:%02d", minutes, seconds));
        if (remainingSeconds <= 300) timerLabel.setForeground(UITheme.ERROR);
        else if (remainingSeconds <= 600) timerLabel.setForeground(UITheme.WARNING);
        else timerLabel.setForeground(UITheme.ACCENT);
    }

    private void confirmSubmit() {
        saveCurrentAnswer();
        long answered = answers.values().stream().filter(Objects::nonNull).count();
        int unanswered = questions.size() - (int) answered;
        String msg = "<html>Are you sure you want to submit?<br>" +
            "Answered: " + answered + " / " + questions.size();
        if (unanswered > 0) msg += "<br><font color='orange'>" + unanswered + " question(s) unanswered.</font>";
        msg += "</html>";
        int choice = JOptionPane.showConfirmDialog(this, msg, "Submit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) doSubmit();
    }

    private void doSubmit() {
        if (countdownTimer != null) countdownTimer.stop();
        saveCurrentAnswer();

        ServiceResult r = resultService.submitExam(
            studentId, exam.getId(), questions, answers, exam.getTotalMarks(), exam.getPassingMarks());

        Color color = r.success() ? UITheme.SUCCESS : UITheme.ERROR;
        JOptionPane.showMessageDialog(this, r.message(),
            r.success() ? "Exam Submitted" : "Submission Error",
            r.success() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        onSubmit.run();
    }
}
