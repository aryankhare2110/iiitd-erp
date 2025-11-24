package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ManageSectionsPanel extends JPanel {
    public ManageSectionsPanel(AdminService adminService) {
        setLayout(new BorderLayout());
        add(new JLabel("Manage Sections"), BorderLayout.NORTH);
    }
}