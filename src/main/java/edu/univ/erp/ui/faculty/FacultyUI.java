package edu. univ.erp.ui. faculty;

import com.formdev.flatlaf. FlatIntelliJLaf;
import edu.univ.erp. ui.faculty.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ. erp.ui.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class FacultyUI extends BaseFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;

    private DashboardPanel dashboardPanel;
    private MySectionsPanel mySectionsPanel;
    private ComponentsPanel componentsPanel;
    private GradingPanel gradingPanel;
    private StatisticsPanel statisticsPanel;

    public FacultyUI() {
        super("IIITD ERP – Faculty Dashboard");
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = UIUtils.createSidebar();

        try {
            ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(
                    getClass().getResource("/Images/logo.png")));
            Image scaled = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
            sidebar.add(UIUtils.sidebarLogoPanel(new ImageIcon(scaled)));
        } catch (Exception e) {
            System.err.println("Logo not found");
        }

        sidebar.add(Box.createVerticalStrut(10));

        JButton btnDashboard = UIUtils. sidebarButton("Dashboard");
        JButton btnMySections = UIUtils.sidebarButton("My Sections");
        JButton btnComponents = UIUtils.sidebarButton("Components");
        JButton btnGrading = UIUtils.sidebarButton("Grading");
        JButton btnStatistics = UIUtils.sidebarButton("Section Statistics");
        JButton btnLogout = UIUtils.sidebarButton("Logout");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnMySections);
        sidebar.add(Box. createVerticalStrut(5));
        sidebar.add(btnComponents);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnGrading);
        sidebar. add(Box.createVerticalStrut(5));
        sidebar. add(btnStatistics);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(15));

        add(sidebar, BorderLayout.WEST);

        // Content panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        dashboardPanel = new DashboardPanel();
        mySectionsPanel = new MySectionsPanel();
        componentsPanel = new ComponentsPanel();
        gradingPanel = new GradingPanel();
        statisticsPanel = new StatisticsPanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel. add(mySectionsPanel, "mySections");
        contentPanel. add(componentsPanel, "components");
        contentPanel.add(gradingPanel, "grading");
        contentPanel.add(statisticsPanel, "statistics");

        add(contentPanel, BorderLayout.CENTER);

        // Button actions
        btnDashboard. addActionListener(e -> switchPanel(btnDashboard, "dashboard"));
        btnMySections. addActionListener(e -> switchPanel(btnMySections, "mySections"));
        btnComponents. addActionListener(e -> switchPanel(btnComponents, "components"));
        btnGrading.addActionListener(e -> switchPanel(btnGrading, "grading"));
        btnStatistics.addActionListener(e -> switchPanel(btnStatistics, "statistics"));

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginUI();
        });

        switchPanel(btnDashboard, "dashboard");
        setVisible(true);
    }

    private void switchPanel(JButton btn, String panelName) {
        // Reset all button colors
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setForeground(new Color(180, 190, 210));
            }
        }

        // Highlight selected button
        btn.setForeground(new Color(248, 249, 250));

        // Refresh panel before showing
        switch (panelName) {
            case "dashboard":
                dashboardPanel.refresh();
                break;
            case "mySections":
                mySectionsPanel. refresh();
                break;
            case "components":
                componentsPanel.refresh();
                break;
            case "grading":
                gradingPanel.refresh();
                break;
            case "statistics":
                statisticsPanel.refresh();
                break;
        }

        cardLayout.show(contentPanel, panelName);
    }

    public static void main(String[] args) {
        FlatIntelliJLaf. setup();
        SwingUtilities.invokeLater(FacultyUI::new);
    }
}