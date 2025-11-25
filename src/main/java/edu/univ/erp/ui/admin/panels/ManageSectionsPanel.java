package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

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

    private final List<Section> sections = new ArrayList<>();

    public ManageSectionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Sections", "Add, view, and manage course sections"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Course", "Instructor", "Term", "Year", "Room", "Capacity"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Add Section", e -> openCreateDialog()),
                UIUtils.secondaryButton("Edit Section", e -> openEditDialog()),
                UIUtils.secondaryButton("Delete Section", e -> deleteSection())
        );
        add(bottom, BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        model.setRowCount(0);
        sections.clear();

        List<Section> allSections = adminService.getAllSections();
        List<Course> courses = adminService.getAllCourses();
        List<Faculty> facultyList = adminService.getAllFaculty();

        for (Section s : allSections) {
            sections.add(s);

            Course c = courses.stream().filter(a -> a.getCourseID() == s.getCourseID()).findFirst().orElse(null);
            String courseCode = c != null ? c.getCode() : "Unknown";

            String instructorName = "Unassigned";
            if (s.getInstructorID() != 0) {
                Faculty f = facultyList.stream().filter(a -> a.getFacultyId() == s.getInstructorID()).findFirst().orElse(null);
                if (f != null) instructorName = f.getFullName();
            }

            model.addRow(new Object[]{
                    courseCode, instructorName, s.getTerm(), s.getYear(), s.getRoom(), s.getCapacity()
            });
        }
    }

    private void openCreateDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<Course> courses = adminService.getAllCourses();
        JComboBox<String> courseF = new JComboBox<>(courses.stream().map(Course::getCode).toArray(String[]::new));

        List<Faculty> facultys = adminService.getAllFaculty();
        String[] facNames = new String[facultys.size() + 1];
        facNames[0] = "Unassigned";
        for (int i = 0; i < facultys.size(); i++) facNames[i+1] = facultys.get(i).getFullName();
        JComboBox<String> instrF = new JComboBox<>(facNames);

        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon", "Winter", "Summer"});
        JSpinner yearF = new JSpinner(new SpinnerNumberModel(java.time.Year.now().getValue(), 2000, 2100, 1));
        JTextField roomF = new JTextField();
        JSpinner capF = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));

        panel.add(new JLabel("Course:")); panel.add(courseF);
        panel.add(new JLabel("Instructor:")); panel.add(instrF);
        panel.add(new JLabel("Term:")); panel.add(termF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Room:")); panel.add(roomF);
        panel.add(new JLabel("Capacity:")); panel.add(capF);

        if (JOptionPane.showConfirmDialog(this, panel, "Create Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        int courseId = courses.get(courseF.getSelectedIndex()).getCourseID();
        int instructorId = (instrF.getSelectedIndex() == 0) ? 0 : facultys.get(instrF.getSelectedIndex()-1).getFacultyId();

        String term = (String) termF.getSelectedItem();
        int year = (Integer) yearF.getValue();
        String room = roomF.getText().trim();
        int capacity = (Integer) capF.getValue();

        if (room.isEmpty()) {
            DialogUtils.errorDialog("Room cannot be empty.");
            return;
        }

        Section s = new Section(0, courseId, instructorId, term, year, room, capacity);

        if (adminService.createSection(s)) {
            DialogUtils.infoDialog("Section created successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Failed to create section.");
        }
    }

    private void openEditDialog() {
        int r = table.getSelectedRow();
        if (r == -1) { DialogUtils.errorDialog("Select a section first."); return; }

        int id = table.convertRowIndexToModel(r);
        Section s = sections.get(id);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<Course> courses = adminService.getAllCourses();
        JComboBox<String> courseF = new JComboBox<>(courses.stream().map(Course::getCode).toArray(String[]::new));
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseID() == s.getCourseID()) courseF.setSelectedIndex(i);
        }

        List<Faculty> facultys = adminService.getAllFaculty();
        String[] facNames = new String[facultys.size()+1];
        facNames[0] = "Unassigned";
        for (int i = 0; i < facultys.size(); i++) facNames[i+1] = facultys.get(i).getFullName();
        JComboBox<String> instrF = new JComboBox<>(facNames);

        if (s.getInstructorID() == 0) instrF.setSelectedIndex(0);
        else {
            for (int i = 0; i < facultys.size(); i++) {
                if (facultys.get(i).getFacultyId() == s.getInstructorID()) instrF.setSelectedIndex(i+1);
            }
        }

        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon","Winter","Summer"});
        termF.setSelectedItem(s.getTerm());

        JSpinner yearF = new JSpinner(new SpinnerNumberModel(s.getYear(), 2000, 2100, 1));
        JTextField roomF = new JTextField(s.getRoom());
        JSpinner capF = new JSpinner(new SpinnerNumberModel(s.getCapacity(), 1, 1000, 1));

        panel.add(new JLabel("Course:")); panel.add(courseF);
        panel.add(new JLabel("Instructor:")); panel.add(instrF);
        panel.add(new JLabel("Term:")); panel.add(termF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Room:")); panel.add(roomF);
        panel.add(new JLabel("Capacity:")); panel.add(capF);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Section",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        int courseId = courses.get(courseF.getSelectedIndex()).getCourseID();
        int instructorId = (instrF.getSelectedIndex() == 0) ? 0 : facultys.get(instrF.getSelectedIndex()-1).getFacultyId();
        String term = (String) termF.getSelectedItem();
        int year = (Integer) yearF.getValue();
        String room = roomF.getText().trim();
        int capacity = (Integer) capF.getValue();

        if (room.isEmpty()) { DialogUtils.errorDialog("Room cannot be empty."); return; }

        Section updated = new Section(s.getSectionID(), courseId, instructorId, term, year, room, capacity);

        if (adminService.updateSection(updated)) {
            DialogUtils.infoDialog("Section updated successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Failed to update section.");
        }
    }

    private void deleteSection() {
        int r = table.getSelectedRow();
        if (r == -1) { DialogUtils.errorDialog("Select a section first."); return; }

        int id = table.convertRowIndexToModel(r);
        Section s = sections.get(id);

        if (!DialogUtils.confirmDialog("Delete this section? This cannot be undone.")) return;

        if (adminService.deleteSection(s.getSectionID())) {
            DialogUtils.infoDialog("Section deleted successfully!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Cannot delete section. Students may be enrolled.");
        }
    }
}