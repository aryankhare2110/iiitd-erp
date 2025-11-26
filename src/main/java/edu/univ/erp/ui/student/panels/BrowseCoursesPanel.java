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
import java.time.LocalDate;
import java.util.List;

public class BrowseCoursesPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private JTable table;
    private DefaultTableModel model;
    private List<Course> courses;

    public BrowseCoursesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        LocalDate ddl = studentService.getAddDropDeadline();
        boolean showBadge = ddl != null;
        String badgeText = "";

        if (showBadge) {
            boolean closed = LocalDate.now().isAfter(ddl);
            badgeText = closed ? "Add/Drop Closed" : "Add/Drop until " + ddl;
        }

        add(UIUtils.createHeaderWithBadge("Browse Courses", "Select a course and register for sections", showBadge, badgeText), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(20, 50, 20, 50));

        model = new DefaultTableModel(
                new String[]{"Code", "Title", "Credits", "Prerequisites", "Department"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel btnRow = UIUtils.createButtonRow(UIUtils.primaryButton("Register", e -> openRegisterDialog()), UIUtils.secondaryButton("Refresh", e -> loadCourses()));
        add(btnRow, BorderLayout.SOUTH);

        loadCourses();
    }

    private void loadCourses() {
        model.setRowCount(0);
        courses = studentService.browseCourses();

        for (Course c : courses) {
            String deptName = studentService.getDepartmentName(c.getDepartmentID());
            String prereq = (c.getPrerequisites() == null || c.getPrerequisites().isEmpty())
                    ? "None" : c.getPrerequisites();

            model.addRow(new Object[]{c.getCode(), c.getTitle(), c.getCredits(), prereq, deptName});
        }
    }

    private void openRegisterDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            DialogUtils.errorDialog("Please select a course first.");
            return;
        }

        LocalDate ddl = studentService.getAddDropDeadline();
        if (ddl != null && LocalDate.now().isAfter(ddl)) {
            DialogUtils.errorDialog("Add/Drop deadline has passed (" + ddl + ").");
            return;
        }

        Course selected = courses.get(row);
        Student me = studentService.getMyProfile();
        if (me == null) {
            DialogUtils.errorDialog("Could not load student profile.");
            return;
        }

        int studentId = me.getStudentId();

        if (studentService.isEnrolled(studentId, selected.getCourseID())) {
            DialogUtils.errorDialog("You are already enrolled in " + selected.getCode() + ".");
            return;
        }

        List<Section> sections = studentService.getSectionsForCourse(selected.getCourseID());
        if (sections.isEmpty()) {
            DialogUtils.errorDialog("No sections available for " + selected.getCode() + ".");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(selected.getCode() + " – " + selected.getTitle());
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 17));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel credits = new JLabel("Credits: " + selected.getCredits());
        credits.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        credits.setAlignmentX(Component.LEFT_ALIGNMENT);

        String prereqText = (selected.getPrerequisites() == null) ? "None" : selected.getPrerequisites();
        JLabel prereq = new JLabel("Prerequisites: " + prereqText);
        prereq.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        prereq.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(credits);
        panel.add(Box.createVerticalStrut(5));
        panel.add(prereq);
        panel.add(Box.createVerticalStrut(20));

        String[] sectionOptions = new String[sections.size()];
        for (int i = 0; i < sections.size(); i++) {
            Section s = sections.get(i);
            Faculty f = studentService.getFacultyForSection(s.getInstructorID());

            String instructor = (f != null) ? f.getFullName() : "TBA";
            int enrolled = studentService.getEnrollmentCount(s.getSectionID());
            String seats = enrolled + "/" + s.getCapacity();

            sectionOptions[i] = String.format(
                    "%s %d | %s | Room: %s | Seats: %s",
                    s.getTerm(), s.getYear(), instructor, s.getRoom(), seats
            );
        }

        JLabel secLabel = new JLabel("Select Section:");
        secLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        secLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> sectionCombo = new JComboBox<>(sectionOptions);
        sectionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        sectionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(secLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sectionCombo);

        int choice = JOptionPane.showConfirmDialog(
                this, panel, "Register for " + selected.getCode(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) return;

        Section chosen = sections.get(sectionCombo.getSelectedIndex());

        int enrolled = studentService.getEnrollmentCount(chosen.getSectionID());
        if (enrolled >= chosen.getCapacity()) {
            DialogUtils.errorDialog("This section is full (" + enrolled + "/" + chosen.getCapacity() + ").");
            return;
        }

        boolean ok = studentService.registerForSection(studentId, chosen.getSectionID());
        if (ok) {
            DialogUtils.infoDialog("Successfully registered for " + selected.getCode() + "!");
            loadCourses();
        } else {
            DialogUtils.errorDialog("Registration failed.");
        }
    }

    public void refresh() {
        loadCourses();
    }
}