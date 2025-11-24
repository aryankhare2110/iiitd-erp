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

        JLabel subtitle = new JLabel("Add, view, and manage student accounts");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // ===== TABLE SECTION =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));

        model = new DefaultTableModel(
                new String[]{"Roll No", "Name", "Email", "Branch", "Year", "Term", "Status"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setBackground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));

        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ===== BUTTON ROW =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setBackground(new Color(248, 249, 250));
        bottom.setBorder(new EmptyBorder(0, 50, 40, 50));

        JButton btnAdd = primaryButton("Add Student");
        JButton btnToggle = secondaryButton("Enable / Disable");

        bottom.add(btnAdd);
        bottom.add(Box.createHorizontalStrut(15));
        bottom.add(btnToggle);

        add(bottom, BorderLayout.SOUTH);

        // ===== LOAD DATA =====
        loadStudents();

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> openCreateDialog());
        btnToggle.addActionListener(e -> toggleStatus());
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(new Color(13, 110, 253));
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setBackground(new Color(248, 249, 250));
        b.setForeground(new Color(73, 80, 87));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(9, 19, 9, 19)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void loadStudents() {
        model.setRowCount(0);

        List<Student> list = studentDAO.getAllStudents();
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.getRollNo(),
                    s.getFullName(),
                    authDAO.getEmailByUserId(s.getUserId()),
                    s.getBranch(),
                    s.getYear(),
                    s.getTerm(),
                    authDAO.getStatusByUserId(s.getUserId())
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
        JComboBox<String> branchF = new JComboBox<>(new String[]{"CSE", "CSD", "CSAI", "CSAM",
                "CSECON", "CSB", "ECE", "EVE", "CSSS"});
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

        if (JOptionPane.showConfirmDialog(this, panel, "Create Student",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

        String email = emailF.getText().trim().toLowerCase();
        String pwd = new String(pwdF.getPassword()).trim();
        String roll = rollF.getText().trim();
        String name = nameF.getText().trim();

        if (email.isEmpty() || pwd.isEmpty() || roll.isEmpty() || name.isEmpty()) {
            DialogUtils.errorDialog("All fields must be filled.");
            return;
        }

        if (authDAO.emailChecker(email)) {
            DialogUtils.errorDialog("Email already exists.");
            return;
        }

        Student s = new Student(0, 0,
                (String) degreeF.getSelectedItem(),
                (String) branchF.getSelectedItem(),
                (Integer) yearF.getValue(),
                (String) termF.getSelectedItem(),
                roll,
                name
        );

        if (adminService.createStudent(email, pwd, s)) {
            DialogUtils.infoDialog("Student created successfully!");
            loadStudents();
        } else {
            DialogUtils.errorDialog("Failed to create student.");
        }
    }

    private void toggleStatus() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a student first.");
            return;
        }

        String email = model.getValueAt(r, 2).toString();
        String status = model.getValueAt(r, 6).toString();

        int userId = authDAO.getUserId(email);
        boolean active = status.equalsIgnoreCase("ACTIVE");

        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated successfully.");
            loadStudents();
        } else {
            DialogUtils.errorDialog("Failed to update status.");
        }
    }
}