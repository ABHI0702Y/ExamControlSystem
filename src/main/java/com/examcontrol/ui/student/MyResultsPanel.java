package com.examcontrol.ui.student;

import com.examcontrol.model.Result;
import com.examcontrol.model.StudentAnswer;
import com.examcontrol.model.Question;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyResultsPanel extends JPanel {

    private final ResultService resultService = new ResultService();
    private final ExamService   examService   = new ExamService();
    private final int studentId;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Result> results;

    public MyResultsPanel(int studentId) {
        this.studentId = studentId;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(
            new String[]{"#", "Exam", "Score", "Total", "Percentage", "Result", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);

        // Color PASS/FAIL cells
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("PASS".equals(v)) setForeground(UITheme.SUCCESS);
                else if ("FAIL".equals(v)) setForeground(UITheme.ERROR);
                else setForeground(UITheme.TEXT_PRIMARY);
                if (!sel) setBackground(UITheme.BG_MID);
                return this;
            }
        });

        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.add(UITheme.label("My Results", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_DARK);
        JButton refresh  = UITheme.secondaryButton("Refresh");
        JButton viewBtn  = UITheme.secondaryButton("View Details");
        actions.add(refresh); actions.add(viewBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadData());
        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a result first.", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
            showDetailDialog(results.get(row));
        });
    }

    private void loadData() {
        results = resultService.getResultsByStudent(studentId);
        tableModel.setRowCount(0);
        int i = 1;
        for (Result r : results) {
            tableModel.addRow(new Object[]{
                i++, r.getExamTitle(), r.getScore(), r.getTotalMarks(),
                String.format("%.1f%%", r.getPercentage()),
                r.isPassed() ? "PASS" : "FAIL",
                r.getSubmittedAt() != null ? r.getSubmittedAt().toString().substring(0, 16) : ""
            });
        }
    }

    private void showDetailDialog(Result result) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Result Detail — " + result.getExamTitle(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(720, 560);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.setLayout(new BorderLayout(0, 12));

        // Summary bar
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        summary.setBackground(UITheme.BG_MID);
        summary.setBorder(new EmptyBorder(8, 16, 8, 16));
        summary.add(badge("Score",      result.getScore() + "/" + result.getTotalMarks(), UITheme.ACCENT));
        summary.add(badge("Percentage", String.format("%.1f%%", result.getPercentage()),  UITheme.WARNING));
        summary.add(badge("Result",     result.isPassed() ? "PASSED" : "FAILED",
            result.isPassed() ? UITheme.SUCCESS : UITheme.ERROR));
        dlg.add(summary, BorderLayout.NORTH);

        // Per-question breakdown
        List<StudentAnswer> studentAnswers = resultService.getAnswersByResult(result.getId());
        List<Question> examQuestions       = examService.getQuestionsForExam(result.getExamId());
        Map<Integer, StudentAnswer> ansMap = studentAnswers.stream()
            .collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a));

        DefaultTableModel dm = new DefaultTableModel(
            new String[]{"#", "Question", "Your Answer", "Correct", "Status", "Marks"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable dtable = new JTable(dm);
        UITheme.styleTable(dtable);
        dtable.getColumnModel().getColumn(1).setPreferredWidth(300);

        dtable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                if ("Correct".equals(v)) setForeground(UITheme.SUCCESS);
                else if ("Wrong".equals(v)) setForeground(UITheme.ERROR);
                else setForeground(UITheme.TEXT_MUTED);
                if (!sel) setBackground(UITheme.BG_MID);
                return this;
            }
        });

        int i = 1;
        for (Question q : examQuestions) {
            StudentAnswer sa = ansMap.get(q.getId());
            String selected = sa != null && sa.getSelectedOption() != null ? sa.getSelectedOption() : "—";
            boolean correct = sa != null && sa.isCorrect();
            String status   = sa == null || sa.getSelectedOption() == null ? "Skipped"
                            : (correct ? "Correct" : "Wrong");
            String yourAns  = selected.equals("—") ? "—" : selected + ". " + q.getOptionByLetter(selected);
            String corrAns  = q.getCorrectOption() + ". " + q.getOptionByLetter(q.getCorrectOption());
            dm.addRow(new Object[]{
                i++,
                shorten(q.getQuestionText(), 50),
                yourAns.length() > 40 ? yourAns.substring(0, 37) + "..." : yourAns,
                corrAns.length() > 40 ? corrAns.substring(0, 37) + "..." : corrAns,
                status,
                correct ? "+" + q.getMarks() : "0"
            });
        }

        dlg.add(UITheme.scrollPane(dtable), BorderLayout.CENTER);
        JButton close = UITheme.primaryButton("Close");
        close.addActionListener(e -> dlg.dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.BG_DARK);
        south.add(close);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private JPanel badge(String label, String value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBackground(UITheme.BG_MID);
        JLabel v = UITheme.label(value, UITheme.FONT_HEADER, color);
        JLabel l = UITheme.label(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        p.add(v, BorderLayout.CENTER);
        p.add(l, BorderLayout.SOUTH);
        return p;
    }

    private String shorten(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }
}
