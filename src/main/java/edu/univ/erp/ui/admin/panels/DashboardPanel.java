package edu.univ.erp.ui. admin. panels;

import edu.univ. erp.auth.session.UserSession;
import edu. univ.erp.service.AdminService;
import edu. univ.erp.service. AuthService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java. time.format.DateTimeFormatter;

public class DashboardPanel extends JPanel {

    private final AdminService adminService;
    private final AuthService authService;
    private JLabel studentCountLabel;
    private JLabel facultyCountLabel;
    private JLabel courseCountLabel;

    public DashboardPanel() {
        adminService = new AdminService();
        authService = new AuthService();

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Admin Dashboard", "Overview of the IIITD ERP System"), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout. Y_AXIS));
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(30, 50, 40, 50));

        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 25, 0));
        cardsRow. setBackground(new Color(248, 249, 250));
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel studentCard = statCard("Total Students", String.valueOf(adminService.getStudentCount()), new Color(13, 110, 253));
        JPanel facultyCard = statCard("Total Faculty", String. valueOf(adminService.getFacultyCount()), new Color(111, 66, 193));
        JPanel courseCard = statCard("Total Courses", String.valueOf(adminService.getCourseCount()), new Color(253, 126, 20));

        studentCountLabel = findCountLabel(studentCard);
        facultyCountLabel = findCountLabel(facultyCard);
        courseCountLabel = findCountLabel(courseCard);

        cardsRow. add(studentCard);
        cardsRow.add(facultyCard);
        cardsRow.add(courseCard);
        center. add(cardsRow);
        center.add(Box.createVerticalStrut(35));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 30, 0));
        twoCol.setBackground(new Color(248, 249, 250));
        twoCol. setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        twoCol.add(createStatusSection());

        twoCol.add(createNotificationSection());

        center.add(twoCol);
        add(center, BorderLayout. CENTER);
    }

    private JPanel createStatusSection() {
        JPanel statusSection = new JPanel(new BorderLayout(0, 15));
        statusSection. setBackground(Color.WHITE);
        statusSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel statusContent = new JPanel();
        statusContent.setLayout(new BoxLayout(statusContent, BoxLayout.Y_AXIS));
        statusContent.setBackground(Color.WHITE);

        JLabel statusTitle = new JLabel("System Status");
        statusTitle. setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        statusTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusContent.add(statusTitle);
        statusContent.add(Box.createVerticalStrut(20));

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setBackground(Color.WHITE);
        loginRow. setAlignmentX(Component.LEFT_ALIGNMENT);
        loginRow.add(UIUtils.makeLabel("Logged in as:", false));
        loginRow. add(UIUtils.makeLabel(" " + UserSession.getUserEmail(), true));
        statusContent.add(loginRow);
        statusContent.add(Box. createVerticalStrut(12));

        boolean isOn = adminService.isMaintenanceMode();
        JLabel statusBadge = makeStatusBadge(isOn);

        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(isOn);
        toggle.setOpaque(false);
        toggle. setCursor(new Cursor(Cursor.HAND_CURSOR));

        toggle. addActionListener(e -> {
            boolean newState = toggle.isSelected();
            String msg = newState
                    ?  "Enable maintenance mode?\nThis will restrict student & faculty access."
                    : "Disable maintenance mode?";

            if (JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                toggle.setSelected(!newState);
                return;
            }

            if (adminService.setMaintenanceMode(newState)) {
                updateBadge(statusBadge, newState);
                DialogUtils.successDialog("Maintenance mode is now " + (newState ? "ON" : "OFF"));
            } else {
                toggle.setSelected(!newState);
                DialogUtils.errorDialog("Failed to update maintenance mode.");
            }
        });

        JPanel maintenanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        maintenanceRow. setBackground(Color.WHITE);
        maintenanceRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        maintenanceRow.add(UIUtils.makeLabel("Maintenance Mode:", false));
        maintenanceRow.add(Box.createHorizontalStrut(10));
        maintenanceRow.add(toggle);
        maintenanceRow.add(Box. createHorizontalStrut(10));
        maintenanceRow. add(statusBadge);
        statusContent.add(maintenanceRow);
        statusContent.add(Box. createVerticalStrut(12));

        JPanel timestampRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timestampRow.setBackground(Color. WHITE);
        timestampRow. setAlignmentX(Component. LEFT_ALIGNMENT);
        timestampRow.add(UIUtils. makeLabel("Session started:", false));
        timestampRow.add(UIUtils.makeLabel(" " + LocalDateTime.now(). format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), false));
        statusContent.add(timestampRow);

        statusSection.add(statusContent, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(UIUtils.secondaryButton("Change Password", e -> openPasswordDialog()));
        btnPanel.add(UIUtils.secondaryButton("Set Add/Drop Deadline", e -> openDeadlineDialog()));

        statusSection.add(btnPanel, BorderLayout.SOUTH);

        return statusSection;
    }

    private JPanel createNotificationSection() {
        JPanel notifSection = new JPanel(new BorderLayout(0, 15));
        notifSection.setBackground(Color.WHITE);
        notifSection.setBorder(BorderFactory. createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel notifTitle = new JLabel("Send Notification");
        notifTitle.setFont(new Font("Helvetica Neue", Font.BOLD, 18));
        notifSection.add(notifTitle, BorderLayout.NORTH);

        JTextArea notifArea = new JTextArea(6, 20);
        notifArea.setLineWrap(true);
        notifArea. setWrapStyleWord(true);
        notifArea.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        notifArea. setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JScrollPane scrollPane = new JScrollPane(notifArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        notifSection.add(scrollPane, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sendPanel.setBackground(Color.WHITE);
        sendPanel. add(UIUtils.primaryButton("Send Notification", e -> {
            String message = notifArea.getText().trim();
            if (message.isEmpty()) {
                DialogUtils.errorDialog("Notification cannot be empty.");
                return;
            }

            if (adminService.sendNotification(message, UserSession.getUserEmail())) {
                DialogUtils.successDialog("Notification sent successfully!");
                notifArea.setText("");
            } else {
                DialogUtils.errorDialog("Failed to send notification.");
            }
        }));

        notifSection.add(sendPanel, BorderLayout.SOUTH);

        return notifSection;
    }

    private JPanel statCard(String title, String count, Color accentColor) {
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
        content.setLayout(new BoxLayout(content, BoxLayout. Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Helvetica Neue", Font. BOLD, 48));
        countLabel.setForeground(new Color(33, 37, 41));
        countLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(countLabel);

        card.add(content, BorderLayout. CENTER);
        return card;
    }

    private JLabel makeStatusBadge(boolean on) {
        JLabel badge = new JLabel(on ? "ON" : "OFF");
        badge.setFont(new Font("Helvetica Neue", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge. setBackground(on ? new Color(40, 167, 69) : new Color(220, 53, 69));
        badge.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        badge.setHorizontalAlignment(SwingConstants. CENTER);
        return badge;
    }

    private void updateBadge(JLabel badge, boolean on) {
        badge.setText(on ? "ON" : "OFF");
        badge.setBackground(on ? new Color(40, 167, 69) : new Color(220, 53, 69));
        badge.repaint();
    }

    private void openPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPasswordField oldPF = new JPasswordField();
        JPasswordField newPF = new JPasswordField();
        JPasswordField confirmPF = new JPasswordField();

        panel.add(new JLabel("Old Password:"));
        panel.add(oldPF);
        panel.add(new JLabel("New Password:"));
        panel.add(newPF);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPF);

        if (JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String oldPass = new String(oldPF.getPassword()). trim();
        String newPass = new String(newPF.getPassword()).trim();
        String confirm = new String(confirmPF. getPassword()).trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            DialogUtils.errorDialog("All fields must be filled.");
            return;
        }

        if (authService.login(UserSession.getUserEmail(), oldPass) == null) {
            DialogUtils.errorDialog("Old password is incorrect.");
            return;
        }

        if (! newPass.equals(confirm)) {
            DialogUtils.errorDialog("New passwords do not match.");
            return;
        }

        if (authService.resetPassword(UserSession.getUserEmail(), newPass)) {
            DialogUtils.successDialog("Password changed successfully!");
        } else {
            DialogUtils.errorDialog("Failed to update password.");
        }
    }

    private void openDeadlineDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel. setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField dateField = new JTextField("2025-01-31");
        panel.add(new JLabel("Add/Drop Deadline (YYYY-MM-DD):"));
        panel.add(dateField);

        if (JOptionPane.showConfirmDialog(this, panel, "Set Add/Drop Deadline",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String dateStr = dateField.getText().trim();

        if (! dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            DialogUtils.errorDialog("Please enter date in format YYYY-MM-DD.");
            return;
        }

        try {
            java.time.LocalDate parsed = java.time.LocalDate. parse(dateStr);

            if (adminService.setAddDropDeadline(parsed)) {
                DialogUtils.successDialog("Add/Drop Deadline updated to " + dateStr);
            } else {
                DialogUtils.errorDialog("Failed to update deadline.");
            }
        } catch (Exception ex) {
            DialogUtils.errorDialog("Invalid date format.");
        }
    }

    private JLabel findCountLabel(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        if (label.getFont(). getSize() == 48) {
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }
    public void refresh() {
        if (studentCountLabel != null) {
            studentCountLabel.setText(String. valueOf(adminService.getStudentCount()));
        }
        if (facultyCountLabel != null) {
            facultyCountLabel. setText(String.valueOf(adminService.getFacultyCount()));
        }
        if (courseCountLabel != null) {
            courseCountLabel.setText(String.valueOf(adminService.getCourseCount()));
        }
        revalidate();
        repaint();
    }
}