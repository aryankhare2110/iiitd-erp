package edu.univ.erp.ui.admin;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.admin.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.BaseFrame;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class AdminUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedButton = null;

    public AdminUI() {
        super("IIITD ERP – Admin Dashboard");
        setLayout(new BorderLayout());

        JPanel sidebar = UIUtils.createSidebar();

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(AdminUI.class.getResource("/Images/img.png")));
        Image scaled = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
        sidebar.add(UIUtils.sidebarLogoPanel(new ImageIcon(scaled)));
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnDashboard = UIUtils.sidebarButton("Dashboard");
        JButton btnStudents = UIUtils.sidebarButton("Manage Students");
        JButton btnFaculty = UIUtils.sidebarButton("Manage Faculty");
        JButton btnAdmins = UIUtils.sidebarButton("Manage Admins");
        JButton btnCourses = UIUtils.sidebarButton("Manage Courses");
        JButton btnSections = UIUtils.sidebarButton("Manage Sections");
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
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSections);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        AdminService adminService = new AdminService();

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new ManageStudentsPanel(), "manageStudents");
        contentPanel.add(new ManageFacultyPanel(), "manageFaculty");
        contentPanel.add(new ManageAdminPanel(), "manageAdmins");
        contentPanel.add(new ManageCoursesPanel(adminService), "manageCourses");
        contentPanel.add(new ManageSectionsPanel(), "manageSections");

        add(contentPanel, BorderLayout.CENTER);

        btnDashboard.addActionListener(e -> switchPanel(btnDashboard, "dashboard"));
        btnStudents.addActionListener(e -> switchPanel(btnStudents,  "manageStudents"));
        btnFaculty.addActionListener(e -> switchPanel(btnFaculty,   "manageFaculty"));
        btnAdmins.addActionListener(e -> switchPanel(btnAdmins,    "manageAdmins"));
        btnCourses.addActionListener(e -> switchPanel(btnCourses,   "manageCourses"));
        btnSections.addActionListener(e -> switchPanel(btnSections,  "manageSections"));

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        switchPanel(btnDashboard, "dashboard");
        setVisible(true);
    }

    private void switchPanel(JButton button, String panelName) {
        UIUtils.setSidebarSelected(selectedButton, button);
        selectedButton = button;
        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(AdminUI::new);
    }
}