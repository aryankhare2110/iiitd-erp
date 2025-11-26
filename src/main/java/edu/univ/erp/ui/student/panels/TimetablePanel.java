package edu.univ.erp.ui.student.panels;

import edu.univ.erp.domain.Student;
import edu.univ. erp.domain.TimetableEntry;
import edu.univ.erp.service.StudentService;
import edu.univ. erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java. util.*;
import java.util. List;

public class TimetablePanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private JPanel schedulePanel;

    public TimetablePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("My Timetable", "View your class schedule"), BorderLayout.NORTH);

        schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        schedulePanel.setBackground(new Color(248, 249, 250));
        schedulePanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        JScrollPane scrollPane = new JScrollPane(schedulePanel);
        scrollPane. setBorder(null);
        scrollPane.getVerticalScrollBar(). setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadTimetable();
    }

    private void loadTimetable() {
        schedulePanel. removeAll();

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        List<TimetableEntry> entries = studentService.getTimetable(student.getStudentId());

        if (entries.isEmpty()) {
            JLabel noClasses = new JLabel("No classes scheduled");
            noClasses.setFont(new Font("Helvetica Neue", Font.ITALIC, 18));
            noClasses.setForeground(new Color(108, 117, 125));
            schedulePanel.add(Box.createVerticalStrut(50));
            schedulePanel.add(noClasses);
        } else {
            Map<String, List<TimetableEntry>> byDay = new LinkedHashMap<>();
            for (String day : new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"}) {
                byDay.put(day, new ArrayList<>());
            }

            for (TimetableEntry e : entries) {
                String day = e.getDay().toUpperCase();
                if (byDay.containsKey(day)) byDay.get(day).add(e);
            }

            // Display each day
            boolean first = true;
            for (Map.Entry<String, List<TimetableEntry>> entry : byDay.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    if (!first) schedulePanel.add(Box.createVerticalStrut(20));
                    first = false;

                    entry.getValue().sort(Comparator.comparing(TimetableEntry::getStartTime));
                    schedulePanel.add(createDayPanel(entry.getKey(), entry. getValue()));
                }
            }
        }

        schedulePanel.revalidate();
        schedulePanel.repaint();
    }

    private JPanel createDayPanel(String day, List<TimetableEntry> entries) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1), new EmptyBorder(20, 20, 20, 20)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel dayLabel = new JLabel(day.substring(0, 1) + day.substring(1).toLowerCase());
        dayLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 20));
        dayLabel.setForeground(new Color(13, 110, 253));
        panel.add(dayLabel);
        panel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < entries.size(); i++) {
            panel.add(createClassCard(entries.get(i)));
            if (i < entries.size() - 1) panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    private JPanel createClassCard(TimetableEntry e) {
        JPanel card = new JPanel();
        card. setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(248, 249, 250));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1), new EmptyBorder(12, 15, 12, 15)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        card.add(createLabel(e.getCode() + " (" + e.getDescription() + ")", Font.BOLD, 16, new Color(33, 37, 41)));
        card.add(Box.createVerticalStrut(5));
        card.add(createLabel("Time: " + e.getStartTime() + " - " + e.getEndTime(), Font. PLAIN, 14, new Color(73, 80, 87)));
        card.add(Box.createVerticalStrut(5));
        card.add(createLabel("Instructor: " + e.getFullName() + "  |  Room: " + e.getRoom(), Font.PLAIN, 14, new Color(73, 80, 87)));

        return card;
    }

    private JLabel createLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Helvetica Neue", style, size));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public void refresh() {
        loadTimetable();
    }
}