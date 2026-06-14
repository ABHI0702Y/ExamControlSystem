package com.examcontrol.ui.admin;

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

public class QuestionManagementPanel extends JPanel {

    private final ExamService examService = new ExamService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Question> questions;

    public QuestionManagementPanel() {
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Subject", "Question (preview)", "Correct", "Marks", "Difficulty"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(380);

        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        header.add(UITheme.label("Question Bank", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY), BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_DARK);
        JButton refresh = UITheme.secondaryButton("Refresh");
        JButton add     = UITheme.primaryButton("+ Add Question");
        JButton edit    = UITheme.secondaryButton("Edit");
        JButton del     = UITheme.dangerButton("Delete");
        actions.add(refresh); actions.add(add); actions.add(edit); actions.add(del);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> loadData());
        add.addActionListener(e -> showQuestionDialog(null));
        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select a question first."); return; }
            showQuestionDialog(questions.get(row));
        });
        del.addActionListener(e -> deleteSelected());
    }

    private void loadData() {
        questions = examService.getAllQuestions();
        tableModel.setRowCount(0);
        for (Question q : questions) {
            String preview = q.getQuestionText();
            if (preview.length() > 60) preview = preview.substring(0, 57) + "...";
            tableModel.addRow(new Object[]{
                q.getId(), q.getSubject(), preview, q.getCorrectOption(), q.getMarks(), q.getDifficulty()
            });
        }
    }

    private void showQuestionDialog(Question existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            isEdit ? "Edit Question" : "Add Question", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(580, 620);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setBackground(UITheme.BG_DARK);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(16, 20, 8, 20));

        JTextArea questionTxt = UITheme.textArea(4, 40);
        JTextField optAF = UITheme.textField(), optBF = UITheme.textField();
        JTextField optCF = UITheme.textField(), optDF = UITheme.textField();
        JComboBox<String> correctF  = UITheme.comboBox("A", "B", "C", "D");
        JTextField subjectF         = UITheme.textField();
        JComboBox<String> diffF     = UITheme.comboBox("EASY", "MEDIUM", "HARD");
        JSpinner marksF             = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        styleSpinner(marksF);

        if (isEdit) {
            questionTxt.setText(existing.getQuestionText());
            optAF.setText(existing.getOptionA()); optBF.setText(existing.getOptionB());
            optCF.setText(existing.getOptionC()); optDF.setText(existing.getOptionD());
            correctF.setSelectedItem(existing.getCorrectOption());
            subjectF.setText(existing.getSubject());
            diffF.setSelectedItem(existing.getDifficulty());
            marksF.setValue(existing.getMarks());
        }

        addSection(form, "Question Text", new JScrollPane(questionTxt));
        addSection(form, "Option A",      optAF);
        addSection(form, "Option B",      optBF);
        addSection(form, "Option C",      optCF);
        addSection(form, "Option D",      optDF);

        JPanel row1 = rowPanel("Correct Answer", correctF, "Subject", subjectF);
        JPanel row2 = rowPanel("Difficulty", diffF, "Marks", marksF);
        form.add(row1); form.add(Box.createVerticalStrut(8));
        form.add(row2);

        JLabel errLbl = UITheme.label("", UITheme.FONT_SMALL, UITheme.ERROR);
        JButton save  = UITheme.primaryButton(isEdit ? "Save Changes" : "Add Question");
        save.addActionListener(e -> {
            Question q = isEdit ? existing : new Question();
            q.setQuestionText(questionTxt.getText().trim());
            q.setOptionA(optAF.getText().trim()); q.setOptionB(optBF.getText().trim());
            q.setOptionC(optCF.getText().trim()); q.setOptionD(optDF.getText().trim());
            q.setCorrectOption((String) correctF.getSelectedItem());
            q.setSubject(subjectF.getText().trim());
            q.setDifficulty((String) diffF.getSelectedItem());
            q.setMarks((Integer) marksF.getValue());
            if (!isEdit) q.setCreatedBy(AuthService.getInstance().getCurrentUser().getId());

            ServiceResult r = isEdit ? examService.updateQuestion(q) : examService.createQuestion(q);
            if (r.success()) { dlg.dispose(); loadData(); }
            else errLbl.setText(r.message());
        });

        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setBackground(UITheme.BG_DARK);
        south.setBorder(new EmptyBorder(8, 20, 16, 20));
        south.add(errLbl, BorderLayout.NORTH);
        south.add(save,   BorderLayout.SOUTH);

        dlg.add(new JScrollPane(form) {{ setBorder(null); getViewport().setBackground(UITheme.BG_DARK); }}, BorderLayout.CENTER);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Select a question first."); return; }
        Question q = questions.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete this question? It will be removed from all exams.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            ServiceResult r = examService.deleteQuestion(q.getId());
            if (r.success()) loadData(); else showError(r.message());
        }
    }

    private void addSection(JPanel form, String label, Component field) {
        form.add(UITheme.label(label, UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        form.add(Box.createVerticalStrut(4));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 10));
        form.add(field);
        form.add(Box.createVerticalStrut(10));
    }

    private JPanel rowPanel(String l1, Component f1, String l2, Component f2) {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(UITheme.BG_DARK);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(UITheme.label(l1, UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        p.add(f1);
        p.add(UITheme.label(l2, UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        p.add(f2);
        return p;
    }

    private void styleSpinner(JSpinner s) {
        s.setBackground(UITheme.BG_CARD);
        s.setForeground(UITheme.TEXT_PRIMARY);
        ((JSpinner.DefaultEditor) s.getEditor()).getTextField().setBackground(UITheme.BG_CARD);
        ((JSpinner.DefaultEditor) s.getEditor()).getTextField().setForeground(UITheme.TEXT_PRIMARY);
    }

    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
}
