package edu. univ.erp.ui. student.panels;

import edu. univ.erp.domain.Student;
import edu.univ. erp.domain.TimetableEntry;
import edu.univ.erp.service.StudentService;
import edu. univ.erp.ui. common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax. swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TimetablePanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private JPanel schedulePanel;

    public TimetablePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils. createHeader("My Timetable", "View your class schedule"), BorderLayout.NORTH);

        schedulePanel = new JPanel();
        schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
        schedulePanel.setBackground(new Color(248, 249, 250));
        schedulePanel. setBorder(new EmptyBorder(20, 50, 20, 50));

        JScrollPane scrollPane = new JScrollPane(schedulePanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.secondaryButton("Refresh", e -> loadTimetable())
        );
        add(bottom, BorderLayout.SOUTH);

        loadTimetable();
    }

    private void loadTimetable() {
        schedulePanel.removeAll();

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
            noClasses.setAlignmentX(Component.CENTER_ALIGNMENT);
            schedulePanel.add(Box.createVerticalStrut(50));
            schedulePanel.add(noClasses);
        } else {
            Map<String, List<TimetableEntry>> entriesByDay = new LinkedHashMap<>();
            String[] daysOrder = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

            for (String day : daysOrder) {
                entriesByDay.put(day, new ArrayList<>());
            }

            for (TimetableEntry entry : entries) {
                String day = entry.getDay(). toUpperCase();
                if (entriesByDay.containsKey(day)) {
                    entriesByDay.get(day). add(entry);
                }
            }

            for (List<TimetableEntry> dayEntries : entriesByDay. values()) {
                dayEntries.sort(Comparator.comparing(TimetableEntry::getStartTime));
            }

            boolean first = true;
            for (Map.Entry<String, List<TimetableEntry>> dayEntry : entriesByDay.entrySet()) {
                if (!dayEntry.getValue().isEmpty()) {
                    if (!first) {
                        schedulePanel.add(Box.createVerticalStrut(20));
                    }
                    first = false;

                    schedulePanel.add(createDaySection(dayEntry.getKey(), dayEntry.getValue()));
                }
            }
        }

        schedulePanel.revalidate();
        schedulePanel.repaint();
    }

    private JPanel createDaySection(String day, List<TimetableEntry> entries) {
        JPanel dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
        dayPanel.setBackground(Color.WHITE);
        dayPanel. setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        dayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Day header
        JLabel dayLabel = new JLabel(capitalize(day));
        dayLabel. setFont(new Font("Helvetica Neue", Font.BOLD, 20));
        dayLabel.setForeground(new Color(13, 110, 253));
        dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dayPanel.add(dayLabel);
        dayPanel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < entries.size(); i++) {
            TimetableEntry entry = entries. get(i);
            dayPanel.add(createClassEntry(entry));

            if (i < entries.size() - 1) {
                dayPanel.add(Box.createVerticalStrut(10));
            }
        }

        return dayPanel;
    }

    private JPanel createClassEntry(TimetableEntry entry) {
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.Y_AXIS));
        entryPanel.setBackground(new Color(248, 249, 250));
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        entryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Course code and type
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setBackground(new Color(248, 249, 250));
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel courseLabel = new JLabel(entry.getCode() + " ");
        courseLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        courseLabel.setForeground(new Color(33, 37, 41));

        JLabel typeLabel = new JLabel("(" + entry.getDescription() + ")");
        typeLabel.setFont(new Font("Helvetica Neue", Font. PLAIN, 14));
        typeLabel.setForeground(new Color(108, 117, 125));

        topRow.add(courseLabel);
        topRow.add(typeLabel);

        // Time
        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timeRow.setBackground(new Color(248, 249, 250));
        timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel timeIcon = new JLabel("Time: ");
        timeIcon.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));

        JLabel timeLabel = new JLabel(entry.getStartTime() + " - " + entry.getEndTime());
        timeLabel. setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(73, 80, 87));

        timeRow.add(timeIcon);
        timeRow.add(timeLabel);

        // Instructor and Room
        JPanel detailsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailsRow. setBackground(new Color(248, 249, 250));
        detailsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel instructorIcon = new JLabel("Instructor: ");
        instructorIcon. setFont(new Font("Helvetica Neue", Font.PLAIN, 14));

        JLabel instructorLabel = new JLabel(entry.getFullName() + "  |  ");
        instructorLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        instructorLabel. setForeground(new Color(73, 80, 87));

        JLabel roomIcon = new JLabel("Room: ");
        roomIcon. setFont(new Font("Helvetica Neue", Font.PLAIN, 14));

        JLabel roomLabel = new JLabel(entry.getRoom());
        roomLabel.setFont(new Font("Helvetica Neue", Font. PLAIN, 14));
        roomLabel.setForeground(new Color(73, 80, 87));

        detailsRow.add(instructorIcon);
        detailsRow.add(instructorLabel);
        detailsRow.add(roomIcon);
        detailsRow.add(roomLabel);

        entryPanel.add(topRow);
        entryPanel.add(Box.createVerticalStrut(5));
        entryPanel.add(timeRow);
        entryPanel. add(Box.createVerticalStrut(5));
        entryPanel.add(detailsRow);

        return entryPanel;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void refresh() {
        loadTimetable();
    }
}