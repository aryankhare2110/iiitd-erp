package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageSectionsPanel extends JPanel {

    private final AdminService adminService = new AdminService();

    private final JTable table;
    private final DefaultTableModel model;

    private final List<Section> sectionList = new ArrayList<>();

    public ManageSectionsPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader(
                "Manage Sections",
                "Create, update and remove course sections"
        ), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Course", "Instructor", "Term", "Year", "Room", "Capacity"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Add Section", e -> openCreateDialog()),
                UIUtils.primaryButton("Edit Section", e -> openEditDialog()),
                UIUtils.secondaryButton("Delete Section", e -> deleteSection())
        );
        add(bottom, BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        model.setRowCount(0);
        sectionList.clear();

        List<Section> sections = adminService.getAllSections();
        List<Course> courses = adminService.getAllCourses();
        List<Faculty> facultyList = adminService.getAllFaculty();

        for (Section s : sections) {
            sectionList.add(s);

            Course course = courses.stream()
                    .filter(c -> c.getCourseID() == s.getCourseID())
                    .findFirst().orElse(null);

            Faculty faculty = facultyList.stream()
                    .filter(f -> f.getFacultyId() == s.getInstructorID())
                    .findFirst().orElse(null);

            String courseCode = (course != null) ? course.getCode() : "Unknown";
            String instructor = (faculty != null) ? faculty.getFullName() : "Unknown";

            model.addRow(new Object[]{
                    courseCode, instructor, s.getTerm(), s.getYear(),
                    s.getRoom(), s.getCapacity()
            });
        }
    }

    private void openCreateDialog() {
        List<Course> courses = adminService.getAllCourses();
        List<Faculty> faculties = adminService.getAllFaculty();

        if (courses.isEmpty()) {
            DialogUtils.errorDialog("No courses available.");
            return;
        }

        if (faculties.isEmpty()) {
            DialogUtils.errorDialog("No faculty available.");
            return;
        }

        JPanel panel = buildFormPanel(courses, faculties, null);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
            return;

        FormData data = extractFormData(panel, courses, faculties);
        if (data == null) return;

        Section s = new Section(0, data.courseId, data.facultyId, data.term, data.year, data.room, data.capacity);
        boolean ok = adminService.createSection(s);

        if (ok) {
            DialogUtils.infoDialog("Section created successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Failed to create section.");
        }
    }

    private void openEditDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a section first.");
            return;
        }

        int idx = table.convertRowIndexToModel(r);
        Section s = sectionList.get(idx);

        List<Course> courses = adminService.getAllCourses();
        List<Faculty> faculties = adminService.getAllFaculty();

        JPanel panel = buildFormPanel(courses, faculties, s);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
            return;

        FormData data = extractFormData(panel, courses, faculties);
        if (data == null) return;

        Section updated = new Section(
                s.getSectionID(), data.courseId, data.facultyId,
                data.term, data.year, data.room, data.capacity
        );

        boolean ok = adminService.updateSection(updated);
        if (ok) {
            DialogUtils.infoDialog("Section updated successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Failed to update section.");
        }
    }

    private void deleteSection() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a section first.");
            return;
        }

        int idx = table.convertRowIndexToModel(r);
        Section s = sectionList.get(idx);

        if (!DialogUtils.confirmDialog("Delete this section? This action cannot be undone.")) {
            return;
        }

        if (adminService.deleteSection(s.getSectionID())) {
            DialogUtils.infoDialog("Section deleted successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Cannot delete section. Students may be enrolled.");
        }
    }


    private static class FormData {
        int courseId, facultyId, year, capacity;
        String term, room;

        FormData(int c, int f, String t, int y, String r, int cap) {
            courseId = c; facultyId = f; term = t; year = y; room = r; capacity = cap;
        }
    }

    private JPanel buildFormPanel(List<Course> courses, List<Faculty> faculties, Section existing) {

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> courseF = new JComboBox<>(courses.stream().map(Course::getCode).toArray(String[]::new));
        JComboBox<String> instrF = new JComboBox<>(faculties.stream().map(Faculty::getFullName).toArray(String[]::new));
        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon", "Winter", "Summer"});

        JTextField yearF = new JTextField();
        JTextField roomF = new JTextField();
        JSpinner capF = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));

        if (existing != null) {
            for (int i = 0; i < courses.size(); i++)
                if (courses.get(i).getCourseID() == existing.getCourseID()) courseF.setSelectedIndex(i);

            for (int i = 0; i < faculties.size(); i++)
                if (faculties.get(i).getFacultyId() == existing.getInstructorID()) instrF.setSelectedIndex(i);

            termF.setSelectedItem(existing.getTerm());
            yearF.setText(String.valueOf(existing.getYear()));
            roomF.setText(existing.getRoom());
            capF.setValue(existing.getCapacity());
        } else {
            yearF.setText(String.valueOf(java.time.Year.now().getValue()));
        }

        panel.add(new JLabel("Course:")); panel.add(courseF);
        panel.add(new JLabel("Instructor:")); panel.add(instrF);
        panel.add(new JLabel("Term:")); panel.add(termF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Room:")); panel.add(roomF);
        panel.add(new JLabel("Capacity:")); panel.add(capF);

        return panel;
    }

    private FormData extractFormData(JPanel panel, List<Course> courses, List<Faculty> faculties) {
        Component[] comps = panel.getComponents();

        JComboBox<?> courseF = (JComboBox<?>) comps[1];
        JComboBox<?> instrF = (JComboBox<?>) comps[3];
        JComboBox<?> termF = (JComboBox<?>) comps[5];
        JTextField yearF = (JTextField) comps[7];
        JTextField roomF = (JTextField) comps[9];
        JSpinner capF = (JSpinner) comps[11];

        int courseId = courses.get(courseF.getSelectedIndex()).getCourseID();
        int facultyId = faculties.get(instrF.getSelectedIndex()).getFacultyId();
        String term = (String) termF.getSelectedItem();
        String yearStr = yearF.getText().trim();
        String room = roomF.getText().trim();
        int cap = (Integer) capF.getValue();

        if (!yearStr.matches("\\d{4}")) {
            DialogUtils.errorDialog("Year must be a valid 4-digit number.");
            return null;
        }

        if (room.isEmpty()) {
            DialogUtils.errorDialog("Room cannot be empty.");
            return null;
        }

        return new FormData(courseId, facultyId, term, Integer.parseInt(yearStr), room, cap);
    }
}