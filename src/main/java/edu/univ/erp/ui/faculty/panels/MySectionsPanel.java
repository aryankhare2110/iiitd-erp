package edu.univ.erp.ui.faculty.panels;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;
import edu.univ.erp.dao.CourseDAO;
import edu.univ.erp.dao.EnrollmentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MySectionsPanel extends JPanel {
    private final FacultyService facultyService = new FacultyService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    private final DefaultTableModel model;
    private final JTable table;
    private final List<Section> sections = new ArrayList<>();

    public MySectionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("My Sections", "Sections you are teaching"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Course Code", "Course Title", "Term", "Year", "Room", "Capacity", "Enrolled"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 40, 10, 40));
        center.setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        add(UIUtils.createButtonRow(), BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        model.setRowCount(0);
        sections.clear();
        try {
            Faculty me = facultyService.getMyProfile();
            if (me == null) {
                DialogUtils.errorDialog("Unable to load faculty profile.");
                return;
            }
            List<Section> my = facultyService.mySections(me.getFacultyId());
            for (Section s : my) {
                sections.add(s);
                Course c = courseDAO.getCourseById(s.getCourseID());
                String code = c != null ? c.getCode() : "Unknown";
                String title = c != null ? c.getTitle() : "Unknown";
                int enrolled = enrollmentDAO.getEnrollmentsBySection(s.getSectionID()).size();
                model.addRow(new Object[]{
                        code, title, s.getTerm(), s.getYear(), s.getRoom(), s.getCapacity(), enrolled
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to load sections: " + ex.getMessage());
        }
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public void refresh() {
        loadSections();
    }
}