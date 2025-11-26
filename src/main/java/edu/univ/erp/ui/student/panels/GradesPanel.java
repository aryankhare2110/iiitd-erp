package edu.univ.erp.ui.student.panels;

import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class GradesPanel extends JPanel {

    private final StudentService studentService = new StudentService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final GradesDAO gradesDAO = new GradesDAO();
    private final ComponentScoreDAO componentScoreDAO = new ComponentScoreDAO();
    private final SectionComponentDAO sectionComponentDAO = new SectionComponentDAO();
    private final ComponentTypeDAO componentTypeDAO = new ComponentTypeDAO();

    private final JTable table;
    private final DefaultTableModel model;
    private JLabel sgpaLabel;
    private List<Enrollment> enrollments;

    public GradesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("My Grades", "View your academic performance"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Course Code", "Course Title", "Credits", "Grade", "Grade Points"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel sgpaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sgpaPanel.setBackground(new Color(248, 249, 250));
        sgpaPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        sgpaLabel = new JLabel("SGPA: 0.00");
        sgpaLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 20));
        sgpaLabel.setForeground(new Color(13, 110, 253));

        sgpaPanel.add(sgpaLabel);
        center.add(sgpaPanel, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        add(UIUtils.createButtonRow(UIUtils.primaryButton("View Component Scores", e -> viewComponentScores()), UIUtils.secondaryButton("Download Transcript", e -> downloadTranscript())), BorderLayout.SOUTH);

        loadGrades();
    }

    private void loadGrades() {
        model.setRowCount(0);

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        enrollments = enrollmentDAO.getEnrollmentsByStudent(student.getStudentId());

        if (enrollments.isEmpty()) {
            model.addRow(new Object[]{"No grades available", "", "", "", ""});
            sgpaLabel.setText("SGPA: 0.00");
            return;
        }

        double totalGradePoints = 0;
        int totalCredits = 0;

        for (Enrollment e : enrollments) {

            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section == null) continue;

            Course course = courseDAO.getCourseById(section.getCourseID());
            if (course == null) continue;

            Grade grade = gradesDAO.getGradeByEnrollment(e.getEnrollmentId());
            String gradeLabel = (grade != null) ? grade.getGradeLabel() : "N/A";
            double gradePoint = getGradePoint(gradeLabel);

            model.addRow(new Object[]{
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    gradeLabel,
                    gradeLabel.equals("N/A") ? "N/A" : String.format("%.2f", gradePoint)
            });

            if (!gradeLabel.equals("N/A")) {
                totalGradePoints += gradePoint * course.getCredits();
                totalCredits += course.getCredits();
            }
        }

        double sgpa = (totalCredits > 0) ? (totalGradePoints / totalCredits) : 0.0;
        sgpaLabel.setText("sgpa: " + String.format("%.2f", sgpa));
    }

    private void viewComponentScores() {
        int r = table.getSelectedRow();

        if (r == -1 || model.getValueAt(r, 0).toString().equals("No grades available")) {
            DialogUtils.errorDialog("Please select a course first.");
            return;
        }

        Enrollment enrollment = enrollments.get(r);
        Section section = sectionDAO.getSectionById(enrollment.getSectionId());
        Course course = courseDAO.getCourseById(section.getCourseID());

        if (course == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Component Scores - " + course.getCode(), true);

        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);

        DefaultTableModel scoreModel = new DefaultTableModel(new String[]{"Component", "Weight (%)", "Score", "Weighted Score"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable scoreTable = UIUtils.createStyledTable(scoreModel);

        for (SectionComponent sc : sectionComponentDAO.getComponentsBySection(section.getSectionID())) {

            if (sc.getWeight() == 0) continue;

            String typeName = componentTypeDAO.getComponentTypeName(sc.getTypeID());
            ComponentScore score = componentScoreDAO.getScore(enrollment.getEnrollmentId(), sc.getComponentID());

            double weighted = (score != null) ? (score.getScore() * sc.getWeight() / 100) : 0;

            scoreModel.addRow(new Object[]{typeName + (sc.getDescription() != null ? " - " + sc.getDescription() : ""), sc.getWeight(), score != null ? String.format("%.2f", score.getScore()) : "N/A", score != null ? String.format("%.2f", weighted) : "N/A"});
        }

        dialog.add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(UIUtils.secondaryButton("Close", e -> dialog.dispose()));

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void downloadTranscript() {
        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Transcript_" + student.getRollNo() + ".csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (
                FileWriter writer = new FileWriter(chooser.getSelectedFile());
                CSVPrinter csv = new CSVPrinter(writer,
                        CSVFormat.DEFAULT.withHeader("Course Code", "Course Title", "Component Scores", "Final Score", "Letter Grade"))
        ) {
            for (Enrollment e : enrollments) {

                Section section = sectionDAO.getSectionById(e.getSectionId());
                if (section == null) continue;

                Course course = courseDAO.getCourseById(section.getCourseID());
                if (course == null) continue;

                StringBuilder compStr = new StringBuilder();

                for (SectionComponent sc : sectionComponentDAO.getComponentsBySection(section.getSectionID())) {

                    if (sc.getWeight() == 0) continue;

                    ComponentScore score = componentScoreDAO.getScore(e.getEnrollmentId(), sc.getComponentID());

                    if (score != null) {
                        compStr.append(componentTypeDAO.getComponentTypeName(sc.getTypeID()))
                                .append(": ").append(String.format("%.2f", score.getScore())).append("; ");
                    }
                }

                Grade grade = gradesDAO.getGradeByEnrollment(e.getEnrollmentId());

                csv.printRecord(
                        course.getCode(),
                        course.getTitle(),
                        compStr.length() > 0 ? compStr.toString() : "N/A",
                        grade != null ? String.format("%.2f", grade.getTotalScore()) : "N/A",
                        grade != null ? grade.getGradeLabel() : "N/A"
                );
            }

            DialogUtils.infoDialog("Transcript saved!\n" + chooser.getSelectedFile().getAbsolutePath());

        } catch (Exception ex) {
            DialogUtils.errorDialog("Failed: " + ex.getMessage());
        }
    }

    private double getGradePoint(String grade) {
        switch (grade.toUpperCase()) {
            case "A": return 10;
            case "A-": return 9;
            case "B": return 8;
            case "B-": return 7;
            case "C": return 6;
            case "C-": return 5;
            case "D": return 4;
            case "F": return 0;
        }

        return 0;
    }

    public void refresh() {
        loadGrades();
    }
}