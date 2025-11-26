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

        add(UIUtils.createHeader("Manage Faculty", "Add, edit, and manage faculty accounts"),
                BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Name", "Email", "Department", "Designation", "Status"}, 0
        ) {
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
                UIUtils.primaryButton("Edit Faculty", e -> openEditDialog()),
                UIUtils.secondaryButton("Enable / Disable", e -> toggleStatus())
        );

        add(bottom, BorderLayout.SOUTH);

        loadFaculty();

        add(bottom, BorderLayout.SOUTH);
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
        JPanel panel = buildForm(null);

        if (!showDialog(panel, "Create Faculty")) return;

        FormData d = extract(panel);
        if (d == null) {
            DialogUtils.errorDialog("Please fill all required fields.");
            return;
        }

        if (adminService.createFaculty(d.email, d.password,
                new Faculty(0, 0, d.deptId, d.designation, d.name))) {

            DialogUtils.infoDialog("Faculty created successfully!");
            loadFaculty();
        } else {
            DialogUtils.errorDialog("Failed to create faculty. Email may already exist.");
        }
    }

    private void openEditDialog() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select a faculty member first.");
            return;
        }

        String email = model.getValueAt(r, 1).toString();
        Faculty f = facultyDAO.getFacultyByUserId(authDAO.getUserId(email));

        JPanel panel = buildForm(f);

        if (!showDialog(panel, "Edit Faculty")) return;

        FormData d = extract(panel);
        if (d == null) {
            DialogUtils.errorDialog("Please fill all required fields.");
            return;
        }

        Faculty updated = new Faculty(
                f.getFacultyId(),
                f.getUserId(),
                d.deptId,
                d.designation,
                d.name
        );

        if (adminService.updateFaculty(updated)) {
            DialogUtils.infoDialog("Faculty updated successfully!");
            loadFaculty();
        } else {
            DialogUtils.errorDialog("Failed to update faculty.");
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

        boolean newStatus = !"ACTIVE".equalsIgnoreCase(status);

        if (adminService.setUserStatus(userId, newStatus)) {
            DialogUtils.infoDialog("Status updated.");
            loadFaculty();
        } else {
            DialogUtils.errorDialog("Failed to update status.");
        }
    }

    private static class FormData {
        String email, password, name, designation;
        int deptId;

        FormData(String e, String p, String n, int dept, String des) {
            email = e; password = p; name = n; deptId = dept; designation = des;
        }
    }

    private JPanel buildForm(Faculty existing) {

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField emailF = new JTextField();
        emailF.setName("email");

        JPasswordField pwdF = new JPasswordField();
        pwdF.setName("pwd");

        JTextField nameF = new JTextField();
        nameF.setName("name");

        JComboBox<String> deptF =
                new JComboBox<>(departmentDAO.getAllDepartmentNames().toArray(new String[0]));
        deptF.setName("dept");

        JComboBox<String> desigF = new JComboBox<>(new String[]{
                "Professor", "Associate Professor", "Assistant Professor",
                "Visiting Professor", "Adjunct Faculty",
                "Emeritus Professor", "Department Head"
        });
        desigF.setName("desig");

        if (existing != null) {
            emailF.setText(authDAO.getEmailByUserId(existing.getUserId()));
            emailF.setEnabled(false);
            nameF.setText(existing.getFullName());
            deptF.setSelectedItem(departmentDAO.getDepartmentNameById(existing.getDepartmentId()));
            desigF.setSelectedItem(existing.getDesignation());
        }

        p.add(new JLabel("Email:")); p.add(emailF);

        if (existing == null) {
            p.add(new JLabel("Password:")); p.add(pwdF);
        }

        p.add(new JLabel("Full Name:")); p.add(nameF);
        p.add(new JLabel("Department:")); p.add(deptF);
        p.add(new JLabel("Designation:")); p.add(desigF);

        return p;
    }

    private FormData extract(JPanel p) {

        JTextField email = (JTextField) find(p, "email");
        JTextField name = (JTextField) find(p, "name");
        JComboBox<?> dept = (JComboBox<?>) find(p, "dept");
        JComboBox<?> desig = (JComboBox<?>) find(p, "desig");

        if (email == null || name == null) return null;

        String pwd = "";
        JPasswordField pwdField = (JPasswordField) find(p, "pwd");
        if (pwdField != null) pwd = new String(pwdField.getPassword()).trim();

        int deptId = departmentDAO.getDepartmentIdByName((String) dept.getSelectedItem());

        return new FormData(
                email.getText().trim().toLowerCase(),
                pwd,
                name.getText().trim(),
                deptId,
                (String) desig.getSelectedItem()
        );
    }

    private Component find(JPanel p, String name) {
        for (Component c : p.getComponents())
            if (name.equals(c.getName())) return c;
        return null;
    }

    private boolean showDialog(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(
                this, panel, title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) == JOptionPane.OK_OPTION;
    }

    public void refresh() {
        loadFaculty();
    }
}