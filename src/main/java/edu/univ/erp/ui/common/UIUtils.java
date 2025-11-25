package edu.univ.erp.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public final class UIUtils {

    public static JPanel createHeader(String titleText, String subtitleText) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        return header;
    }

    public static JButton primaryButton(String text, ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
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
        b.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
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
        t.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        t.setBackground(Color.WHITE);
        t.setAutoCreateRowSorter(true);
        t.setGridColor(new Color(230, 230, 230));

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Helvetica Neue", Font.BOLD, 13));
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
        label.setFont(new Font("Helvetica Neue", bold ? Font.BOLD : Font.PLAIN, 14));
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
        b.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
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

    public static JPanel createHeaderWithBadge(String titleText, String subtitleText, boolean showBadge, String badgeText) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(248, 249, 250));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(8));

        JLabel subtitle = new JLabel(subtitleText);
        subtitle.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitle);

        header.add(leftPanel, BorderLayout.WEST);

        if (showBadge) {
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightPanel.setBackground(new Color(248, 249, 250));
            rightPanel.add(createWarningBadge(badgeText));
            header.add(rightPanel, BorderLayout.EAST);
        }

        return header;
    }

    public static JLabel createWarningBadge(String text) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Helvetica Neue", Font.BOLD, 11));
        badge.setForeground(new Color(133, 100, 4));
        badge.setBackground(new Color(255, 243, 205));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return badge;
    }

    public static JPanel createStatCard(String title, String count, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 20, 25, 20)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(4, 100));
        accentBar.setBackground(accentColor);
        card.add(accentBar, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 36));
        countLabel.setForeground(new Color(33, 37, 41));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(countLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static JPanel createInfoCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    public static JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(makeLabel(label, false));
        row.add(makeLabel(" " + value, true));
        return row;
    }


}