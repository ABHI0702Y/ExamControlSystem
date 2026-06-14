package com.examcontrol.ui.teacher;

import com.examcontrol.model.Exam;
import com.examcontrol.model.Question;
import com.examcontrol.service.AuthService;
import com.examcontrol.service.ExamService;
import com.examcontrol.service.UserService.ServiceResult;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExamManagementPanel extends JPanel {

    private final ExamService examService = new ExamService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Exam> exams;

    public ExamManagementPanel() {
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Title", "Duration(min)", "Questions", "Total Marks", "Passing", "Active"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);

        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.add(UITheme.label("My Exams", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_DARK);
        JButton refresh = UITheme.secondaryButton("Refresh");
        JButton add     = UITheme.primaryButton("+ New Exam");
        JButton edit    = UITheme.secondaryButton("Edit");
        JButton qMgr    = UITheme.secondaryButton("Questions");
        JButton del     = UITheme.dangerButton("Delete");
        actions.add(refresh); actions.add(add); actions.add(edit); actions.add(qMgr); actions.add(del);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadData());
        add.addActionListener(e -> showExamDialog(null));
        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select an exam first."); return; }
            showExamDialog(exams.get(row));
        });
        qMgr.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select an exam first."); return; }
            showQuestionManager(exams.get(row));
        });
        del.addActionListener(e -> deleteSelected());
    }

    private void loadData() {
        exams = examService.getAllExams();
        tableModel.setRowCount(0);
        for (Exam ex : exams) {
            tableModel.addRow(new Object[]{
                ex.getId(), ex.getTitle(), ex.getDurationMinutes(), ex.getQuestionCount(),
                ex.getTotalMarks(), ex.getPassingMarks(), ex.isActive() ? "Yes" : "No"
            });
        }
    }

    private void showExamDialog(Exam existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Exam" : "New Exam", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(460, 400);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_DARK);
        form.setBorder(new EmptyBorder(16, 20, 8, 20));

        JTextField titleF = UITheme.textField();
        JTextArea  descF  = UITheme.textArea(3, 28);
        JSpinner   durF   = spinner(60, 5, 600, 5);
        JSpinner   passF  = spinner(40, 0, 1000, 5);
        JCheckBox  active = new JCheckBox("Active", true);
        active.setBackground(UITheme.BG_DARK); active.setForeground(UITheme.TEXT_PRIMARY);

        if (isEdit) {
            titleF.setText(existing.getTitle()); descF.setText(existing.getDescription());
            durF.setValue(existing.getDurationMinutes()); passF.setValue(existing.getPassingMarks());
            active.setSelected(existing.isActive());
        }

        addRow(form, 0, "Title",         titleF);
        addRow(form, 1, "Description",   new JScrollPane(descF) {{ setPreferredSize(new Dimension(280, 64)); }});
        addRow(form, 2, "Duration(min)", durF);
        addRow(form, 3, "Passing Marks", passF);
        addRow(form, 4, "",              active);

        JLabel errLbl = UITheme.label("", UITheme.FONT_SMALL, UITheme.ERROR);
        JButton save  = UITheme.primaryButton(isEdit ? "Save" : "Create");
        save.addActionListener(e -> {
            Exam ex = isEdit ? existing : new Exam();
            ex.setTitle(titleF.getText().trim()); ex.setDescription(descF.getText().trim());
            ex.setDurationMinutes((Integer) durF.getValue()); ex.setPassingMarks((Integer) passF.getValue());
            ex.setActive(active.isSelected());
            if (!isEdit) ex.setCreatedBy(AuthService.getInstance().getCurrentUser().getId());
            ServiceResult r = isEdit ? examService.updateExam(ex) : examService.createExam(ex);
            if (r.success()) { dlg.dispose(); loadData(); } else errLbl.setText(r.message());
        });

        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setBackground(UITheme.BG_DARK); south.setBorder(new EmptyBorder(8, 20, 16, 20));
        south.add(errLbl, BorderLayout.NORTH); south.add(save, BorderLayout.SOUTH);
        dlg.add(form, BorderLayout.CENTER); dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showQuestionManager(Exam exam) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Questions — " + exam.getTitle(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(900, 560);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.setLayout(new BorderLayout(12, 0));
        dlg.getRootPane().setBorder(new EmptyBorder(16, 16, 16, 16));

        // In-exam list
        javax.swing.table.DefaultTableModel inModel = new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Subject", "Question", "Marks"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable inTable = new JTable(inModel); UITheme.styleTable(inTable);

        // Available (not yet in exam) list
        javax.swing.table.DefaultTableModel outModel = new javax.swing.table.DefaultTableModel(
            new String[]{"ID", "Subject", "Question", "Marks"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable outTable = new JTable(outModel); UITheme.styleTable(outTable);

        Runnable reload = () -> {
            inModel.setRowCount(0);
            examService.getQuestionsForExam(exam.getId()).forEach(q ->
                inModel.addRow(new Object[]{q.getId(), q.getSubject(), shorten(q.getQuestionText(), 50), q.getMarks()}));
            outModel.setRowCount(0);
            examService.getQuestionsNotInExam(exam.getId()).forEach(q ->
                outModel.addRow(new Object[]{q.getId(), q.getSubject(), shorten(q.getQuestionText(), 50), q.getMarks()}));
        };
        reload.run();

        JButton addQ = UITheme.primaryButton("◀ Add to Exam");
        JButton remQ = UITheme.dangerButton("Remove ▶");
        addQ.addActionListener(e -> {
            int row = outTable.getSelectedRow();
            if (row < 0) return;
            examService.addQuestionToExam(exam.getId(), (Integer) outModel.getValueAt(row, 0));
            reload.run(); loadData();
        });
        remQ.addActionListener(e -> {
            int row = inTable.getSelectedRow();
            if (row < 0) return;
            examService.removeQuestionFromExam(exam.getId(), (Integer) inModel.getValueAt(row, 0));
            reload.run(); loadData();
        });

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setBackground(UITheme.BG_DARK);
        btnPanel.add(addQ); btnPanel.add(remQ);

        JPanel left  = sidePanel("In Exam",             UITheme.scrollPane(inTable));
        JPanel mid   = sidePanel("",                    btnPanel);
        JPanel right = sidePanel("Available Questions", UITheme.scrollPane(outTable));
        dlg.add(left, BorderLayout.WEST); dlg.add(mid, BorderLayout.CENTER); dlg.add(right, BorderLayout.EAST);
        dlg.setVisible(true);
    }

    private JPanel sidePanel(String title, Component c) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(UITheme.BG_DARK);
        p.setPreferredSize(new Dimension(380, 0));
        if (!title.isEmpty()) p.add(UITheme.label(title, UITheme.FONT_HEADER, UITheme.ACCENT), BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private String shorten(String s, int max) {
        return s.length() > max ? s.substring(0, max - 3) + "..." : s;
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Select an exam first."); return; }
        Exam ex = exams.get(row);
        int c = JOptionPane.showConfirmDialog(this, "Delete \"" + ex.getTitle() + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            ServiceResult r = examService.deleteExam(ex.getId());
            if (r.success()) loadData(); else showError(r.message());
        }
    }

    private void addRow(JPanel p, int row, String label, Component field) {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 0, 5, 0);
        g.gridy = row; g.gridx = 0; g.weightx = 0;
        p.add(UITheme.label(label, UITheme.FONT_BODY, UITheme.TEXT_MUTED), g);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(5, 10, 5, 0);
        p.add(field, g);
    }

    private JSpinner spinner(int val, int min, int max, int step) {
        JSpinner s = new JSpinner(new SpinnerNumberModel(val, min, max, step));
        ((JSpinner.DefaultEditor) s.getEditor()).getTextField().setBackground(UITheme.BG_CARD);
        ((JSpinner.DefaultEditor) s.getEditor()).getTextField().setForeground(UITheme.TEXT_PRIMARY);
        return s;
    }

    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
}
