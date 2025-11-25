package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.DepartmentDAO;
import edu.univ.erp.dao.FacultyDAO;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageFacultyPanel extends JPanel {

    private final FacultyDAO facultyDAO = new FacultyDAO();
    private final AuthDAO authDAO = new AuthDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final AdminService adminService = new AdminService();

    private final JTable table;
    private final DefaultTableModel model;

    public ManageFacultyPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Faculty", "Add, view, and manage faculty accounts"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Name", "Email", "Department", "Designation", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Add Faculty", e -> openCreateDialog()),
                UIUtils.secondaryButton("Enable / Disable", e -> toggleStatus())
        );
        add(bottom, BorderLayout.SOUTH);

        loadFaculty();
    }

    private void loadFaculty() {
        model.setRowCount(0);
        List<Faculty> list = facultyDAO.getAllFaculty();
        for (Faculty f : list) {
            model.addRow(new Object[]{
                    f.getFullName(),
                    authDAO.getEmailByUserId(f.getUserId()),
                    departmentDAO.getDepartmentNameById(f.getDepartmentId()),
                    f.getDesignation(),
                    authDAO.getStatusByUserId(f.getUserId())
            });
        }
    }

    private void openCreateDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField emailF = new JTextField();
        JPasswordField pwdF = new JPasswordField();
        JTextField nameF = new JTextField();

        JComboBox<String> deptF = new JComboBox<>(departmentDAO.getAllDepartmentNames().toArray(new String[0]));
        JComboBox<String> desigF = new JComboBox<>(new String[]{
                "Professor", "Associate Professor", "Assistant Professor",
                "Visiting Professor", "Adjunct Faculty", "Emeritus Professor", "Department Head"
        });

        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Password:")); panel.add(pwdF);
        panel.add(new JLabel("Full Name:")); panel.add(nameF);
        panel.add(new JLabel("Department:")); panel.add(deptF);
        panel.add(new JLabel("Designation:")); panel.add(desigF);

        if (JOptionPane.showConfirmDialog(this, panel, "Create Faculty", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String email = emailF.getText().trim().toLowerCase();
        String pwd = new String(pwdF.getPassword()).trim();
        String name = nameF.getText().trim();
        int deptId = departmentDAO.getDepartmentIdByName((String) deptF.getSelectedItem());
        String desig = (String) desigF.getSelectedItem();

        if (email.isEmpty() || pwd.isEmpty() || name.isEmpty()) {
            DialogUtils.errorDialog("All fields must be filled.");
            return;
        }

        if (adminService.createFaculty(email, pwd, new Faculty(0, 0, deptId, desig, name))) {
            DialogUtils.infoDialog("Faculty created successfully!");
            loadFaculty();
        } else {
            DialogUtils.errorDialog(authDAO.emailChecker(email) ? "Email already exists." : "Failed to create faculty.");
        }
    }

    private void toggleStatus() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a faculty member first.");
            return;
        }
        String email = model.getValueAt(r, 1).toString();
        String status = model.getValueAt(r, 4).toString();
        Integer userId = authDAO.getUserId(email);
        if (userId == null) {
            DialogUtils.errorDialog("Could not resolve user id for selected faculty.");
            return;
        }
        boolean active = "ACTIVE".equalsIgnoreCase(status);
        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated.");
            loadFaculty();
        } else {
            DialogUtils.errorDialog("Failed to update status.");
        }
    }
}