package edu.univ.erp.ui.faculty.panels;

import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.FacultyService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * ScoresPanel runs inside FacultyUI. It provides:
 * - a section selector (faculty's sections)
 * - list of enrolled students
 * - component selector (section components)
 * - ability to save a score for selected student × component
 * - finalize grade for selected student
 *
 * Constructed without parameters so FacultyUI can instantiate it and show it in the cards.
 */
public class ScoresPanel extends JPanel {
    private final FacultyService facultyService = new FacultyService();
    private final SectionComponentDAO componentDAO = new SectionComponentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ComponentScoreDAO scoreDAO = new ComponentScoreDAO();
    private final GradesDAO gradesDAO = new GradesDAO();

    private final JComboBox<String> sectionCombo;
    private final DefaultTableModel studentsModel;
    private final JTable studentsTable;

    private final JComboBox<String> componentCombo;
    private List<SectionComponent> components = new ArrayList<>();

    private final JTextField scoreField;
    private final JLabel currentScoreLabel;

    // Map section ids to Section objects for quick lookup
    private final java.util.List<Section> facultySections = new ArrayList<>();

    public ScoresPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        add(UIUtils.createHeader("Scores", "Enter component scores and finalize grades"), BorderLayout.NORTH);

        // Top: section selector
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(8, 12, 8, 12));
        top.add(new JLabel("Section: "));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(300, 28));
        sectionCombo.addActionListener(e -> onSectionChanged());
        top.add(sectionCombo);

        JButton refreshSectionsBtn = UIUtils.secondaryButton("Refresh Sections", ev -> loadSections());
        top.add(refreshSectionsBtn);
        add(top, BorderLayout.NORTH);

        // Left: students list
        studentsModel = new DefaultTableModel(new String[]{"Roll", "Name", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        studentsTable = UIUtils.createStyledTable(studentsModel);
        JScrollPane leftScroll = new JScrollPane(studentsTable);
        leftScroll.setPreferredSize(new Dimension(420, 400));

        // Right: component selector & score entry
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(new EmptyBorder(12, 12, 12, 12));
        right.setBackground(Color.WHITE);

        right.add(new JLabel("Component:"));
        componentCombo = new JComboBox<>();
        componentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        componentCombo.addActionListener(e -> loadSelectedComponentScore());
        right.add(componentCombo);
        right.add(Box.createVerticalStrut(10));

        currentScoreLabel = new JLabel("Current score: -");
        right.add(currentScoreLabel);
        right.add(Box.createVerticalStrut(8));

        right.add(new JLabel("Enter Score (0-100):"));
        scoreField = new JTextField();
        scoreField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        right.add(scoreField);
        right.add(Box.createVerticalStrut(12));

        JButton saveBtn = UIUtils.primaryButton("Save Score", e -> saveScoreForSelectedStudent());
        JButton finalizeBtn = UIUtils.primaryButton("Finalize Grade", e -> finalizeGradeForSelectedStudent());
        right.add(saveBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(finalizeBtn);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, right);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.secondaryButton("Refresh", e -> refresh()),
                UIUtils.secondaryButton("Clear selection", e -> {
                    studentsTable.clearSelection();
                    componentCombo.setSelectedIndex(-1);
                    currentScoreLabel.setText("Current score: -");
                })
        );
        add(bottom, BorderLayout.SOUTH);

        loadSections();
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        facultySections.clear();
        try {
            Faculty me = facultyService.getMyProfile();
            if (me == null) {
                DialogUtils.errorDialog("Unable to load faculty profile.");
                return;
            }
            List<Section> my = facultyService.mySections(me.getFacultyId());
            for (Section s : my) {
                facultySections.add(s);
                // show "<sectionId> — CODE (Term Year)"
                Course c = new CourseDAO().getCourseById(s.getCourseID());
                String label = String.format("%d — %s (%s %d)", s.getSectionID(), c != null ? c.getCode() : "Unknown", s.getTerm(), s.getYear());
                sectionCombo.addItem(label);
            }
            if (sectionCombo.getItemCount() > 0) sectionCombo.setSelectedIndex(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to load sections: " + ex.getMessage());
        }
    }

    private void onSectionChanged() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx < 0 || idx >= facultySections.size()) return;
        int sectionId = facultySections.get(idx).getSectionID();
        loadStudents(sectionId);
        loadComponents(sectionId);
    }

    private void loadStudents(int sectionId) {
        studentsModel.setRowCount(0);
        try {
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(sectionId);
            for (Enrollment en : enrollments) {
                Student s = studentDAO.getStudentById(en.getStudentId());
                String roll = s != null ? s.getRollNo() : "N/A";
                String name = s != null ? s.getFullName() : "Unknown";
                studentsModel.addRow(new Object[]{en.getEnrollmentId(), roll, name, en.getStatus()});
            }
            if (studentsModel.getRowCount() > 0) studentsTable.setRowSelectionInterval(0, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to load enrolled students: " + ex.getMessage());
        }
    }

    private void loadComponents(int sectionId) {
        components = componentDAO.getComponentsBySection(sectionId);
        componentCombo.removeAllItems();
        for (SectionComponent sc : components) {
            String t = new ComponentTypeDAO().getComponentTypeName(sc.getTypeID());
            String times = (sc.getDay()!=null?sc.getDay():"") + (sc.getStartTime()!=null ? " " + sc.getStartTime() + "-" + sc.getEndTime() : "");
            String desc = sc.getDescription() != null ? sc.getDescription() : "";
            componentCombo.addItem(String.format("%s — %s (id:%d)", t, desc, sc.getComponentID()) + (times.isEmpty() ? "" : " " + times));
        }
        if (componentCombo.getItemCount() > 0) componentCombo.setSelectedIndex(0);
    }

    private void loadSelectedComponentScore() {
        int compIdx = componentCombo.getSelectedIndex();
        if (compIdx < 0 || compIdx >= components.size()) {
            currentScoreLabel.setText("Current score: -");
            return;
        }
        int componentId = components.get(compIdx).getComponentID();
        int sel = studentsTable.getSelectedRow();
        if (sel == -1) { currentScoreLabel.setText("Current score: -"); return; }
        int enrollmentId = (int) studentsModel.getValueAt(studentsTable.convertRowIndexToModel(sel), 0);
        try {
            Double sc = scoreDAO.getScore(enrollmentId, componentId).getScore();
            currentScoreLabel.setText(sc == null ? "Current score: -" : "Current score: " + sc);
        } catch (Exception ex) {
            currentScoreLabel.setText("Current score: -");
        }
    }

    private void saveScoreForSelectedStudent() {
        int sel = studentsTable.getSelectedRow();
        if (sel == -1) { DialogUtils.errorDialog("Select a student first."); return; }
        int enrollmentId = (int) studentsModel.getValueAt(studentsTable.convertRowIndexToModel(sel), 0);

        int compIdx = componentCombo.getSelectedIndex();
        if (compIdx < 0 || compIdx >= components.size()) { DialogUtils.errorDialog("Select a component first."); return; }
        int componentId = components.get(compIdx).getComponentID();

        String txt = scoreField.getText().trim();
        double val;
        try { val = Double.parseDouble(txt); } catch (Exception ex) { DialogUtils.errorDialog("Enter a valid number."); return; }
        if (val < 0 || val > 100) { DialogUtils.errorDialog("Score must be 0-100."); return; }

        ComponentScore cs = new ComponentScore(0, enrollmentId, componentId, val);
        boolean ok = false;
        try {
            ok = scoreDAO.insertScore(cs); // preferred DAO method
        } catch (Throwable ignored) {
        }
        if (!ok) {
            try { ok = facultyService.enterScore(cs); } catch (Throwable ignored) {}
        }
        if (ok) {
            DialogUtils.infoDialog("Score saved.");
            loadSelectedComponentScore();
        } else {
            DialogUtils.errorDialog("Failed to save score. Check DAO implementation.");
        }
    }

    private void finalizeGradeForSelectedStudent() {
        int sel = studentsTable.getSelectedRow();
        if (sel == -1) { DialogUtils.errorDialog("Select a student first."); return; }
        int enrollmentId = (int) studentsModel.getValueAt(studentsTable.convertRowIndexToModel(sel), 0);
        try {
            Double total = gradesDAO.getGradeByEnrollment(enrollmentId).getTotalScore();
            if (total == null) total = 0.0;
            String grade = JOptionPane.showInputDialog(this, "Computed total: " + total + "\nEnter grade label (e.g. A, B+):");
            if (grade == null || grade.trim().isEmpty()) return;
            boolean ok = facultyService.finalizeGrade(enrollmentId, total, grade.trim());
            if (ok) {
                DialogUtils.infoDialog("Grade finalized.");
                refresh();
            } else {
                DialogUtils.errorDialog("Failed to finalize grade.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            DialogUtils.errorDialog("Failed to compute/submit grade: " + ex.getMessage());
        }
    }

    /**
     * Set the current section (useful if FacultyUI wants to programmatically switch to a section)
     */
    public void setSection(int sectionId) {
        for (int i = 0; i < facultySections.size(); i++) {
            if (facultySections.get(i).getSectionID() == sectionId) {
                sectionCombo.setSelectedIndex(i);
                return;
            }
        }
        // if not found, refresh sections and try again
        loadSections();
        for (int i = 0; i < facultySections.size(); i++) {
            if (facultySections.get(i).getSectionID() == sectionId) {
                sectionCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    public void refresh() {
        loadSections();
        // reload students & components for selected section
        onSectionChanged();
    }
}