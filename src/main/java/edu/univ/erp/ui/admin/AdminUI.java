package edu.univ.erp. ui.admin;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.admin.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class AdminUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;

    private DashboardPanel dashboardPanel;
    private ManageStudentsPanel manageStudentsPanel;
    private ManageFacultyPanel manageFacultyPanel;
    private ManageAdminPanel manageAdminPanel;
    private ManageCoursesPanel manageCoursesPanel;
    private ManageSectionsPanel manageSectionsPanel;
    private BackupRestorePanel backupRestorePanel;

    public AdminUI() {
        super("IIITD ERP – Admin Dashboard");
        setLayout(new BorderLayout());

        sidebar = UIUtils.createSidebar();
        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(AdminUI.class.getResource("/Images/logo.png")));
        Image scaled = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
        sidebar.add(UIUtils. sidebarLogoPanel(new ImageIcon(scaled)));
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnDashboard = UIUtils.sidebarButton("Dashboard");
        JButton btnStudents = UIUtils.sidebarButton("Manage Students");
        JButton btnFaculty = UIUtils. sidebarButton("Manage Faculty");
        JButton btnAdmins = UIUtils.sidebarButton("Manage Admins");
        JButton btnCourses = UIUtils.sidebarButton("Manage Courses");
        JButton btnSections = UIUtils.sidebarButton("Manage Sections");
        JButton btnBackup = UIUtils.sidebarButton("Backup");
        JButton btnLogout = UIUtils.sidebarButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStudents);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnFaculty);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAdmins);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCourses);
        sidebar. add(Box.createVerticalStrut(5));
        sidebar. add(btnSections);
        sidebar. add(Box.createVerticalStrut(5));
        sidebar. add(btnBackup);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout. WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        AdminService adminService = new AdminService();

        dashboardPanel = new DashboardPanel();
        manageStudentsPanel = new ManageStudentsPanel();
        manageFacultyPanel = new ManageFacultyPanel();
        manageAdminPanel = new ManageAdminPanel();
        manageCoursesPanel = new ManageCoursesPanel(adminService);
        manageSectionsPanel = new ManageSectionsPanel();
        backupRestorePanel = new BackupRestorePanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(manageStudentsPanel, "manageStudents");
        contentPanel.add(manageFacultyPanel, "manageFaculty");
        contentPanel.add(manageAdminPanel, "manageAdmins");
        contentPanel.add(manageCoursesPanel, "manageCourses");
        contentPanel.add(manageSectionsPanel, "manageSections");
        contentPanel.add(backupRestorePanel, "backupRestorePanel");

        add(contentPanel, BorderLayout.CENTER);

        btnDashboard.addActionListener(e -> switchPanel(btnDashboard,"dashboard"));
        btnStudents.addActionListener(e -> switchPanel(btnStudents,"manageStudents"));
        btnFaculty.addActionListener(e -> switchPanel(btnFaculty,"manageFaculty"));
        btnAdmins.addActionListener(e -> switchPanel(btnAdmins,"manageAdmins"));
        btnCourses.addActionListener(e -> switchPanel(btnCourses,"manageCourses"));
        btnSections.addActionListener(e -> switchPanel(btnSections,"manageSections"));
        btnBackup.addActionListener(e -> switchPanel(btnBackup,"backup"));

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        switchPanel(btnDashboard, "dashboard");
        setVisible(true);
    }

    private void switchPanel(JButton btn, String panelName) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton button = (JButton) c;
                button.setForeground(new Color(180, 190, 210));
            }
        }

        btn.setForeground(new Color(248, 249, 250));

        switch (panelName) {
            case "dashboard":
                dashboardPanel.refresh();
                break;
            case "manageStudents":
                manageStudentsPanel.refresh();
                break;
            case "manageFaculty":
                manageFacultyPanel.refresh();
                break;
            case "manageAdmins":
                manageAdminPanel.refresh();
                break;
            case "manageCourses":
                manageCoursesPanel.refresh();
                break;
            case "manageSections":
                manageSectionsPanel.refresh();
                break;
            case "backupRestorePanel":
                backupRestorePanel.refresh();
                break;
        }

        cardLayout. show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(AdminUI::new);
    }
}