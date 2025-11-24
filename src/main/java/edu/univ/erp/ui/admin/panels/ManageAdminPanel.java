package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.domain.Admin;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageAdminPanel extends JPanel {

    private final AuthDAO authDAO = new AuthDAO();
    private final AdminService adminService = new AdminService();

    private JTable table;
    private DefaultTableModel model;

    public ManageAdminPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(new EmptyBorder(40, 50, 20, 50));

        JLabel title = new JLabel("Manage Administrators");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("View, add, and manage Admin accounts");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(108, 117, 125));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(248, 249, 250));
        center.setBorder(new EmptyBorder(10, 50, 20, 50));

        model = new DefaultTableModel(new String[]{"Email", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(Color.WHITE);

        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(33, 37, 41));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(222, 226, 230)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        center.add(scrollPane, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setBackground(new Color(248, 249, 250));
        bottom.setBorder(new EmptyBorder(0, 50, 40, 50));

        JButton btnAdd = primaryButton("Add Admin");
        JButton btnToggle = secondaryButton("Enable / Disable");

        bottom.add(btnAdd);
        bottom.add(Box.createHorizontalStrut(15));
        bottom.add(btnToggle);

        add(bottom, BorderLayout.SOUTH);

        loadAdmins();

        btnAdd.addActionListener(e -> openCreateDialog());
        btnToggle.addActionListener(e -> toggleStatus());
    }

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
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(9, 19, 9, 19)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
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

        JTextField emailF = new JTextField(20);
        JPasswordField pwdF = new JPasswordField(20);

        panel.add(new JLabel("Email:"));
        panel.add(emailF);
        panel.add(new JLabel("Password:"));
        panel.add(pwdF);

        if (JOptionPane.showConfirmDialog(this, panel,
                "Create Admin", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
            return;

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

        int row = table.getSelectedRow();
        if (row == -1) {
            DialogUtils.errorDialog("Select an admin first.");
            return;
        }

        String email = (String) model.getValueAt(row, 0);
        String status = (String) model.getValueAt(row, 1);

        int userId = authDAO.getUserId(email);
        boolean active = "ACTIVE".equalsIgnoreCase(status);

        if (adminService.setUserStatus(userId, !active)) {
            DialogUtils.infoDialog("Status updated successfully.");
            loadAdmins();
        } else {
            DialogUtils.errorDialog("Failed to update status.");
        }
    }
}