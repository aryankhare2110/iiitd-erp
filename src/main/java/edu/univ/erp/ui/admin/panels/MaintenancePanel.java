package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class MaintenancePanel extends JPanel {
    public MaintenancePanel(AdminService adminService) {
        setLayout(new BorderLayout());
        add(new JLabel("Maintenance"), BorderLayout.NORTH);
    }
}