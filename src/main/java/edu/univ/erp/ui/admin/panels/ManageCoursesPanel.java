package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.dao.CourseDAO;
import edu.univ.erp.dao.DepartmentDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {

    private final CourseDAO courseDAO = new CourseDAO();
    private final DepartmentDAO deptDAO = new DepartmentDAO();
    private final AdminService adminService;

    private final JTable table;
    private final DefaultTableModel model;

    public ManageCoursesPanel(AdminService adminService) {
        this.adminService = adminService;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Courses", "Add, view and manage courses"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Code", "Title", "Department", "Credits", "Prerequisites"}, 0) {
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

        JPanel bottom = UIUtils.createButtonRow(UIUtils.primaryButton("Add Course", e -> openCreateDialog()), UIUtils.primaryButton("Edit Course", e -> openEditDialog()), UIUtils.secondaryButton("Delete Course", e -> deleteCourse()));
        add(bottom, BorderLayout.SOUTH);

        loadCourses();
    }

    private void loadCourses() {
        model.setRowCount(0);
        List<Course> courses = courseDAO.getAllCourses();

        for (Course c : courses) {
            model.addRow(new Object[]{c.getCode(), c.getTitle(), deptDAO.getDepartmentNameById(c.getDepartmentID()), c.getCredits(), c.getPrerequisites() == null ? "-" : c.getPrerequisites()
            });
        }
    }

    private void openCreateDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField codeF = new JTextField();
        JTextField titleF = new JTextField();
        JTextField creditsF = new JTextField();
        JTextField prereqF = new JTextField();

        JComboBox<String> deptF = new JComboBox<>(deptDAO.getAllDepartmentNames().toArray(new String[0]));

        panel.add(new JLabel("Course Code:"));
        panel.add(codeF);
        panel.add(new JLabel("Title:"));
        panel.add(titleF);
        panel.add(new JLabel("Credits:"));
        panel.add(creditsF);
        panel.add(new JLabel("Department:"));
        panel.add(deptF);
        panel.add(new JLabel("Prerequisites:"));
        panel.add(prereqF);

        if (JOptionPane.showConfirmDialog(this, panel, "Create Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String code = codeF.getText().trim();
        String title = titleF.getText().trim();
        String credsStr = creditsF.getText().trim();
        String prereq = prereqF.getText().trim();
        int deptId = deptDAO.getDepartmentIdByName((String) deptF.getSelectedItem());

        if (code.isEmpty() || title.isEmpty() || credsStr.isEmpty()) {
            DialogUtils.errorDialog("Code, Title and Credits are required.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(credsStr);
            if (credits < 1) throw new Exception();
        } catch (Exception ex) {
            DialogUtils.errorDialog("Credits must be a positive number.");
            return;
        }

        Course course = new Course(0, deptId, code, title, credits, prereq.isEmpty() ? null : prereq);

        if (adminService.createCourse(course)) {
            DialogUtils.infoDialog("Course created successfully!");
            loadCourses();
        } else {
            DialogUtils.errorDialog("Failed to create course. Code already exists?");
        }
    }

    private void openEditDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a course first.");
            return;
        }

        String code = model.getValueAt(r, 0).toString();
        Course existing = courseDAO.getCourseByCode(code);

        if (existing == null) {
            DialogUtils.errorDialog("Course not found.");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField codeF = new JTextField(existing.getCode());
        JTextField titleF = new JTextField(existing.getTitle());
        JTextField creditsF = new JTextField(String.valueOf(existing.getCredits()));
        JTextField prereqF = new JTextField(existing.getPrerequisites() == null ? "" : existing.getPrerequisites());

        JComboBox<String> deptF = new JComboBox<>(deptDAO.getAllDepartmentNames().toArray(new String[0]));
        deptF.setSelectedItem(deptDAO.getDepartmentNameById(existing.getDepartmentID()));

        panel.add(new JLabel("Course Code:"));    panel.add(codeF);
        panel.add(new JLabel("Title:"));    panel.add(titleF);
        panel.add(new JLabel("Credits:"));    panel.add(creditsF);
        panel.add(new JLabel("Department:"));   panel.add(deptF);
        panel.add(new JLabel("Prerequisites:"));    panel.add(prereqF);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String newCode = codeF.getText().trim();
        String title = titleF.getText().trim();
        String credsStr = creditsF.getText().trim();
        String prereq = prereqF.getText().trim();
        int deptId = deptDAO.getDepartmentIdByName((String) deptF.getSelectedItem());

        if (newCode.isEmpty() || title.isEmpty() || credsStr.isEmpty()) {
            DialogUtils.errorDialog("Code, Title, and Credits are required.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(credsStr);
            if (credits < 1) throw new Exception();
        } catch (Exception ex) {
            DialogUtils.errorDialog("Credits must be a positive number.");
            return;
        }

        Course updated = new Course(existing.getCourseID(), deptId, newCode, title, credits, prereq);

        if (adminService.updateCourse(updated)) {
            DialogUtils.infoDialog("Course updated successfully!");
            loadCourses();
        } else {
            DialogUtils.errorDialog("Failed to update course.");
        }
    }

    private void deleteCourse() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a course first.");
            return;
        }

        String code = model.getValueAt(r, 0).toString();
        Course existing = courseDAO.getCourseByCode(code);
        if (existing == null) {
            DialogUtils.errorDialog("Course not found.");
            return;
        }

        if (!DialogUtils.confirmDialog("Delete course " + existing.getCode() + "?\nThis cannot be undone.")) {
            return;
        }

        if (!adminService.deleteCourse(existing.getCourseID())) {
            DialogUtils.errorDialog("Cannot delete course because enrollments/sections exist.");
            return;
        }

        DialogUtils.infoDialog("Course deleted successfully!");
        loadCourses();
    }

    public void refresh() {
        loadCourses();
    }
}