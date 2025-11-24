package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ManageFacultyPanel extends JPanel {
    public ManageFacultyPanel(AdminService adminService) {
        setLayout(new BorderLayout());
        add(new JLabel("Manage Faculty"), BorderLayout.NORTH);
    }
}
