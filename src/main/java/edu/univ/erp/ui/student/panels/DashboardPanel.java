package edu.univ.erp.ui.student.panels;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.domain.Notification;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final StudentService studentService;
    private final AuthService authService;
    private Student student;
    private JLabel enrolledCountLabel;
    private JLabel totalCreditsLabel;

    public DashboardPanel() {
        studentService = new StudentService();
        authService = new AuthService();
        student = studentService.getMyProfile();

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeaderWithBadge("Student Dashboard", "Welcome to IIITD ERP System",
                studentService.isMaintenanceMode(), " ⚠ MAINTENANCE MODE "), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(30, 50, 40, 50));

        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 25, 0));
        cardsRow. setBackground(new Color(248, 249, 250));
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        int enrolledCount = getValueOrZero(() -> studentService.getEnrolledCoursesCount());
        int totalCredits = getValueOrZero(() -> studentService.getTotalCredits());

        JPanel enrolledCard = UIUtils.createStatCard("Courses Enrolled", String.valueOf(enrolledCount), new Color(13, 110, 253));
        JPanel creditsCard = UIUtils.createStatCard("Total Credits", String.valueOf(totalCredits), new Color(111, 66, 193));

        enrolledCountLabel = findCountLabel(enrolledCard);
        totalCreditsLabel = findCountLabel(creditsCard);

        cardsRow.add(enrolledCard);
        cardsRow.add(creditsCard);
        center.add(cardsRow);
        center.add(Box.createVerticalStrut(35));

        JPanel twoCol = new JPanel(new GridLayout(1, 2, 30, 0));
        twoCol.setBackground(new Color(248, 249, 250));
        twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        twoCol.add(createProfileCard());
        twoCol.add(createNotificationPreview());
        center.add(twoCol);

        add(center, BorderLayout.CENTER);
    }

    private int getValueOrZero(IntSupplier supplier) {
        try { return supplier.getAsInt(); } catch (Exception e) { return 0; }
    }

    @FunctionalInterface
    interface IntSupplier { int getAsInt(); }

    private JPanel createProfileCard() {
        JPanel card = UIUtils.createInfoCard("Profile");

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        if (student != null) {
            content.add(UIUtils.createInfoRow("Name:", student.getFullName()));
            content.add(Box.createVerticalStrut(12));
            content.add(UIUtils.createInfoRow("Roll No:", student.getRollNo()));
            content.add(Box.createVerticalStrut(12));
            content.add(UIUtils.createInfoRow("Program:", student.getDegreeLevel() + " - " + student.getBranch()));
            content.add(Box.createVerticalStrut(12));
            content.add(UIUtils.createInfoRow("Email:", UserSession.getUserEmail()));
            content.add(Box.createVerticalStrut(12));
            content.add(UIUtils.createInfoRow("Current:", "Year " + student.getYear() + ", " + student.getTerm() + " Term"));
        }

        card.add(content, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(UIUtils.secondaryButton("Change Password", e -> openPasswordDialog()));
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createNotificationPreview() {
        JPanel card = UIUtils.createInfoCard("Recent Notifications");
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        List<Notification> notifications = getNotifications();

        if (notifications.isEmpty()) {
            JLabel noNotif = new JLabel("No recent notifications");
            noNotif.setFont(new Font("Helvetica Neue", Font.ITALIC, 16));
            noNotif.setForeground(new Color(108, 117, 125));
            listPanel.add(noNotif);
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                listPanel.add(createNotificationItem(notifications.get(i)));
                if (i < notifications.size() - 1) {
                    listPanel.add(Box.createVerticalStrut(12));
                }
            }
        }

        listPanel.add(Box.createVerticalGlue());
        card.add(listPanel, BorderLayout.CENTER);
        return card;
    }

    private List<Notification> getNotifications() {
        try {
            List<Notification> notifs = studentService.getRecentNotifications(3);
            return notifs != null ? notifs : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private JPanel createNotificationItem(Notification notif) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel bullet = new JLabel("•");
        bullet.setFont(new Font("Helvetica Neue", Font.PLAIN, 18));
        bullet.setForeground(new Color(13, 110, 253));

        String msg = notif.getMessage();
        if (msg.length() > 60) msg = msg.substring(0, 60) + "...";

        JLabel text = new JLabel(msg);
        text.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        text.setForeground(new Color(73, 80, 87));

        item.add(bullet, BorderLayout.WEST);
        item.add(text, BorderLayout.CENTER);
        return item;
    }

    private void openPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPasswordField oldPF = new JPasswordField();
        JPasswordField newPF = new JPasswordField();
        JPasswordField confirmPF = new JPasswordField();

        panel.add(new JLabel("Current Password:")); panel.add(oldPF);
        panel.add(new JLabel("New Password:")); panel.add(newPF);
        panel.add(new JLabel("Confirm New Password:")); panel.add(confirmPF);

        if (JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String oldPass = new String(oldPF.getPassword()).trim();
        String newPass = new String(newPF.getPassword()).trim();
        String confirm = new String(confirmPF.getPassword()).trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            DialogUtils.errorDialog("All fields are required.");
            return;
        }

        if (authService.login(UserSession.getUserEmail(), oldPass) == null) {
            DialogUtils.errorDialog("Current password is incorrect.");
            return;
        }

        if (!newPass.equals(confirm)) {
            DialogUtils.errorDialog("New passwords do not match.");
            return;
        }

        if (newPass.length() < 6) {
            DialogUtils.errorDialog("Password must be at least 6 characters long.");
            return;
        }

        boolean ok = authService.resetPassword(UserSession.getUserEmail(), newPass);
        DialogUtils.infoDialog(ok ? "Password changed successfully!" : "Failed to update password.");
    }

    private JLabel findCountLabel(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        if (label.getFont().getSize() >= 36) {
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void refresh() {
        if (enrolledCountLabel != null) {
            int enrolledCount = getValueOrZero(() -> studentService.getEnrolledCoursesCount());
            enrolledCountLabel.setText(String.valueOf(enrolledCount));
        }
        if (totalCreditsLabel != null) {
            int totalCredits = getValueOrZero(() -> studentService.getTotalCredits());
            totalCreditsLabel.setText(String.valueOf(totalCredits));
        }
        student = studentService.getMyProfile();

        revalidate();
        repaint();
    }
}