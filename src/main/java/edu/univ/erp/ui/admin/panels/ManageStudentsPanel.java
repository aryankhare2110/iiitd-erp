package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.StudentDAO;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageStudentsPanel extends JPanel {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final AdminService adminService = new AdminService();

    private JTable table;
    private DefaultTableModel model;

    public ManageStudentsPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // ===== HEADER =====
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel("Manage Students");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Add, view, and manage student accounts");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // ===== CENTER PANEL (TABLE) =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"Roll No", "Name", "Email", "Branch", "Year", "Term", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(33, 37, 41));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(222, 226, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        center.add(scrollPane, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        //BOTTOM PANEL (ACTION BUTTONS)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setBackground(new Color(248, 249, 250));
        bottom.setBorder(new EmptyBorder(0, 50, 40, 50));

        JButton btnAdd = primaryButton("Add Student");
        JButton btnToggle = secondaryButton("Enable / Disable");

        bottom.add(btnAdd);
        bottom.add(Box.createHorizontalStrut(15));
        bottom.add(btnToggle);

        add(bottom, BorderLayout.SOUTH);

        // Load initial data
        loadStudents();

        // ACTION LISTENERS
        btnAdd.addActionListener(e -> openCreateDialog());
        btnToggle.addActionListener(e -> toggleStatus());
    }

    // ===== BUTTON STYLES =====
    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(13, 110, 253));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBackground(new Color(248, 249, 250));
        b.setForeground(new Color(73, 80, 87));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(9, 19, 9, 19)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ===== LOAD TABLE =====
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

    //CREATE STUDENT
    private void openCreateDialog() {

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField emailF = new JTextField(20);
        JPasswordField pwdF = new JPasswordField(20);
        JTextField rollF = new JTextField(20);
        JTextField nameF = new JTextField(20);

        JComboBox<String> degreeF = new JComboBox<>(new String[]{"B.Tech", "M.Tech", "PhD"});
        JComboBox<String> branchF = new JComboBox<>(new String[]{"CSE", "CSD", "CSAI", "CSAM", "CSECON", "CSB", "ECE", "EVE", "CSSS"});
        JSpinner yearF = new JSpinner(new SpinnerNumberModel(1, 1, 6, 1));
        JComboBox<String> termF = new JComboBox<>(new String[]{"Monsoon", "Winter", "Summer"});

        panel.add(new JLabel("Email:"));
        panel.add(emailF);
        panel.add(new JLabel("Password:"));
        panel.add(pwdF);
        panel.add(new JLabel("Roll No:"));
        panel.add(rollF);
        panel.add(new JLabel("Full Name:"));
        panel.add(nameF);
        panel.add(new JLabel("Degree:"));
        panel.add(degreeF);
        panel.add(new JLabel("Branch:"));
        panel.add(branchF);
        panel.add(new JLabel("Year:"));
        panel.add(yearF);
        panel.add(new JLabel("Term:"));
        panel.add(termF);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String email = emailF.getText().trim().toLowerCase();
        String pwd = new String(pwdF.getPassword());
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

        Student s = new Student(0, 0, degree, branch, year, term, roll, name);

        if (adminService.createStudent(email, pwd, s)) {
            DialogUtils.infoDialog("Student created successfully!");
            loadStudents();
        } else {
            DialogUtils.errorDialog("Failed to create student.");
        }
    }

    // ===== ENABLE / DISABLE STUDENT =====
    private void toggleStatus() {

        int row = table.getSelectedRow();
        if (row == -1) {
            DialogUtils.errorDialog("Please select a student first.");
            return;
        }

        String email = (String) model.getValueAt(row, 2);
        String status = (String) model.getValueAt(row, 6);

        int userId = authDAO.getUserId(email);

        boolean active = "ACTIVE".equalsIgnoreCase(status);

        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated successfully.");
            loadStudents();
        } else {
            DialogUtils.errorDialog("Error updating status.");
        }
    }
}