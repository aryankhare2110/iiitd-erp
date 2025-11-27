package edu.univ.erp.ui. faculty. panels;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.FacultyService;
import edu.univ. erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class StatisticsPanel extends JPanel {

    private final FacultyService facultyService = new FacultyService();
    private Faculty faculty;

    private JComboBox<String> sectionCombo;
    private List<Section> sections;

    private JPanel histogramPanel;
    private JLabel avgScoreLabel;
    private JLabel highestScoreLabel;
    private JLabel lowestScoreLabel;
    private JLabel passRateLabel;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        faculty = facultyService.getMyProfile();

        add(UIUtils.createHeaderWithBadge("Section Statistics",
                        "View class performance statistics and grade distribution",
                        facultyService.isMaintenanceMode(),
                        " ⚠ MAINTENANCE MODE "),
                BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Section Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(new Color(248, 249, 250));
        selectorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        selectorPanel.add(UIUtils.makeLabel("Select Section:", true));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(400, 30));
        sectionCombo.addActionListener(e -> loadStatistics());
        selectorPanel. add(sectionCombo);

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Stats Cards Row
        mainPanel.add(createStatsCards());
        mainPanel.add(Box. createVerticalStrut(20));

        // Histogram
        mainPanel.add(createHistogramSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadSections();
    }

    private JPanel createStatsCards() {
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsRow.setBackground(new Color(248, 249, 250));
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel avgCard = createMiniStatCard("Average Score", "0.00", new Color(13, 110, 253));
        JPanel highCard = createMiniStatCard("Highest Score", "0.00", new Color(25, 135, 84));
        JPanel lowCard = createMiniStatCard("Lowest Score", "0.00", new Color(220, 53, 69));
        JPanel passCard = createMiniStatCard("Pass Rate", "0%", new Color(111, 66, 193));

        avgScoreLabel = findStatLabel(avgCard);
        highestScoreLabel = findStatLabel(highCard);
        lowestScoreLabel = findStatLabel(lowCard);
        passRateLabel = findStatLabel(passCard);

        cardsRow.add(avgCard);
        cardsRow.add(highCard);
        cardsRow. add(lowCard);
        cardsRow.add(passCard);

        return cardsRow;
    }

    private JPanel createMiniStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory. createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout. Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Helvetica Neue", Font. PLAIN, 12));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Helvetica Neue", Font. BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(valueLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JLabel findStatLabel(JPanel card) {
        for (Component comp : card.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        if (label.getFont().getSize() >= 24) {
                            return label;
                        }
                    }
                }
            }
        }
        return null;
    }

    private JPanel createHistogramSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel titleLabel = new JLabel("Grade Distribution");
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        section.add(titleLabel, BorderLayout.NORTH);

        histogramPanel = new JPanel();
        histogramPanel.setBackground(Color.WHITE);
        histogramPanel.setPreferredSize(new Dimension(0, 200));

        section.add(histogramPanel, BorderLayout.CENTER);
        return section;
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        if (faculty == null) return;

        sections = facultyService.getMySections();
        for (Section s : sections) {
            Course c = facultyService.getCourseById(s.getCourseID());
            if (c != null) {
                sectionCombo.addItem(c.getCode() + " - " + s.getTerm() + " " + s. getYear());
            }
        }

        if (! sections.isEmpty()) {
            loadStatistics();
        }
    }

    private void loadStatistics() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1 || sections. isEmpty()) return;

        Section section = sections. get(idx);
        List<Enrollment> enrollments = facultyService.getEnrolledStudents(section.getSectionID());

        if (enrollments.isEmpty()) {
            resetStatistics();
            return;
        }

        // Collect all grades
        List<Double> scores = new ArrayList<>();
        Map<String, Integer> gradeDistribution = new LinkedHashMap<>();
        gradeDistribution.put("A", 0);
        gradeDistribution. put("A-", 0);
        gradeDistribution.put("B", 0);
        gradeDistribution.put("B-", 0);
        gradeDistribution.put("C", 0);
        gradeDistribution.put("C-", 0);
        gradeDistribution. put("D", 0);
        gradeDistribution.put("F", 0);

        for (Enrollment e : enrollments) {
            Grade grade = facultyService.getGrade(e.getEnrollmentId());

            if (grade != null) {
                double score = grade.getTotalScore();
                String gradeLabel = grade.getGradeLabel();

                scores.add(score);
                gradeDistribution.put(gradeLabel, gradeDistribution.get(gradeLabel) + 1);
            }
        }

        if (scores.isEmpty()) {
            resetStatistics();
            return;
        }

        // Calculate statistics
        double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double highest = scores.stream(). mapToDouble(Double::doubleValue).max().orElse(0.0);
        double lowest = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        long passCount = scores.stream().filter(s -> s >= 60).count();
        double passRate = (passCount * 100.0) / scores.size();

        // Update stat cards
        if (avgScoreLabel != null) avgScoreLabel.setText(String. format("%.2f", avg));
        if (highestScoreLabel != null) highestScoreLabel.setText(String.format("%. 2f", highest));
        if (lowestScoreLabel != null) lowestScoreLabel.setText(String.format("%.2f", lowest));
        if (passRateLabel != null) passRateLabel.setText(String.format("%.1f%%", passRate));

        // Draw histogram
        drawHistogram(gradeDistribution, enrollments. size());
    }

    private void resetStatistics() {
        if (avgScoreLabel != null) avgScoreLabel.setText("0.00");
        if (highestScoreLabel != null) highestScoreLabel.setText("0.00");
        if (lowestScoreLabel != null) lowestScoreLabel.setText("0.00");
        if (passRateLabel != null) passRateLabel.setText("0%");

        histogramPanel.removeAll();
        histogramPanel. revalidate();
        histogramPanel.repaint();
    }

    private void drawHistogram(Map<String, Integer> distribution, int totalStudents) {
        histogramPanel.removeAll();
        histogramPanel.setLayout(new GridLayout(1, distribution.size(), 10, 0));

        int maxCount = distribution.values().stream().max(Integer::compareTo).orElse(1);

        Color[] colors = {
                new Color(25, 135, 84),   // A - Green
                new Color(40, 167, 69),   // A- - Light Green
                new Color(13, 110, 253),  // B - Blue
                new Color(23, 162, 184),  // B- - Cyan
                new Color(255, 193, 7),   // C - Yellow
                new Color(253, 126, 20),  // C- - Orange
                new Color(220, 53, 69),   // D - Red
                new Color(108, 117, 125)  // F - Gray
        };

        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            String grade = entry.getKey();
            int count = entry.getValue();
            double percentage = (count * 100.0) / totalStudents;

            histogramPanel.add(createHistogramBar(grade, count, percentage, maxCount, colors[colorIndex % colors.length]));
            colorIndex++;
        }

        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    private JPanel createHistogramBar(String label, int count, double percentage, int maxCount, Color color) {
        JPanel barContainer = new JPanel();
        barContainer.setLayout(new BoxLayout(barContainer, BoxLayout.Y_AXIS));
        barContainer.setBackground(Color.WHITE);

        // Count label
        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Helvetica Neue", Font. BOLD, 12));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bar
        JPanel barWrapper = new JPanel();
        barWrapper.setLayout(new BorderLayout());
        barWrapper.setBackground(Color.WHITE);
        barWrapper. setPreferredSize(new Dimension(40, 120));

        int barHeight = maxCount > 0 ? (int) ((count * 100.0) / maxCount) : 0;

        JPanel spacer = new JPanel();
        spacer.setBackground(Color.WHITE);
        spacer.setPreferredSize(new Dimension(40, 100 - barHeight));

        JPanel bar = new JPanel();
        bar.setBackground(color);
        bar.setPreferredSize(new Dimension(40, barHeight));

        barWrapper. add(spacer, BorderLayout. NORTH);
        barWrapper.add(bar, BorderLayout.CENTER);

        // Grade label
        JLabel gradeLabel = new JLabel(label);
        gradeLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Percentage label
        JLabel percentLabel = new JLabel(String.format("%.1f%%", percentage));
        percentLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 10));
        percentLabel.setForeground(new Color(108, 117, 125));
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barContainer.add(countLabel);
        barContainer.add(Box.createVerticalStrut(5));
        barContainer.add(barWrapper);
        barContainer. add(Box.createVerticalStrut(5));
        barContainer.add(gradeLabel);
        barContainer.add(percentLabel);

        return barContainer;
    }

    public void refresh() {
        loadSections();
    }
}