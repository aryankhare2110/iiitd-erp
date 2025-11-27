package edu.univ.erp.ui. admin. panels;

import edu.univ.erp.dao.ComponentTypeDAO;
import edu.univ.erp.dao.SectionComponentDAO;
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
    private final SectionComponentDAO sectionComponentDAO = new SectionComponentDAO();
    private final ComponentTypeDAO componentTypeDAO = new ComponentTypeDAO();
    private final JTable table;
    private final DefaultTableModel model;
    private final List<Section> sectionList = new ArrayList<>();

    public ManageSectionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Sections", "Add, view and manage sections"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Course", "Instructor", "Term", "Year", "Room", "Capacity"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center. setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout. CENTER);

        JPanel bottom = UIUtils.createButtonRow(UIUtils.primaryButton("Add Section", e -> openCreateDialog()), UIUtils.primaryButton("Edit Section", e -> openEditDialog()), UIUtils.primaryButton("Manage Schedule", e -> openScheduleDialog()), UIUtils.secondaryButton("Delete Section", e -> deleteSection()));
        add(bottom, BorderLayout. SOUTH);

        loadSections();
    }

    private void loadSections() {
        model. setRowCount(0);
        sectionList.clear();

        List<Section> sections = adminService.getAllSections();
        List<Course> courses = adminService. getAllCourses();
        List<Faculty> facultyList = adminService.getAllFaculty();

        for (Section s : sections) {
            sectionList.add(s);
            Course course = courses.stream(). filter(c -> c.getCourseID() == s.getCourseID()).findFirst().orElse(null);
            Faculty faculty = facultyList.stream().filter(f -> f.getFacultyId() == s.getInstructorID()).findFirst().orElse(null);
            model.addRow(new Object[]{
                    course != null ? course. getCode() : "Unknown",
                    faculty != null ? faculty.getFullName() : "Unknown",
                    s.getTerm(), s.getYear(), s.getRoom(), s.getCapacity()
            });
        }
    }

    private void openCreateDialog() {
        List<Course> courses = adminService. getAllCourses();
        List<Faculty> faculties = adminService.getAllFaculty();

        if (courses.isEmpty() || faculties.isEmpty()) {
            DialogUtils.errorDialog("No courses or faculty available.");
            return;
        }

        JPanel panel = buildFormPanel(courses, faculties, null);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Section",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        int courseId = courses.get(((JComboBox<? >) panel.getComponent(1)). getSelectedIndex()).getCourseID();
        int facultyId = faculties.get(((JComboBox<?>) panel.getComponent(3)).getSelectedIndex()).getFacultyId();
        String term = (String) ((JComboBox<?>) panel. getComponent(5)).getSelectedItem();
        int year = Integer. parseInt(((JTextField) panel.getComponent(7)).getText().trim());
        String room = ((JTextField) panel.getComponent(9)).getText().trim();
        int capacity = (Integer) ((JSpinner) panel.getComponent(11)).getValue();

        if (adminService.createSection(new Section(0, courseId, facultyId, term, year, room, capacity))) {
            DialogUtils.infoDialog("Section created!");
            loadSections();
        } else {
            DialogUtils. errorDialog("Failed to create section.");
        }
    }

    private void openEditDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a section first.");
            return;
        }

        Section s = sectionList.get(table.convertRowIndexToModel(r));
        List<Course> courses = adminService. getAllCourses();
        List<Faculty> faculties = adminService.getAllFaculty();

        JPanel panel = buildFormPanel(courses, faculties, s);

        if (JOptionPane.showConfirmDialog(this, panel, "Edit Section",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;

        int courseId = courses.get(((JComboBox<?>) panel. getComponent(1)).getSelectedIndex()).getCourseID();
        int facultyId = faculties. get(((JComboBox<? >) panel.getComponent(3)). getSelectedIndex()).getFacultyId();
        String term = (String) ((JComboBox<?>) panel.getComponent(5)).getSelectedItem();
        int year = Integer.parseInt(((JTextField) panel.getComponent(7)).getText().trim());
        String room = ((JTextField) panel.getComponent(9)).getText().trim();
        int capacity = (Integer) ((JSpinner) panel.getComponent(11)).getValue();

        if (adminService.updateSection(new Section(s.getSectionID(), courseId, facultyId, term, year, room, capacity))) {
            DialogUtils.infoDialog("Section updated!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Failed to update section.");
        }
    }

    private void openScheduleDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a section first.");
            return;
        }

        Section section = sectionList.get(table.convertRowIndexToModel(r));
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule - " + model.getValueAt(r, 0), true);
        dialog. setLayout(new BorderLayout());
        dialog.setSize(700, 500);

        DefaultTableModel scheduleModel = new DefaultTableModel(
                new String[]{"Type", "Day", "Time", "Description"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable scheduleTable = UIUtils.createStyledTable(scheduleModel);

        Runnable reload = () -> {
            scheduleModel.setRowCount(0);
            for (SectionComponent sc : sectionComponentDAO.getComponentsBySection(section.getSectionID())) {
                String type = componentTypeDAO.getComponentTypeName(sc.getTypeID());
                if (type.equals("LECTURE") || type.equals("TUTORIAL") || type.equals("LAB")) {
                    scheduleModel.addRow(new Object[]{type, sc.getDay(),
                            sc.getStartTime() + " - " + sc.getEndTime(), sc.getDescription()});
                }
            }
        };
        reload.run();

        dialog.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(UIUtils.primaryButton("Add Class", e -> {
            if (addScheduleComponent(section. getSectionID())) reload.run();
        }));
        btnPanel.add(UIUtils.secondaryButton("Delete", e -> {
            int row = scheduleTable.getSelectedRow();
            if (row == -1) {
                DialogUtils.errorDialog("Select a row.");
                return;
            }
            List<SectionComponent> comps = sectionComponentDAO.getComponentsBySection(section.getSectionID());
            int count = 0;
            for (SectionComponent sc : comps) {
                String type = componentTypeDAO.getComponentTypeName(sc.getTypeID());
                if ((type.equals("LECTURE") || type.equals("TUTORIAL") || type.equals("LAB")) && count++ == row) {
                    sectionComponentDAO.deleteComponent(sc.getComponentID());
                    reload.run();
                    DialogUtils.infoDialog("Deleted!");
                    return;
                }
            }
        }));
        btnPanel.add(UIUtils.secondaryButton("Close", e -> dialog.dispose()));

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean addScheduleComponent(int sectionId) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"LECTURE", "TUTORIAL", "LAB"});
        JComboBox<String> dayCombo = new JComboBox<>(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"});
        JTextField startTime = new JTextField("09:00");
        JTextField endTime = new JTextField("10:30");
        JTextField description = new JTextField();

        panel.add(new JLabel("Type:")); panel.add(typeCombo);
        panel.add(new JLabel("Day:")); panel.add(dayCombo);
        panel.add(new JLabel("Start:")); panel.add(startTime);
        panel.add(new JLabel("End:")); panel.add(endTime);
        panel. add(new JLabel("Description:")); panel.add(description);

        if (JOptionPane.showConfirmDialog(this, panel, "Add Class", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return false;

        String type = (String) typeCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        String start = startTime.getText().trim();
        String end = endTime. getText().trim();

        if (! start.matches("\\d{2}:\\d{2}") || !end.matches("\\d{2}:\\d{2}")) {
            DialogUtils.errorDialog("Time must be HH:MM format.");
            return false;
        }

        SectionComponent component = new SectionComponent(0, sectionId,
                componentTypeDAO.getComponentTypeIdByName(type), day, start, end, 0.0, description. getText().trim());

        if (sectionComponentDAO. insertComponent(component)) {
            DialogUtils.infoDialog("Added!");
            return true;
        }

        DialogUtils.errorDialog("Failed.");
        return false;
    }

    private void deleteSection() {
        int r = table.getSelectedRow();
        if (r == -1 || !DialogUtils.confirmDialog("Delete this section?")) return;

        if (adminService.deleteSection(sectionList.get(table.convertRowIndexToModel(r)).getSectionID())) {
            DialogUtils.infoDialog("Deleted!");
            loadSections();
        } else {
            DialogUtils.errorDialog("Cannot delete.  Students may be enrolled.");
        }
    }

    private JPanel buildFormPanel(List<Course> courses, List<Faculty> faculties, Section existing) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> courseF = new JComboBox<>(courses.stream().map(Course::getCode).toArray(String[]::new));
        JComboBox<String> instrF = new JComboBox<>(faculties.stream().map(Faculty::getFullName).toArray(String[]::new));
        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon", "Winter", "Summer"});
        JTextField yearF = new JTextField(String.valueOf(java.time.Year.now().getValue()));
        JTextField roomF = new JTextField();
        JSpinner capF = new JSpinner(new SpinnerNumberModel(30, 1, 1000, 1));

        if (existing != null) {
            for (int i = 0; i < courses.size(); i++)
                if (courses.get(i).getCourseID() == existing.getCourseID()) courseF.setSelectedIndex(i);
            for (int i = 0; i < faculties.size(); i++)
                if (faculties.get(i).getFacultyId() == existing.getInstructorID()) instrF.setSelectedIndex(i);
            termF.setSelectedItem(existing.getTerm());
            yearF. setText(String.valueOf(existing. getYear()));
            roomF. setText(existing.getRoom());
            capF.setValue(existing.getCapacity());
        }

        panel.add(new JLabel("Course:")); panel.add(courseF);
        panel.add(new JLabel("Instructor:")); panel.add(instrF);
        panel.add(new JLabel("Term:")); panel.add(termF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Room:")); panel.add(roomF);
        panel.add(new JLabel("Capacity:")); panel.add(capF);

        return panel;
    }

    public void refresh() {
        loadSections();
    }
}