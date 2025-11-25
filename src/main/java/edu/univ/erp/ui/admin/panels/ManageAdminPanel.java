package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.domain.Admin;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageAdminPanel extends JPanel {

    private final AuthDAO authDAO = new AuthDAO();
    private final AdminService adminService = new AdminService();

    private final JTable table;
    private final DefaultTableModel model;

    public ManageAdminPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Manage Administrators", "Add, view and manage admin accounts"), BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Email", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = UIUtils.createStyledTable(model);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = UIUtils.createButtonRow(
                UIUtils.primaryButton("Add Admin", e -> openCreateDialog()),
                UIUtils.secondaryButton("Enable / Disable", e -> toggleStatus())
        );
        add(bottom, BorderLayout.SOUTH);

        loadAdmins();
    }

    private void loadAdmins() {
        model.setRowCount(0);
        List<Admin> admins = authDAO.getAllAdmins();
        for (Admin a : admins) {
            model.addRow(new Object[]{a.getEmail(), a.getStatus()});
        }
    }

    private void openCreateDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField emailF = new JTextField();
        JPasswordField pwdF = new JPasswordField();
        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Password:")); panel.add(pwdF);

        if (JOptionPane.showConfirmDialog(this, panel, "Create Admin", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String email = emailF.getText().trim().toLowerCase();
        String pwd = new String(pwdF.getPassword()).trim();
        if (email.isEmpty() || pwd.isEmpty()) {
            DialogUtils.errorDialog("All fields must be filled.");
            return;
        }
        if (authDAO.emailChecker(email)) {
            DialogUtils.errorDialog("Email already exists.");
            return;
        }
        boolean created = adminService.createAdmin(email, pwd);
        if (created) {
            DialogUtils.infoDialog("Admin created successfully!");
            loadAdmins();
        } else {
            DialogUtils.errorDialog("Failed to create admin.");
        }
    }

    private void toggleStatus() {
        int r = table.getSelectedRow();
        if (r == -1) {
            DialogUtils.errorDialog("Select an admin first.");
            return;
        }
        String email = model.getValueAt(r, 0).toString();
        String status = model.getValueAt(r, 1).toString();
        Integer userId = authDAO.getUserId(email);
        if (userId == null) {
            DialogUtils.errorDialog("Could not resolve user id.");
            return;
        }
        boolean active = "ACTIVE".equalsIgnoreCase(status);
        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated successfully.");
            loadAdmins();
        } else {
            DialogUtils.errorDialog("Failed to update status.");
        }
    }
}