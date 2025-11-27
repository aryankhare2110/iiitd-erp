package edu.univ.erp.ui. faculty. panels;

import edu.univ. erp.domain.*;
import edu.univ.erp.service.FacultyService;
import edu.univ. erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java. util. HashMap;
import java.util.List;
import java.util.Map;

public class GradingPanel extends JPanel {

    private final FacultyService facultyService = new FacultyService();
    private Faculty faculty;

    private JComboBox<String> sectionCombo;
    private List<Section> sections;

    private JTable studentsTable;
    private DefaultTableModel studentsModel;

    private JButton uploadButton;
    private JButton enterScoresButton;
    private JButton finalizeButton;

    public GradingPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        faculty = facultyService.getMyProfile();

        add(UIUtils.createHeaderWithBadge("Enter Scores & Finalize Grades",
                        "Enter component scores for students and calculate final grades",
                        facultyService.isMaintenanceMode(),
                        " ⚠ MAINTENANCE MODE "),
                BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Section Selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(new Color(248, 249, 250));
        selectorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        selectorPanel.add(UIUtils.makeLabel("Select Section:", true));
        sectionCombo = new JComboBox<>();
        sectionCombo. setPreferredSize(new Dimension(400, 30));
        sectionCombo.addActionListener(e -> loadStudents());
        selectorPanel.add(sectionCombo);

        mainPanel.add(selectorPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        studentsModel = new DefaultTableModel(
                new String[]{"Roll No", "Student Name", "Component Scores", "Total Score", "Grade"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        studentsTable = UIUtils.createStyledTable(studentsModel);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel. setBackground(new Color(248, 249, 250));
        tablePanel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        mainPanel.add(tablePanel);
        add(mainPanel, BorderLayout.CENTER);

        uploadButton = UIUtils.primaryButton("Upload CSV", e -> uploadCSV());
        enterScoresButton = UIUtils.primaryButton("Enter Scores (Manual)", e -> enterScores());
        finalizeButton = UIUtils. primaryButton("Finalize Grades", e -> finalizeGrades());

        add(UIUtils.createButtonRow(
                uploadButton,
                enterScoresButton,
                finalizeButton,
                UIUtils.secondaryButton("Download Template", e -> downloadTemplate())
        ), BorderLayout.SOUTH);

        loadSections();
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean maintenanceMode = facultyService. isMaintenanceMode();
        uploadButton.setEnabled(!maintenanceMode);
        enterScoresButton.setEnabled(!maintenanceMode);
        finalizeButton. setEnabled(!maintenanceMode);

        if (maintenanceMode) {
            sectionCombo.setEnabled(false);
        }
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        if (faculty == null) return;

        sections = facultyService.getMySections();
        for (Section s : sections) {
            Course c = facultyService.getCourseById(s.getCourseID());
            if (c != null) {
                sectionCombo.addItem(c.getCode() + " - " + s.getTerm() + " " + s.getYear());
            }
        }

        if (! sections.isEmpty()) {
            loadStudents();
        }
    }

    private void loadStudents() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1 || sections.isEmpty()) return;

        Section section = sections. get(idx);
        studentsModel.setRowCount(0);

        List<Enrollment> enrollments = facultyService.getEnrolledStudents(section.getSectionID());
        List<SectionComponent> components = facultyService.getComponents(section. getSectionID());

        for (Enrollment e : enrollments) {
            Student student = facultyService.getStudentById(e. getStudentId());
            if (student == null) continue;

            StringBuilder scoreStr = new StringBuilder();

            for (SectionComponent sc : components) {
                ComponentScore score = facultyService.getScore(e.getEnrollmentId(), sc.getComponentID());
                String typeName = facultyService.getComponentTypeName(sc.getTypeID());

                if (score != null) {
                    scoreStr.append(typeName). append(": ")
                            .append(String. format("%.1f", score. getScore())).append(" | ");
                } else {
                    scoreStr. append(typeName).append(": - | ");
                }
            }

            Grade grade = facultyService.getGrade(e.getEnrollmentId());
            String gradeLabel = (grade != null) ? grade.getGradeLabel() : "-";
            double finalScore = (grade != null) ? grade.getTotalScore() : 0;

            studentsModel.addRow(new Object[]{
                    student. getRollNo(),
                    student.getFullName(),
                    scoreStr.length() > 0 ? scoreStr.toString() : "-",
                    finalScore > 0 ? String.format("%.2f", finalScore) : "-",
                    gradeLabel
            });
        }

        if (enrollments.isEmpty()) {
            studentsModel.addRow(new Object[]{"No students enrolled", "", "", "", ""});
        }
    }

    private void uploadCSV() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot upload scores during maintenance mode.");
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1) {
            DialogUtils.errorDialog("Please select a section first.");
            return;
        }

        Section section = sections.get(idx);
        List<SectionComponent> components = facultyService. getComponents(section.getSectionID());

        if (components.isEmpty()) {
            DialogUtils.errorDialog("No components defined for this section. Please add components first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select CSV File to Upload");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try {
            int recordsProcessed = parseAndUploadCSV(file, section.getSectionID(), components);
            DialogUtils.successDialog("CSV uploaded successfully!\nRecords processed: " + recordsProcessed);
            loadStudents();
        } catch (Exception e) {
            DialogUtils.errorDialog("Failed to upload CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int parseAndUploadCSV(File file, int sectionId, List<SectionComponent> components) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        String headerLine = br.readLine();
        if (headerLine == null) {
            br.close();
            throw new Exception("CSV file is empty");
        }

        String[] headers = headerLine. split(",");

        if (headers.length < 2 || ! headers[0].trim().equalsIgnoreCase("Roll No")) {
            br.close();
            throw new Exception("Invalid CSV format.  First column must be 'Roll No'");
        }

        Map<Integer, Integer> columnToComponentId = new HashMap<>();
        for (int i = 1; i < headers.length; i++) {
            String componentName = headers[i].trim();
            boolean found = false;

            for (SectionComponent sc : components) {
                String typeName = facultyService.getComponentTypeName(sc.getTypeID());
                if (typeName.equalsIgnoreCase(componentName)) {
                    columnToComponentId.put(i, sc.getComponentID());
                    found = true;
                    break;
                }
            }

            if (!found) {
                br.close();
                throw new Exception("Component '" + componentName + "' not found in section components");
            }
        }

        List<Enrollment> enrollments = facultyService.getEnrolledStudents(sectionId);
        Map<String, Integer> rollNoToEnrollmentId = new HashMap<>();

        for (Enrollment e : enrollments) {
            Student student = facultyService.getStudentById(e.getStudentId());
            if (student != null) {
                rollNoToEnrollmentId.put(student.getRollNo(). trim(). toUpperCase(), e.getEnrollmentId());
            }
        }

        int successCount = 0;
        int lineNumber = 1;

        while ((line = br. readLine()) != null) {
            lineNumber++;
            if (line.trim().isEmpty()) continue;

            String[] values = line.split(",");

            if (values.length == 0) continue;

            String rollNo = values[0].trim(). toUpperCase();

            if (! rollNoToEnrollmentId. containsKey(rollNo)) {
                System.err.println("Line " + lineNumber + ": Student with Roll No '" + rollNo + "' not found in section");
                continue;
            }

            int enrollmentId = rollNoToEnrollmentId.get(rollNo);

            for (int i = 1; i < values.length && i < headers.length; i++) {
                if (! columnToComponentId.containsKey(i)) continue;

                String scoreStr = values[i].trim();
                if (scoreStr.isEmpty() || scoreStr.equals("-")) continue;

                try {
                    double score = Double.parseDouble(scoreStr);

                    if (score < 0 || score > 100) {
                        System.err.println("Line " + lineNumber + ": Invalid score " + score + " (must be 0-100)");
                        continue;
                    }

                    int componentId = columnToComponentId. get(i);
                    ComponentScore existing = facultyService.getScore(enrollmentId, componentId);

                    if (existing != null) {
                        ComponentScore updated = new ComponentScore(existing.getScoreId(),
                                enrollmentId, componentId, score);
                        facultyService.updateScore(updated);
                    } else {
                        ComponentScore newScore = new ComponentScore(0,
                                enrollmentId, componentId, score);
                        facultyService.enterScore(newScore);
                    }

                } catch (NumberFormatException e) {
                    System.err. println("Line " + lineNumber + ": Invalid score format '" + scoreStr + "'");
                }
            }

            successCount++;
        }

        br.close();

        if (successCount == 0) {
            throw new Exception("No valid data found in CSV");
        }

        return successCount;
    }

    private void downloadTemplate() {
        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1) {
            DialogUtils.errorDialog("Please select a section first.");
            return;
        }

        Section section = sections.get(idx);
        List<SectionComponent> components = facultyService.getComponents(section.getSectionID());

        if (components.isEmpty()) {
            DialogUtils.errorDialog("No components defined for this section. Please add components first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Template As");
        fileChooser. setSelectedFile(new File("grades_template.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        if (!file. getName().toLowerCase().endsWith(".csv")) {
            file = new File(file. getAbsolutePath() + ".csv");
        }

        try {
            generateTemplate(file, section.getSectionID(), components);
            DialogUtils.successDialog("Template downloaded successfully to:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            DialogUtils.errorDialog("Failed to generate template: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateTemplate(File file, int sectionId, List<SectionComponent> components) throws Exception {
        java.io.PrintWriter writer = new java.io. PrintWriter(file);

        writer. print("Roll No");
        for (SectionComponent sc : components) {
            String typeName = facultyService.getComponentTypeName(sc.getTypeID());
            writer.print("," + typeName);
        }
        writer.println();

        List<Enrollment> enrollments = facultyService.getEnrolledStudents(sectionId);

        for (Enrollment e : enrollments) {
            Student student = facultyService.getStudentById(e.getStudentId());
            if (student == null) continue;

            writer. print(student.getRollNo());

            for (SectionComponent sc : components) {
                ComponentScore score = facultyService.getScore(e.getEnrollmentId(), sc.getComponentID());
                if (score != null) {
                    writer.print("," + score.getScore());
                } else {
                    writer.print(",");
                }
            }

            writer.println();
        }

        writer.close();
    }

    private void enterScores() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils. errorDialog("Cannot enter scores during maintenance mode.");
            return;
        }

        int r = studentsTable.getSelectedRow();
        if (r == -1 || studentsModel.getValueAt(r, 0). equals("No students enrolled")) {
            DialogUtils.errorDialog("Please select a student first.");
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        Section section = sections.get(idx);
        List<Enrollment> enrollments = facultyService.getEnrolledStudents(section.getSectionID());
        Enrollment enrollment = enrollments.get(r);

        Student student = facultyService.getStudentById(enrollment.getStudentId());
        List<SectionComponent> components = facultyService. getComponents(section.getSectionID());

        if (components.isEmpty()) {
            DialogUtils.errorDialog("No components defined for this section. Please add components first.");
            return;
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        headerPanel.setBackground(new Color(255, 243, 205));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel instructionLabel = new JLabel("<html><b>Enter scores out of 100</b><br>Weighted grades will be calculated automatically based on component weights. </html>");
        instructionLabel. setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        headerPanel.add(instructionLabel);

        JLabel studentInfoLabel = new JLabel("<html>Student: <b>" + student.getFullName() + "</b> | Roll No: <b>" + student.getRollNo() + "</b></html>");
        studentInfoLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        headerPanel.add(studentInfoLabel);

        panel. add(headerPanel, BorderLayout. NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JTextField[] scoreFields = new JTextField[components.size()];

        for (int i = 0; i < components.size(); i++) {
            SectionComponent c = components.get(i);
            ComponentScore existing = facultyService.getScore(enrollment.getEnrollmentId(), c.getComponentID());

            scoreFields[i] = new JTextField(existing != null ?  String.valueOf(existing.getScore()) : "");

            String typeName = facultyService.getComponentTypeName(c.getTypeID());
            JLabel label = new JLabel(typeName + " (Weight: " + c.getWeight() + "%) / 100:");
            label.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));

            fieldsPanel.add(label);
            fieldsPanel.add(scoreFields[i]);
        }

        panel.add(fieldsPanel, BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(this, panel, "Enter Scores for " + student.getRollNo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            for (int i = 0; i < components.size(); i++) {
                String scoreText = scoreFields[i].getText().trim();
                if (scoreText.isEmpty()) continue;

                SectionComponent c = components.get(i);
                double score = Double.parseDouble(scoreText);

                if (score < 0 || score > 100) {
                    DialogUtils.errorDialog("Score must be between 0 and 100.");
                    return;
                }

                ComponentScore existing = facultyService. getScore(enrollment.getEnrollmentId(), c.getComponentID());

                if (existing != null) {
                    ComponentScore updated = new ComponentScore(existing.getScoreId(),
                            enrollment. getEnrollmentId(), c. getComponentID(), score);
                    facultyService.updateScore(updated);
                } else {
                    ComponentScore newScore = new ComponentScore(0,
                            enrollment. getEnrollmentId(), c. getComponentID(), score);
                    facultyService.enterScore(newScore);
                }
            }

            DialogUtils.successDialog("Scores entered successfully for " + student.getFullName() + "!");
            loadStudents();

        } catch (NumberFormatException e) {
            DialogUtils.errorDialog("Invalid score format. Please enter a valid number.");
        }
    }

    private void finalizeGrades() {
        if (facultyService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot finalize grades during maintenance mode.");
            return;
        }

        int idx = sectionCombo.getSelectedIndex();
        if (idx == -1) {
            DialogUtils.errorDialog("Please select a section first.");
            return;
        }

        Section section = sections. get(idx);

        double totalWeight = facultyService.getTotalComponentWeight(section.getSectionID());
        if (totalWeight != 100) {
            DialogUtils.errorDialog("Total component weight must be 100%.  Currently: " + totalWeight + "%\n\n" +
                    "Please adjust component weights in the Components panel.");
            return;
        }

        if (! DialogUtils.confirmDialog("This will calculate and finalize grades for all students based on their component scores.\n\n" +
                "Grade Scale:\n" +
                "A: 90+, A-: 85+, B: 80+, B-: 75+\n" +
                "C: 70+, C-: 65+, D: 60+, F: <60\n\n" +
                "Continue? ")) {
            return;
        }

        int successCount = facultyService.finalizeAllGrades(section.getSectionID());

        DialogUtils.successDialog("Grades finalized successfully!\n\nProcessed: " + successCount + " students");
        loadStudents();
    }

    public void refresh() {
        loadSections();
        updateButtonStates();
    }
}