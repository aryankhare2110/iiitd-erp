package edu.univ.erp.ui.admin;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.admin.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class AdminDashUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedButton = null;
    AdminService adminService = new AdminService();

    public AdminDashUI() {
        super("IIITD ERP – Admin Dashboard");
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(37, 47, 63));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(37, 47, 63));
        logoPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(AdminDashUI.class.getResource("/Images/img.png")));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoLabel);
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnDashboard = navButton("Dashboard");
        JButton btnStudent = navButton("Manage Students");
        JButton btnFaculty = navButton("Manage Faculty");
        JButton btnAdmins = navButton("Manage Admins");
        JButton btnCourse = navButton("Manage Courses");
        JButton btnSection = navButton("Manage Sections");
        JButton btnLogout = navButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStudent);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnFaculty);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAdmins);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCourse);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSection);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new ManageStudentsPanel(), "manageStudents");
        contentPanel.add(new ManageFacultyPanel(), "manageFaculty");
        contentPanel.add(new ManageAdminPanel(), "manageAdmins");
        contentPanel.add(new ManageCoursesPanel(adminService), "manageCourses");
        contentPanel.add(new ManageSectionsPanel(adminService), "manageSection");

        add(contentPanel, BorderLayout.CENTER);

        //Button Actions
        btnDashboard.addActionListener(e -> {
            show("dashboard");
            setSelected(btnDashboard);
        });

        btnStudent.addActionListener(e -> {
            show("manageStudents");
            setSelected(btnStudent);
        });

        btnFaculty.addActionListener(e -> {
            show("manageFaculty");
            setSelected(btnFaculty);
        });

        btnAdmins.addActionListener(e -> {
            show("manageAdmins");
            setSelected(btnAdmins);
        });

        btnCourse.addActionListener(e -> {
            show("manageCourses");
            setSelected(btnCourse);
        });

        btnSection.addActionListener(e -> {
            show("manageSection");
            setSelected(btnSection);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        //Default (Dashboard)
        setSelected(btnDashboard);
        setVisible(true);
    }

    private JButton navButton(String text) {
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

    private void setSelected(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBackground(new Color(37, 47, 63));
            selectedButton.setForeground(new Color(180, 190, 210));
        }
        selectedButton = button;
        button.setBackground(new Color(13, 110, 253));
        button.setForeground(Color.WHITE);
    }

    private void show(String name) {
        cardLayout.show(contentPanel, name);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(AdminDashUI::new);
    }

}