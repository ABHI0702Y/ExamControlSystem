package com.examcontrol.ui.teacher;

import com.examcontrol.model.Exam;
import com.examcontrol.model.Result;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.ResultService;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultsViewPanel extends JPanel {

    private final ResultService resultService = new ResultService();
    private final ExamService   examService   = new ExamService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final boolean showAll;

    public ResultsViewPanel(int unusedExamId, boolean showAll) {
        this.showAll = showAll;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(
            new String[]{"#", "Student", "Exam", "Score", "Total", "Percentage", "Passed", "Submitted"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);

        buildUI();
        loadData(0);
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.add(UITheme.label("Results", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY), BorderLayout.WEST);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setBackground(UITheme.BG_DARK);
        JButton refresh = UITheme.secondaryButton("Refresh");
        top.add(refresh);

        if (!showAll) {
            JLabel filterLbl = UITheme.label("Filter by Exam:", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
            JComboBox<Exam> examCombo = new JComboBox<>();
            examCombo.addItem(null);  // All
            examService.getAllExams().forEach(examCombo::addItem);
            examCombo.setBackground(UITheme.BG_CARD); examCombo.setForeground(UITheme.TEXT_PRIMARY);
            examCombo.setPreferredSize(new Dimension(200, 30));
            examCombo.addActionListener(e -> {
                Exam sel = (Exam) examCombo.getSelectedItem();
                loadData(sel == null ? 0 : sel.getId());
            });
            top.add(filterLbl);
            top.add(examCombo);
        }

        top.add(refresh);
        header.add(top, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadData(0));
    }

    private void loadData(int examId) {
        List<Result> results = examId > 0
            ? resultService.getResultsByExam(examId)
            : resultService.getAllResults();
        tableModel.setRowCount(0);
        int i = 1;
        for (Result r : results) {
            tableModel.addRow(new Object[]{
                i++, r.getStudentName(), r.getExamTitle(),
                r.getScore(), r.getTotalMarks(),
                String.format("%.1f%%", r.getPercentage()),
                r.isPassed() ? "PASS" : "FAIL",
                r.getSubmittedAt() != null ? r.getSubmittedAt().toString().substring(0, 16) : ""
            });
        }
    }
}
