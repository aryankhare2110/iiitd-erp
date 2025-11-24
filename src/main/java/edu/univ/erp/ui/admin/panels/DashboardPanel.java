package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardPanel extends JPanel {

    private AdminService adminService;
    public DashboardPanel() {

        adminService = new AdminService();

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));

        JLabel subtitle = new JLabel("Overview of the IIITD ERP System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 40, 50));

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsRow.setBackground(new Color(248, 249, 250));

        cardsRow.add(statCard("Total Students", String.valueOf(adminService.getStudentCount()), new Color(13, 110, 253)));
        cardsRow.add(statCard("Total Faculty", String.valueOf(adminService.getFacultyCount()), new Color(111, 66, 193)));
        cardsRow.add(statCard("Total Courses", String.valueOf(adminService.getCourseCount()), new Color(253, 126, 20)));

        center.add(cardsRow);
        center.add(Box.createVerticalStrut(30));

        JPanel statusSection = new JPanel();
        statusSection.setLayout(new BoxLayout(statusSection, BoxLayout.Y_AXIS));
        statusSection.setBackground(Color.WHITE);
        statusSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel statusTitle = new JLabel("System Status");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusTitle.setForeground(new Color(33, 37, 41));
        statusTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusSection.add(statusTitle);
        statusSection.add(Box.createVerticalStrut(20));

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setBackground(Color.WHITE);
        loginRow.add(makeLabel("Logged in as:", false));
        loginRow.add(makeLabel(" " + UserSession.getUserEmail(), true));
        statusSection.add(loginRow);
        statusSection.add(Box.createVerticalStrut(20));

        boolean isOn = adminService.isMaintenanceMode();
        final JLabel statusBadge = makeStatusBadge(isOn);

        final JCheckBox toggle = new JCheckBox();
        toggle.setSelected(isOn);
        toggle.setOpaque(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggle.setToolTipText("Toggle maintenance mode");

        toggle.addActionListener(e -> {
            boolean newState = toggle.isSelected();

            String msg;

            if (newState) {
                msg = "Enable maintenance mode?\nThis will restrict student & faculty access.";
            } else {
                msg = "Disable maintenance mode?";
            }

            int choice = JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice != JOptionPane.YES_OPTION) {
                toggle.setSelected(!newState);
                return;
            }

            boolean ok = adminService.setMaintenanceMode(newState);
            if (!ok) {
                toggle.setSelected(!newState);
                DialogUtils.errorDialog("Failed to update maintenance mode.");
                return;
            }

            updateBadge(statusBadge, newState);
            DialogUtils.infoDialog("Maintenance mode is now " + (newState ? "ON" : "OFF") + ".");
        });

        JPanel maintenanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        maintenanceRow.setBackground(Color.WHITE);

        maintenanceRow.add(makeLabel("Maintenance Mode:", false));
        maintenanceRow.add(Box.createHorizontalStrut(10));
        maintenanceRow.add(toggle);
        maintenanceRow.add(Box.createHorizontalStrut(10));
        maintenanceRow.add(statusBadge);

        statusSection.add(maintenanceRow);
        statusSection.add(Box.createVerticalStrut(20));

        JPanel timestampRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timestampRow.setBackground(Color.WHITE);
        timestampRow.add(makeLabel("Logged in since:", false));
        timestampRow.add(makeLabel(" " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), false));

        statusSection.add(timestampRow);

        center.add(statusSection);
        add(center, BorderLayout.CENTER);
    }

    private JLabel makeLabel(String text, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(bold ? new Color(33, 37, 41) : new Color(73, 80, 87));
        return label;
    }

    private JLabel makeStatusBadge(boolean on) {
        JLabel badge = new JLabel(on ? "  ON  " : "  OFF  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(on ? new Color(40, 167, 69) : new Color(220, 53, 69));
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return badge;
    }

    private void updateBadge(JLabel badge, boolean on) {
        badge.setText(on ? "  ON  " : "  OFF  ");
        badge.setBackground(on ? new Color(40, 167, 69) : new Color(220, 53, 69));
        badge.repaint();
    }

    private JPanel statCard(String title, String count, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1), new EmptyBorder(20, 20, 20, 20)));

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(5, 100));
        accentBar.setBackground(accentColor);
        card.add(accentBar, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        countLabel.setForeground(new Color(33, 37, 41));

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(countLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }
}