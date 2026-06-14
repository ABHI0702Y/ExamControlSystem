package com.examcontrol.ui.admin;

import com.examcontrol.model.Role;
import com.examcontrol.model.User;
import com.examcontrol.service.AuthService;
import com.examcontrol.service.UserService;
import com.examcontrol.service.UserService.ServiceResult;
import com.examcontrol.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserService userService = new UserService();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<User> users;

    public UserManagementPanel() {
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Full Name", "Email", "Role", "Active"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        buildUI();
        loadData();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = UITheme.label("User Management", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(UITheme.BG_DARK);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        JButton addBtn     = UITheme.primaryButton("+ Add User");
        JButton editBtn    = UITheme.secondaryButton("Edit");
        JButton resetPwBtn = UITheme.secondaryButton("Reset Pwd");
        JButton deleteBtn  = UITheme.dangerButton("Delete");

        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(resetPwBtn);
        actions.add(deleteBtn);
        header.add(actions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        add(UITheme.scrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        resetPwBtn.addActionListener(e -> showResetPasswordDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
    }

    private void loadData() {
        users = userService.getAllUsers();
        tableModel.setRowCount(0);
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(),
                u.getEmail(), u.getRole(), u.isActive() ? "Yes" : "No"
            });
        }
    }

    private void showAddDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Add User", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(400, 440);
        dlg.setLocationRelativeTo(this);

        JPanel p = formPanel();
        JTextField usernameF  = UITheme.textField();
        JPasswordField passF  = UITheme.passwordField();
        JTextField fullNameF  = UITheme.textField();
        JTextField emailF     = UITheme.textField();
        JComboBox<Role> roleF = new JComboBox<>(Role.values());
        roleF.setBackground(UITheme.BG_CARD); roleF.setForeground(UITheme.TEXT_PRIMARY);

        addFormRow(p, "Username",  usernameF);
        addFormRow(p, "Password",  passF);
        addFormRow(p, "Full Name", fullNameF);
        addFormRow(p, "Email",     emailF);
        addFormRow(p, "Role",      roleF);

        JLabel errLbl = UITheme.label("", UITheme.FONT_SMALL, UITheme.ERROR);

        JButton save = UITheme.primaryButton("Create User");
        save.addActionListener(e -> {
            ServiceResult r = userService.createUser(
                usernameF.getText().trim(),
                new String(passF.getPassword()),
                fullNameF.getText().trim(),
                emailF.getText().trim(),
                (Role) roleF.getSelectedItem());
            if (r.success()) { dlg.dispose(); loadData(); showInfo(r.message()); }
            else errLbl.setText(r.message());
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG_DARK);
        south.add(errLbl, BorderLayout.NORTH);
        south.add(save,   BorderLayout.SOUTH);
        south.setBorder(new EmptyBorder(8, 20, 16, 20));

        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Select a user first."); return; }
        User user = users.get(row);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit User", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(400, 380);
        dlg.setLocationRelativeTo(this);

        JPanel p = formPanel();
        JTextField fullNameF = UITheme.textField(); fullNameF.setText(user.getFullName());
        JTextField emailF    = UITheme.textField(); emailF.setText(user.getEmail());
        JComboBox<Role> roleF = new JComboBox<>(Role.values()); roleF.setSelectedItem(user.getRole());
        roleF.setBackground(UITheme.BG_CARD); roleF.setForeground(UITheme.TEXT_PRIMARY);
        JCheckBox activeBox = new JCheckBox("Active", user.isActive());
        activeBox.setBackground(UITheme.BG_DARK); activeBox.setForeground(UITheme.TEXT_PRIMARY);

        addFormRow(p, "Full Name", fullNameF);
        addFormRow(p, "Email",     emailF);
        addFormRow(p, "Role",      roleF);
        addFormRow(p, "",          activeBox);

        JLabel errLbl = UITheme.label("", UITheme.FONT_SMALL, UITheme.ERROR);
        JButton save = UITheme.primaryButton("Save Changes");
        save.addActionListener(e -> {
            user.setFullName(fullNameF.getText().trim());
            user.setEmail(emailF.getText().trim());
            user.setRole((Role) roleF.getSelectedItem());
            user.setActive(activeBox.isSelected());
            ServiceResult r = userService.updateUser(user);
            if (r.success()) { dlg.dispose(); loadData(); showInfo(r.message()); }
            else errLbl.setText(r.message());
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG_DARK);
        south.add(errLbl, BorderLayout.NORTH);
        south.add(save,   BorderLayout.SOUTH);
        south.setBorder(new EmptyBorder(8, 20, 16, 20));

        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(UITheme.BG_DARK);
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void showResetPasswordDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Select a user first."); return; }
        User user = users.get(row);

        JPasswordField newPassF = UITheme.passwordField();
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UITheme.BG_DARK);
        p.add(UITheme.label("New Password:", UITheme.FONT_BODY, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        p.add(newPassF, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, p,
            "Reset Password for " + user.getUsername(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            ServiceResult r = userService.resetPassword(user.getId(), new String(newPassF.getPassword()));
            if (r.success()) showInfo(r.message()); else showError(r.message());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showWarn("Select a user first."); return; }
        User user = users.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete user \"" + user.getUsername() + "\"? This cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            ServiceResult r = userService.deleteUser(user.getId(), AuthService.getInstance().getCurrentUser().getId());
            if (r.success()) { loadData(); showInfo(r.message()); } else showError(r.message());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 20, 8, 20));
        return p;
    }

    private void addFormRow(JPanel p, String label, Component field) {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(4, 0, 4, 0);
        int row = p.getComponentCount() / 2;
        g.gridy = row; g.gridx = 0; g.weightx = 0;
        p.add(UITheme.label(label, UITheme.FONT_BODY, UITheme.TEXT_MUTED), g);
        g.gridx = 1; g.weightx = 1; g.insets = new Insets(4, 10, 4, 0);
        p.add(field, g);
    }

    private void showInfo(String msg)  { JOptionPane.showMessageDialog(this, msg, "Info",    JOptionPane.INFORMATION_MESSAGE); }
    private void showWarn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
}
