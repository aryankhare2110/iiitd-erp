package edu.univ.erp.ui.instructor;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.ui.instructor.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class InstrDashUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton selectedButton = null;
    FacultyService facultyService = new FacultyService();

    public InstrDashUI() {
        super("IIITD ERP – Instructor Dashboard");
        setLayout(new BorderLayout());

        //Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(new Color(37, 47, 63));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(37, 47, 63));
        logoPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(edu.univ.erp.ui.instructor.InstrDashUI.class.getResource("/Images/img.png")));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.add(logoLabel);

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        //Menu Buttons
        JButton btnDashboard = navButton("Dashboard");
        JButton btnSection = navButton("My Sections");
        JButton btnScore = navButton("Scores Menu");
        JButton btnStatistics = navButton("Section Statistics");
        JButton btnLogout = navButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSection);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnScore);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStatistics);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout.WEST);

        //Content Panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new DashboardPanel(facultyService), "dashboard");
        contentPanel.add(new MySectionsPanel(facultyService), "mySections");
        contentPanel.add(new ScoresMenuPanel(facultyService), "scoresMenu");
        contentPanel.add(new StatisticsPanel(facultyService), "statistics");

        add(contentPanel, BorderLayout.CENTER);

        //Button Actions
        btnDashboard.addActionListener(e -> {
            show("dashboard");
            setSelected(btnDashboard);
        });

        btnSection.addActionListener(e -> {
            show("mySections");
            setSelected(btnSection);
        });

        btnSection.addActionListener(e -> {
            show("scoresMenu");
            setSelected(btnScore);
        });

        btnSection.addActionListener(e -> {
            show("statistics");
            setSelected(btnStatistics);
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
        b.setContentAreaFilled(false);
        b.setOpaque(true);
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
        SwingUtilities.invokeLater(edu.univ.erp.ui.instructor.InstrDashUI::new);
    }
}