package edu.univ.erp.ui. student. panels;

import edu.univ. erp.dao.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java. util.List;
import org.apache.commons.csv. CSVFormat;
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
    private JLabel cgpaLabel;
    private List<Enrollment> enrollments;

    public GradesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("My Grades", "View your academic performance"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Course Code", "Course Title", "Credits", "Grade", "Grade Points"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center. setBackground(new Color(248, 249, 250));
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        // CGPA Display
        JPanel cgpaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cgpaPanel.setBackground(new Color(248, 249, 250));
        cgpaPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel cgpaLabelText = new JLabel("CGPA: ");
        cgpaLabelText. setFont(new Font("Helvetica Neue", Font.BOLD, 18));

        cgpaLabel = new JLabel("0.00");
        cgpaLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        cgpaLabel.setForeground(new Color(13, 110, 253));

        cgpaPanel.add(cgpaLabelText);
        cgpaPanel.add(cgpaLabel);

        center.add(cgpaPanel, BorderLayout.SOUTH);
        add(center, BorderLayout. CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("View Component Scores", e -> viewComponentScores()),
                UIUtils.secondaryButton("Download Transcript", e -> downloadTranscript()),
                UIUtils.secondaryButton("Refresh", e -> loadGrades())
        );
        add(bottom, BorderLayout.SOUTH);

        loadGrades();
    }

    private void loadGrades() {
        model.setRowCount(0);

        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        enrollments = enrollmentDAO. getEnrollmentsByStudent(student.getStudentId());

        if (enrollments.isEmpty()) {
            model.addRow(new Object[]{"No grades available", "", "", "", ""});
            cgpaLabel.setText("0.00");
        } else {
            double totalGradePoints = 0;
            int totalCredits = 0;

            for (Enrollment enrollment : enrollments) {
                Section section = sectionDAO.getSectionById(enrollment.getSectionId());
                if (section == null) continue;

                Course course = courseDAO. getCourseById(section.getCourseID());
                if (course == null) continue;

                Grade grade = gradesDAO.getGradeByEnrollment(enrollment. getEnrollmentId());

                String gradeLabel = (grade != null) ? grade.getGradeLabel() : "N/A";
                double gradePoint = getGradePoint(gradeLabel);
                int credits = course.getCredits();

                model.addRow(new Object[]{
                        course.getCode(),
                        course.getTitle(),
                        credits,
                        gradeLabel,
                        gradeLabel. equals("N/A") ? "N/A" : String.format("%.2f", gradePoint)
                });

                if (!gradeLabel.equals("N/A")) {
                    totalGradePoints += gradePoint * credits;
                    totalCredits += credits;
                }
            }

            // Calculate CGPA
            double cgpa = (totalCredits > 0) ? (totalGradePoints / totalCredits) : 0.0;
            cgpaLabel.setText(String.format("%. 2f", cgpa));
        }
    }

    private void viewComponentScores() {
        int r = table.getSelectedRow();
        if (r == -1 || model.getValueAt(r, 0). toString().equals("No grades available")) {
            DialogUtils.errorDialog("Please select a course first.");
            return;
        }

        Enrollment enrollment = enrollments.get(r);
        Section section = sectionDAO.getSectionById(enrollment.getSectionId());
        Course course = courseDAO.getCourseById(section.getCourseID());

        if (course == null) return;

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Component Scores - " + course.getCode(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);

        // Table for component scores
        DefaultTableModel scoreModel = new DefaultTableModel(
                new String[]{"Component", "Weight (%)", "Score", "Weighted Score"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable scoreTable = UIUtils.createStyledTable(scoreModel);

        // Load component scores
        List<SectionComponent> components = sectionComponentDAO.getComponentsBySection(section. getSectionID());
        double totalWeightedScore = 0;
        double totalWeight = 0;

        for (SectionComponent sc : components) {
            if (sc.getWeight() == 0) continue; // Skip non-grading components (LECTURE, etc.)

            String typeName = componentTypeDAO.getComponentTypeName(sc.getTypeID());
            ComponentScore score = componentScoreDAO.getScore(
                    enrollment.getEnrollmentId(), sc.getComponentID());

            String scoreStr = (score != null) ? String.format("%.2f", score.getScore()) : "N/A";
            double weightedScore = (score != null) ? (score.getScore() * sc.getWeight() / 100) : 0;

            scoreModel.addRow(new Object[]{
                    typeName + (sc.getDescription() != null ? " - " + sc.getDescription() : ""),
                    String.format("%.0f", sc.getWeight()),
                    scoreStr,
                    score != null ? String.format("%.2f", weightedScore) : "N/A"
            });

            if (score != null) {
                totalWeightedScore += weightedScore;
                totalWeight += sc.getWeight();
            }
        }

        // Add total row
        scoreModel.addRow(new Object[]{"", "", "", ""});
        scoreModel.addRow(new Object[]{
                "TOTAL",
                String.format("%.0f", totalWeight),
                "",
                String.format("%.2f", totalWeightedScore)
        });

        // Get final grade
        Grade finalGrade = gradesDAO. getGradeByEnrollment(enrollment.getEnrollmentId());
        if (finalGrade != null) {
            scoreModel.addRow(new Object[]{
                    "FINAL GRADE",
                    "",
                    finalGrade.getGradeLabel(),
                    String.format("%.2f", finalGrade. getTotalScore())
            });
        }

        dialog.add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(UIUtils.secondaryButton("Close", e -> dialog.dispose()));
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog. setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void downloadTranscript() {
        Student student = studentService.getMyProfile();
        if (student == null) {
            DialogUtils.errorDialog("Unable to load student profile.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new File("Transcript_" + student.getRollNo() + ".csv"));

        if (fileChooser. showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try (FileWriter writer = new FileWriter(file);
             CSVPrinter csvPrinter = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader("Course Code", "Course Title", "Component Scores", "Final Score", "Letter Grade"))) {

            for (Enrollment enrollment : enrollments) {
                Section section = sectionDAO.getSectionById(enrollment.getSectionId());
                if (section == null) continue;

                Course course = courseDAO.getCourseById(section.getCourseID());
                if (course == null) continue;

                // Get component scores
                List<SectionComponent> components = sectionComponentDAO.getComponentsBySection(section.getSectionID());
                StringBuilder componentScores = new StringBuilder();

                for (SectionComponent sc : components) {
                    if (sc.getWeight() == 0) continue; // Skip non-grading components

                    String typeName = componentTypeDAO.getComponentTypeName(sc.getTypeID());
                    ComponentScore score = componentScoreDAO.getScore(
                            enrollment.getEnrollmentId(), sc.getComponentID());

                    if (score != null) {
                        componentScores.append(typeName). append(": ").append(String.format("%.2f", score.getScore())).append("; ");
                    }
                }

                // Get final grade
                Grade finalGrade = gradesDAO.getGradeByEnrollment(enrollment.getEnrollmentId());
                String finalScore = (finalGrade != null) ? String.format("%.2f", finalGrade.getTotalScore()) : "N/A";
                String letterGrade = (finalGrade != null) ? finalGrade.getGradeLabel() : "N/A";

                csvPrinter.printRecord(
                        course.getCode(),
                        course.getTitle(),
                        componentScores.length() > 0 ? componentScores.toString() : "N/A",
                        finalScore,
                        letterGrade
                );
            }

            DialogUtils.infoDialog("Transcript saved successfully!\n" + file.getAbsolutePath());

        } catch (Exception e) {
            DialogUtils.errorDialog("Failed to save transcript: " + e. getMessage());
        }
    }

    private double getGradePoint(String grade) {
        if (grade == null) return 0.0;
        switch (grade.toUpperCase()) {
            case "A": return 10.0;
            case "A-": return 9.0;
            case "B": return 8.0;
            case "B-": return 7.0;
            case "C": return 6.0;
            case "C-": return 5.0;
            case "D": return 4.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }

    public void refresh() {
        loadGrades();
    }
}