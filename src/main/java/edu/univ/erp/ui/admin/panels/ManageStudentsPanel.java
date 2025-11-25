package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.StudentDAO;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStudentsPanel extends JPanel {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final AdminService adminService = new AdminService();

    private final JTable table;
    private final DefaultTableModel model;

    public ManageStudentsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Students", "Add, view, and manage student accounts"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Roll No", "Name", "Email", "Branch", "Year", "Term", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Add Student", e -> openCreateDialog()),
                UIUtils.secondaryButton("Enable / Disable", e -> toggleStatus())
        );
        add(bottom, BorderLayout.SOUTH);

        loadStudents();
    }

    private void loadStudents() {
        model.setRowCount(0);
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            String email = authDAO.getEmailByUserId(s.getUserId());
            String status = authDAO.getStatusByUserId(s.getUserId());
            model.addRow(new Object[]{
                    s.getRollNo(),
                    s.getFullName(),
                    email,
                    s.getBranch(),
                    s.getYear(),
                    s.getTerm(),
                    status
            });
        }
    }

    private void openCreateDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField emailF = new JTextField();
        JPasswordField pwdF = new JPasswordField();
        JTextField rollF = new JTextField();
        JTextField nameF = new JTextField();

        JComboBox<String> degreeF = new JComboBox<>(new String[]{"B.Tech", "M.Tech", "PhD"});
        JComboBox<String> branchF = new JComboBox<>(new String[]{"CSE", "CSD", "CSAI", "CSAM", "CSECON", "CSB", "ECE", "EVE", "CSSS"});
        JSpinner yearF = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon", "Winter", "Summer"});

        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Password:")); panel.add(pwdF);
        panel.add(new JLabel("Roll No:")); panel.add(rollF);
        panel.add(new JLabel("Full Name:")); panel.add(nameF);
        panel.add(new JLabel("Degree:")); panel.add(degreeF);
        panel.add(new JLabel("Branch:")); panel.add(branchF);
        panel.add(new JLabel("Year:")); panel.add(yearF);
        panel.add(new JLabel("Term:")); panel.add(termF);

        if (JOptionPane.showConfirmDialog(this, panel, "Create Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        String email = emailF.getText().trim().toLowerCase();
        String pwd = new String(pwdF.getPassword()).trim();
        String roll = rollF.getText().trim();
        String name = nameF.getText().trim();
        String degree = (String) degreeF.getSelectedItem();
        String branch = (String) branchF.getSelectedItem();
        int year = (Integer) yearF.getValue();
        String term = (String) termF.getSelectedItem();

        if (email.isEmpty() || pwd.isEmpty() || roll.isEmpty() || name.isEmpty()) {
            DialogUtils.errorDialog("All fields must be filled.");
            return;
        }

        if (adminService.createStudent(email, pwd, new Student(0, 0, degree, branch, year, term, roll, name))) {
            DialogUtils.infoDialog("Student created successfully!");
            loadStudents();
        } else {
            if (authDAO.emailChecker(email)) {
                DialogUtils.errorDialog("Email address already exists. Please use a different email.");
            } else {
                DialogUtils.errorDialog("Failed to create student. Please check all fields.");
            }
        }
    }

    private void toggleStatus() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Please select a student first.");
            return;
        }
        String email = model.getValueAt(r, 2).toString();
        String status = model.getValueAt(r, 6).toString();
        Integer userId = authDAO.getUserId(email);
        if (userId == null) {
            DialogUtils.errorDialog("Could not resolve user id for selected student.");
            return;
        }
        boolean active = "ACTIVE".equalsIgnoreCase(status);
        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated successfully.");
            loadStudents();
        } else {
            DialogUtils.errorDialog("Error updating status.");
        }
    }
}