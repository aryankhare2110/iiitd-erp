package edu.univ.erp.ui.student;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.BaseFrame;
import edu.univ.erp.ui.common.UIUtils;
import edu.univ.erp.ui.student.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class StudentUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private JButton selectedButton = null;

    private DashboardPanel dashboardPanel;
    private BrowseCoursesPanel browseCoursesPanel;
    private EnrollmentPanel enrollmentPanel;
    private TimetablePanel timetablePanel;
    private GradesPanel gradesPanel;

    public StudentUI() {
        super("IIITD ERP – Student Dashboard");
        setLayout(new BorderLayout());

        sidebar = UIUtils.createSidebar();

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(StudentUI.class.getResource("/Images/logo.png")));
        Image scaled = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
        sidebar.add(UIUtils.sidebarLogoPanel(new ImageIcon(scaled)));
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnDashboard = UIUtils.sidebarButton("Dashboard");
        JButton btnCourses = UIUtils.sidebarButton("Browse Courses");
        JButton btnEnroll = UIUtils.sidebarButton("My Enrollments");
        JButton btnTimetable = UIUtils.sidebarButton("Timetable");
        JButton btnGrades = UIUtils.sidebarButton("Grades");
        JButton btnLogout = UIUtils.sidebarButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCourses);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnEnroll);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnTimetable);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnGrades);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new BrowseCoursesPanel(), "courses");
        contentPanel.add(new EnrollmentPanel(), "enrollments");
        contentPanel.add(new TimetablePanel(), "timetable");
        contentPanel.add(new GradesPanel(), "grades");

        add(contentPanel, BorderLayout.CENTER);

        btnDashboard.addActionListener(e -> switchPanel(btnDashboard, "dashboard"));
        btnCourses.addActionListener(e -> switchPanel(btnCourses, "courses"));
        btnEnroll.addActionListener(e -> switchPanel(btnEnroll, "enrollments"));
        btnTimetable.addActionListener(e -> switchPanel(btnTimetable, "timetable"));
        btnGrades.addActionListener(e -> switchPanel(btnGrades, "grades"));

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        switchPanel(btnDashboard, "dashboard");
        setVisible(true);
    }

    private void switchPanel(JButton button, String panelName) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setForeground(new Color(180, 190, 210));
            }
        }

        button. setForeground(new Color(13, 110, 253));
        switch (panelName) {
            case "dashboard":
                dashboardPanel.refresh();
                break;
            case "courses":
                browseCoursesPanel.refresh();
                break;
            case "enrollments":
                enrollmentPanel.refresh();
                break;
            case "timetable":
                //timetablePanel.refresh();
                break;
            case "grades":
                //gradesPanel.refresh();
                break;
        }
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(StudentUI::new);
    }
}