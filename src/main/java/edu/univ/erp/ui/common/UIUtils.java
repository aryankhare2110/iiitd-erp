package edu.univ.erp.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;

public final class UIUtils {

    public static JPanel createHeader(String titleText, String subtitleText) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        return header;
    }

    public static JButton primaryButton(String text, ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(13, 110, 253));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (onClick != null) b.addActionListener(onClick);
        return b;
    }

    public static JButton secondaryButton(String text, ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBackground(new Color(248, 249, 250));
        b.setForeground(new Color(73, 80, 87));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(9, 19, 9, 19)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (onClick != null) b.addActionListener(onClick);
        return b;
    }

    public static JTable createStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(32);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setBackground(Color.WHITE);
        t.setAutoCreateRowSorter(true);
        t.setGridColor(new Color(230, 230, 230));

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(new Color(248, 249, 250));
        h.setForeground(new Color(33, 37, 41));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(222, 226, 230)));

        t.setSelectionBackground(new Color(220, 235, 255));
        t.setSelectionForeground(new Color(33, 37, 41));

        return t;
    }

    public static JPanel createButtonRow(JButton... buttons) {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bottom.setBackground(new Color(248, 249, 250));
        bottom.setBorder(new EmptyBorder(0, 50, 40, 50));
        for (JButton b : buttons) bottom.add(b);
        return bottom;
    }

    public static JLabel makeLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(bold ? new Color(33, 37, 41) : new Color(73, 80, 87));
        return label;
    }

    public static JButton sidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(220, 42));
        b.setBackground(new Color(37, 47, 63));
        b.setForeground(new Color(180, 190, 210));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setContentAreaFilled(false);
        return b;
    }

    public static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(37, 47, 63));
        return sidebar;
    }

    public static JPanel sidebarLogoPanel(ImageIcon icon) {
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(37, 47, 63));
        logoPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel logoLabel = new JLabel(icon);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoLabel);
        return logoPanel;
    }

    public static void setSidebarSelected(JButton oldBtn, JButton newBtn) {
        if (oldBtn != null) {
            oldBtn.setBackground(new Color(37, 47, 63));
            oldBtn.setForeground(new Color(180, 190, 210));
        }
        newBtn.setBackground(new Color(13, 110, 253));
        newBtn.setForeground(Color.WHITE);
    }

}