package edu.univ.erp.ui.instructor.panels;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.service.FacultyService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardPanel extends JPanel {

    private final FacultyService facultyService;

    public DashboardPanel(FacultyService facultyService) {
        this.facultyService = facultyService;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        //Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel("Faculty Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Overview of the IIITD ERP Instructor");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 40, 50));

        //Stats
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsRow.setBackground(new Color(248, 249, 250));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel studentCard = statCard("Total Students", String.valueOf(adminService.getStudentCount()), new Color(13, 110, 253));
        JPanel facultyCard = statCard("Total Faculty", String.valueOf(adminService.getFacultyCount()), new Color(111, 66, 193));
        JPanel courseCard = statCard("Total Courses", String.valueOf(adminService.getCourseCount()), new Color(253, 126, 20));

        cardsRow.add(studentCard);
        cardsRow.add(facultyCard);
        cardsRow.add(courseCard);

        center.add(cardsRow);
        center.add(Box.createVerticalStrut(30));

        //System Status
        JPanel statusSection = new JPanel();
        statusSection.setLayout(new BoxLayout(statusSection, BoxLayout.Y_AXIS));
        statusSection.setBackground(Color.WHITE);
        statusSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(25, 25, 25, 25)
        ));
        statusSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusTitle = new JLabel("System Status");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusTitle.setForeground(new Color(33, 37, 41));
        statusTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusSection.add(statusTitle);
        statusSection.add(Box.createVerticalStrut(20));

        //Logged-in-as
        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setBackground(Color.WHITE);

        JLabel loggedLabel = new JLabel("Logged in as:");
        loggedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loggedLabel.setForeground(new Color(73, 80, 87));

        JLabel loggedUser = new JLabel(" " + UserSession.getUserEmail());
        loggedUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loggedUser.setForeground(new Color(33, 37, 41));

        loginRow.add(loggedLabel);
        loginRow.add(loggedUser);

        statusSection.add(loginRow);
        statusSection.add(Box.createVerticalStrut(20));

        //Maintenance Mode
        boolean isOn = adminService.isMaintenanceMode();

        JPanel maintenanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        maintenanceRow.setBackground(Color.WHITE);

        JLabel maintenanceText = new JLabel("Maintenance Mode:");
        maintenanceText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceText.setForeground(new Color(73, 80, 87));

        JLabel statusBadge = new JLabel(isOn ? "  ON  " : "  OFF  ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(isOn ? new Color(40, 167, 69) : new Color(220, 53, 69));
        statusBadge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        maintenanceRow.add(maintenanceText);
        maintenanceRow.add(Box.createHorizontalStrut(10));
        maintenanceRow.add(statusBadge);

        statusSection.add(maintenanceRow);
        statusSection.add(Box.createVerticalStrut(20));

        //Logged in since
        JPanel timestampRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timestampRow.setBackground(Color.WHITE);

        JLabel timestampLabel = new JLabel("Logged in since:");
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timestampLabel.setForeground(new Color(73, 80, 87));

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JLabel timestampValue = new JLabel(" " + now.format(formatter));
        timestampValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timestampValue.setForeground(new Color(108, 117, 125));

        timestampRow.add(timestampLabel);
        timestampRow.add(timestampValue);
        statusSection.add(timestampRow);
        center.add(statusSection);

        add(center, BorderLayout.CENTER);
    }

    private JPanel statCard(String title, String count, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

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