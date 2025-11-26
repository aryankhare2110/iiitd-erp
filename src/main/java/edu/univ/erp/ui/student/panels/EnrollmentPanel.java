package edu.univ.erp.ui.student. panels;

import edu.univ.erp.dao.CourseDAO;
import edu.univ.erp.dao.FacultyDAO;
import edu. univ.erp.dao. SectionDAO;
import edu. univ.erp.domain.*;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp. ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing. table.DefaultTableModel;
import java.awt.*;
import java. util.List;

public class EnrollmentPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();  // ADD THIS
    private final JTable table;
    private final DefaultTableModel model;

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils. createHeader("My Enrollments", "View and manage your course enrollments"), BorderLayout.NORTH);

        // CHANGED: Replace "Status" with "Instructor"
        model = new DefaultTableModel(new String[]{"Course Code", "Course Title", "Credits", "Instructor", "Term", "Year"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.setBackground(new Color(248, 249, 250));
        center. add(new JScrollPane(table), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Drop Course", e -> dropCourse()),
                UIUtils.secondaryButton("Refresh", e -> loadEnrollments())  // ADD REFRESH BUTTON
        );
        add(bottom, BorderLayout.SOUTH);

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
        } else {
            for (Enrollment enrollment : enrollments) {
                Section section = sectionDAO.getSectionById(enrollment.getSectionId());
                if (section != null) {
                    Course course = courseDAO. getCourseById(section.getCourseID());
                    Faculty faculty = facultyDAO.getFacultyById(section.getInstructorID());  // ADD THIS

                    if (course != null) {
                        String instructorName = (faculty != null) ? faculty.getFullName() : "TBA";  // ADD THIS

                        model.addRow(new Object[]{
                                course. getCode(),
                                course. getTitle(),
                                course. getCredits(),
                                instructorName,  // CHANGED: Show instructor instead of status
                                section.getTerm(),
                                section.getYear()
                        });
                    }
                }
            }
        }
    }

    private void dropCourse() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Please select a course to drop.");
            return;
        }

        String courseCode = model.getValueAt(r, 0).toString();
        if (courseCode. equals("No enrollments")) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to drop " + courseCode + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        List<Enrollment> enrollments = studentService.getMyEnrollments(student.getStudentId());
        Enrollment targetEnrollment = null;

        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section != null) {
                Course course = courseDAO.getCourseById(section.getCourseID());
                if (course != null && course.getCode().equals(courseCode)) {
                    targetEnrollment = e;
                    break;
                }
            }
        }

        if (targetEnrollment != null) {
            boolean success = studentService.dropSection(student.getStudentId(), targetEnrollment. getSectionId());
            if (success) {
                DialogUtils. infoDialog("Course dropped successfully!");
                loadEnrollments();
            } else {
                DialogUtils.errorDialog("Failed to drop course. Check if add/drop deadline has passed.");
            }
        }
    }

    public void refresh() {
        loadEnrollments();
    }
}