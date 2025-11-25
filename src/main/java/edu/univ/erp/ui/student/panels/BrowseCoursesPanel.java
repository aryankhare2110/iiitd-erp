package edu.univ.erp.ui.student.panels;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrowseCoursesPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private final JTable table;
    private final DefaultTableModel model;
    private final List<Course> courseList = new ArrayList<>();

    public BrowseCoursesPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Browse Courses", "View available courses and register for sections"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Code", "Title", "Credits"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(UIUtils.primaryButton("Show Info", e -> openInfoDialog()), UIUtils.primaryButton("Register", e -> openRegistrationDialog()));
        add(bottom, BorderLayout.SOUTH);

        loadCourses();
    }

    private void loadCourses() {
        model.setRowCount(0);
        courseList.clear();

        List<Course> courses = studentService.browseCourses();
        for (Course c : courses) {
            courseList.add(c);
            model.addRow(new Object[]{
                    c.getCode(), c.getTitle(), c.getCredits()
            });
        }
    }

    private void openInfoDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Please select a course first.");
            return;
        }

        Course selected = courseList.get(r);
        List<Section> sections = studentService.getSectionsForCourse(selected.getCourseID());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(selected.getCode() + " – " + selected.getTitle());
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel credit = new JLabel("Credits: " + selected.getCredits());
        credit.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        credit.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prereq = new JLabel("Prerequisites: " + (selected.getPrerequisites() == null ? "None" : selected.getPrerequisites()));
        prereq.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        prereq.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(credit);
        panel.add(Box.createVerticalStrut(5));
        panel.add(prereq);
        panel.add(Box.createVerticalStrut(20));

        JLabel secTitle = new JLabel("Available Sections:");
        secTitle.setFont(new Font("Helvetica Neue", Font.BOLD, 15));
        secTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(secTitle);
        panel.add(Box.createVerticalStrut(10));

        if (sections.isEmpty()) {
            JLabel none = new JLabel("No sections available.");
            none.setFont(new Font("Helvetica Neue", Font.ITALIC, 13));
            none.setForeground(new Color(108, 117, 125));
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(none);
        } else {
            for (Section s : sections) {
                Faculty f = studentService.getFacultyForSection(s.getInstructorID());
                String instructor = (f == null) ? "TBA" : f.getFullName();

                JLabel sec = new JLabel("• " + s.getTerm() + " " + s.getYear() + " | Instructor: " + instructor + " | Room: " + s.getRoom() + " | Capacity: " + s.getCapacity());
                sec.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
                sec.setAlignmentX(Component.LEFT_ALIGNMENT);

                panel.add(sec);
                panel.add(Box.createVerticalStrut(6));
            }
        }

        JOptionPane.showMessageDialog(this, panel, "Course Information", JOptionPane.PLAIN_MESSAGE);
    }

    private void openRegistrationDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a course first.");
            return;
        }

        Course selected = courseList.get(r);
        List<Section> sections = studentService.getSectionsForCourse(selected.getCourseID());

        if (sections.isEmpty()) {
            DialogUtils.errorDialog("No sections available for this course.");
            return;
        }

        String[] names = sections.stream()
                .map(s -> {
                    Faculty f = studentService.getFacultyForSection(s.getInstructorID());
                    String prof = (f == null) ? "TBA" : f.getFullName();
                    return selected.getCode() + " – " + prof;
                })
                .toArray(String[]::new);

        JComboBox<String> box = new JComboBox<>(names);

        JPanel p = new JPanel(new GridLayout(0, 1, 10, 10));
        p.add(new JLabel("Select Section:"));
        p.add(box);

        if (JOptionPane.showConfirmDialog(this, p, "Register", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }


        int idx = box.getSelectedIndex();
        Section chosen = sections.get(idx);

        Student me = studentService.getMyProfile();

        boolean ok = studentService.registerForSection(me.getStudentId(), chosen.getSectionID());

        if (ok) {
            DialogUtils.infoDialog("Registered successfully!");
        }
        else {
            DialogUtils.errorDialog("Could not register.\nAlready enrolled, full, or maintenance mode.");
        }

    }
}