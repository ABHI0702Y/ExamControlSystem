package com.examcontrol.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {

    // Colour palette
    public static final Color BG_DARK       = new Color(0x1E, 0x1E, 0x2E);
    public static final Color BG_MID        = new Color(0x28, 0x28, 0x3A);
    public static final Color BG_CARD       = new Color(0x31, 0x31, 0x45);
    public static final Color ACCENT        = new Color(0x4F, 0xC3, 0xF7);
    public static final Color ACCENT_HOVER  = new Color(0x81, 0xD4, 0xFA);
    public static final Color TEXT_PRIMARY  = new Color(0xCD, 0xD6, 0xF4);
    public static final Color TEXT_MUTED    = new Color(0x6C, 0x7F, 0xA0);
    public static final Color SUCCESS       = new Color(0xA6, 0xE3, 0xA1);
    public static final Color ERROR         = new Color(0xF3, 0x8B, 0xA8);
    public static final Color WARNING       = new Color(0xFA, 0xB3, 0x87);
    public static final Color BORDER        = new Color(0x45, 0x47, 0x5A);

    // Fonts
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("JetBrains Mono", Font.PLAIN, 12);

    private UITheme() {}

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(BG_DARK);
        btn.setFont(FONT_HEADER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ERROR);
        btn.setForeground(BG_DARK);
        btn.setFont(FONT_HEADER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(6, 14, 6, 14)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField textField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    public static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        styleTextField(pf);
        return pf;
    }

    public static JTextArea textArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setBackground(BG_CARD);
        ta.setForeground(TEXT_PRIMARY);
        ta.setCaretColor(ACCENT);
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }

    public static JComboBox<String> comboBox(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        return cb;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_MID);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(BORDER);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(BG_DARK);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setFont(FONT_HEADER);
        header.setBorder(BorderFactory.createLineBorder(BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static JScrollPane scrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_MID);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        sp.getViewport().setBackground(BG_MID);
        return sp;
    }

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        return sep;
    }

    public static JPanel titledSection(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG_DARK);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), " " + title + " ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                FONT_HEADER, ACCENT),
            new EmptyBorder(8, 8, 8, 8)));
        return p;
    }

    private static void styleTextField(JTextField tf) {
        tf.setBackground(BG_CARD);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
    }
}
