package edu.univ.erp.ui.faculty.panels;

import edu.univ.erp.dao.ComponentScoreDAO;
import edu.univ.erp.dao.SectionComponentDAO;
import edu.univ.erp.dao.EnrollmentDAO;
import edu.univ.erp.domain.ComponentScore;
import edu.univ.erp.domain.SectionComponent;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.ui.common.UIUtils;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.dao.CourseDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * StatisticsPanel (faculty) — computes average score per component by calling
 * scoreDAO.getScore(enrollmentId, componentId) for each enrollment × component.
 */
public class StatisticsPanel extends JPanel {
    private final FacultyService facultyService = new FacultyService();
    private final SectionComponentDAO componentDAO = new SectionComponentDAO();
    private final ComponentScoreDAO scoreDAO = new ComponentScoreDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    private final JComboBox<String> sectionCombo;
    private final ChartPanel chartPanel;

    private final List<Section> facultySections = new ArrayList<>();

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        add(UIUtils.createHeader("Section Statistics", "Average scores per component"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(8, 12, 8, 12));
        top.add(new JLabel("Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(360, 28));
        sectionCombo.addActionListener(e -> loadAndRender());
        top.add(sectionCombo);

        JButton refreshSectionsBtn = UIUtils.secondaryButton("Refresh Sections", e -> loadSections());
        top.add(refreshSectionsBtn);

        add(top, BorderLayout.NORTH);

        chartPanel = new ChartPanel();
        chartPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(new JScrollPane(chartPanel), BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.secondaryButton("Refresh", e -> loadAndRender())
        );
        add(bottom, BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        facultySections.clear();
        try {
            var me = facultyService.getMyProfile();
            if (me == null) {
                DialogUtils.errorDialog("Unable to load faculty profile.");
                return;
            }
            List<Section> my = facultyService.mySections(me.getFacultyId());
            for (Section s : my) {
                facultySections.add(s);
                CourseDAO cd = new CourseDAO();
                var c = cd.getCourseById(s.getCourseID());
                String label = String.format("%d — %s (%s %d)", s.getSectionID(), c != null ? c.getCode() : "Unknown", s.getTerm(), s.getYear());
                sectionCombo.addItem(label);
            }
            if (sectionCombo.getItemCount() > 0) sectionCombo.setSelectedIndex(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to load sections: " + ex.getMessage());
        }
    }

    private void loadAndRender() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx < 0 || idx >= facultySections.size()) {
            chartPanel.setData(Collections.emptyMap());
            return;
        }
        int sectionId = facultySections.get(idx).getSectionID();
        try {
            List<SectionComponent> comps = componentDAO.getComponentsBySection(sectionId);
            Map<String, Double> averages = new LinkedHashMap<>();

            // get all enrollments for this section
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(sectionId);

            for (SectionComponent sc : comps) {
                double sum = 0.0;
                int cnt = 0;
                int compId = sc.getComponentID();

                // for each enrollment, fetch the score for this component using the requested method
                for (Enrollment en : enrollments) {
                    Double sVal = scoreDAO.getScore(en.getEnrollmentId(), compId).getScore(); // <-- requested method
                    if (sVal != null) {
                        sum += sVal;
                        cnt++;
                    }
                }

                if (cnt > 0) {
                    String name = (sc.getDescription() != null && !sc.getDescription().isBlank())
                            ? sc.getDescription()
                            : ("Comp " + sc.getComponentID());
                    averages.put(name, sum / cnt);
                }
            }
            chartPanel.setData(averages);
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to load statistics: " + ex.getMessage());
        }
    }

    // Simple bar-chart panel (same style as earlier)
    private static class ChartPanel extends JPanel {
        private Map<String, Double> data = Collections.emptyMap();

        ChartPanel() {
            setPreferredSize(new Dimension(700, 360));
            setBackground(Color.WHITE);
        }

        void setData(Map<String, Double> data) {
            this.data = data != null ? data : Collections.emptyMap();
            int height = Math.max(360, 60 + data.size() * 60);
            setPreferredSize(new Dimension(700, height));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) {
                g.setColor(new Color(108,117,125));
                g.setFont(new Font("Helvetica Neue", Font.ITALIC, 16));
                g.drawString("No scores available to show statistics.", 20, 40);
                return;
            }

            int padLeft = 80, padTop = 40, padBottom = 60;
            int w = getWidth() - padLeft - 40;
            int h = getHeight() - padTop - padBottom;
            double maxVal = data.values().stream().mapToDouble(d -> d).max().orElse(100.0);

            int barCount = data.size();
            int barWidth = Math.max(30, w / (barCount * 2));
            int gap = Math.max(10, (w - barCount * barWidth) / (barCount + 1));

            int x = padLeft + gap;
            for (Map.Entry<String, Double> e : data.entrySet()) {
                double val = e.getValue();
                int barHeight = (int) Math.round((val / maxVal) * (h - 20));
                int y = padTop + (h - barHeight);

                g.setColor(new Color(13,110,253));
                g.fillRect(x, y, barWidth, barHeight);

                g.setColor(Color.BLACK);
                g.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
                String valStr = String.format("%.1f", val);
                int strW = g.getFontMetrics().stringWidth(valStr);
                g.drawString(valStr, x + (barWidth - strW) / 2, y - 6);

                g.setFont(new Font("Helvetica Neue", Font.PLAIN, 11));
                String label = e.getKey();
                int labelY = padTop + h + 18;
                g.drawString(label, x, labelY);

                x += barWidth + gap;
            }

            g.setColor(Color.BLACK);
            g.drawLine(padLeft, padTop, padLeft, padTop + h);
            g.drawLine(padLeft, padTop + h, getWidth() - 20, padTop + h);
        }
    }

    public void refresh() {
        loadSections();
        loadAndRender();
    }

    /**
     * Programmatically select a section id (FacultyUI may call this when user selected a section in MySectionsPanel)
     */
    public void setSection(int sectionId) {
        for (int i = 0; i < facultySections.size(); i++) {
            if (facultySections.get(i).getSectionID() == sectionId) {
                sectionCombo.setSelectedIndex(i);
                return;
            }
        }
        loadSections();
        for (int i = 0; i < facultySections.size(); i++) {
            if (facultySections.get(i).getSectionID() == sectionId) {
                sectionCombo.setSelectedIndex(i);
                return;
            }
        }
    }
}