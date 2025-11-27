package edu.univ.erp.ui.student.panels;

import edu.univ.erp.dao.CourseDAO;
import edu.univ.erp.dao.FacultyDAO;
import edu.univ.erp.dao.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
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

public class EnrollmentPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();

    private final JTable table;
    private final DefaultTableModel model;

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        LocalDate ddl = studentService.getAddDropDeadline();
        boolean showBadge = ddl != null;
        String badgeText = "";

        if (showBadge) {
            boolean closed = LocalDate.now().isAfter(ddl);
            badgeText = closed ? "Add/Drop Closed" : "Add/Drop until " + ddl;
        }

        add(UIUtils.createHeaderWithBadge("My Enrollments", "View and manage your course enrollments", showBadge, badgeText), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Course Code", "Course Title", "Credits", "Instructor", "Term", "Year"}, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        add(UIUtils.createButtonRow(UIUtils.primaryButton("Drop Course", e -> dropCourse())), BorderLayout.SOUTH);

        loadEnrollments();
    }

    private void loadEnrollments() {
        model.setRowCount(0);

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        List<Enrollment> enrollments = studentService.getMyEnrollments(student.getStudentId());

        if (enrollments.isEmpty()) {
            model.addRow(new Object[]{"No enrollments", "", "", "", "", ""});
            return;
        }

        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section == null) continue;

            Course course = courseDAO.getCourseById(section.getCourseID());
            if (course == null) continue;

            Faculty faculty = facultyDAO.getFacultyById(section.getInstructorID());

            model.addRow(new Object[]{course.getCode(), course.getTitle(), course.getCredits(), faculty != null ? faculty.getFullName() : "TBA", section.getTerm(), section.getYear()});
        }
    }

    private void dropCourse() {
        int r = table.getSelectedRow();
        if (r == -1 || model.getValueAt(r, 0).toString().equals("No enrollments")) {
            DialogUtils.errorDialog("Please select a course to drop.");
            return;
        }

        LocalDate ddl = studentService.getAddDropDeadline();
        if (ddl != null && LocalDate.now().isAfter(ddl)) {
            DialogUtils.errorDialog("Add/Drop deadline has passed (" + ddl + ").");
            return;
        }

        String courseCode = model.getValueAt(r, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to drop " + courseCode + "?", "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        List<Enrollment> enrollments = studentService.getMyEnrollments(student.getStudentId());

        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section == null) continue;

            Course course = courseDAO.getCourseById(section.getCourseID());
            if (course == null) continue;

            if (course.getCode().equals(courseCode)) {

                boolean ok = studentService.dropSection(student.getStudentId(), e.getSectionId());
                if (ok) {
                    DialogUtils.infoDialog("Successfully dropped " + courseCode + "!");
                    loadEnrollments();
                } else {
                    DialogUtils.errorDialog("Failed to drop course.");
                }
                return;
            }
        }
    }

    public void refresh() {
        loadEnrollments();
    }
}