package edu.univ.erp.ui.faculty;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.ui.faculty.panels.*;
import edu.univ.erp.ui.auth.LoginUI;
import edu.univ.erp.ui.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class FacultyUI extends BaseFrame {

      private CardLayout cardLayout;
      private JPanel contentPanel;
      private JPanel sidebar;

      private DashboardPanel dashboardPanel;
      private MySectionsPanel mySectionsPanel;
      private ScoresPanel scoresPanel;
      private StatisticsPanel statisticsPanel;

      public FacultyUI() {
          super("IIITD ERP – Faculty Dashboard");
          setLayout(new BorderLayout());

          sidebar = UIUtils.createSidebar();
          ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(edu.univ.erp.ui.faculty.FacultyUI.class.getResource("/Images/logo.png")));
          Image scaled = logoIcon.getImage().getScaledInstance(90, 52, Image.SCALE_SMOOTH);
          sidebar.add(UIUtils. sidebarLogoPanel(new ImageIcon(scaled)));
          sidebar.add(Box.createVerticalStrut(10));

          JButton btnDashboard = UIUtils.sidebarButton("Dashboard");
          JButton btnMySections = UIUtils.sidebarButton("My Sections");
          JButton btnScores = UIUtils. sidebarButton("Scores");
          JButton btnStatistics = UIUtils.sidebarButton("Section Statistics");
          JButton btnLogout = UIUtils.sidebarButton("Logout");

          sidebar.add(btnDashboard);
          sidebar.add(Box.createVerticalStrut(5));
          sidebar.add(btnMySections);
          sidebar.add(Box.createVerticalStrut(5));
          sidebar.add(btnScores);
          sidebar.add(Box.createVerticalStrut(5));
          sidebar.add(btnStatistics);

          sidebar.add(Box.createVerticalGlue());
          sidebar.add(btnLogout);
          sidebar.add(Box.createVerticalStrut(15));

          add(sidebar, BorderLayout. WEST);

          cardLayout = new CardLayout();
          contentPanel = new JPanel(cardLayout);
          contentPanel.setBackground(Color.WHITE);

          FacultyService facultyService = new FacultyService();

          dashboardPanel = new DashboardPanel();
          mySectionsPanel = new MySectionsPanel();
          scoresPanel = new ScoresPanel();
          statisticsPanel = new StatisticsPanel();

          contentPanel.add(dashboardPanel, "dashboard");
          contentPanel.add(mySectionsPanel, "mySections");
          contentPanel.add(scoresPanel, "scores");
          contentPanel.add(statisticsPanel, "statistics");

          add(contentPanel, BorderLayout.CENTER);

          btnDashboard.addActionListener(e -> switchPanel(btnDashboard,"dashboard"));
          btnMySections.addActionListener(e -> switchPanel(btnMySections,"mySections"));
          btnScores.addActionListener(e -> switchPanel(btnScores,"scores"));
          btnStatistics.addActionListener(e -> switchPanel(btnStatistics,"statistics"));

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
              case "mySections":
                  mySectionsPanel.refresh();
                  break;
              case "scores":
                  scoresPanel.refresh();
                  break;
              case "statistics":
                  statisticsPanel.refresh();
                  break;
          }

          cardLayout. show(contentPanel, panelName);
      }

      public static void main(String[] args) {
          FlatIntelliJLaf.setup();
          SwingUtilities.invokeLater(edu.univ.erp.ui.faculty.FacultyUI::new);
      }
}
