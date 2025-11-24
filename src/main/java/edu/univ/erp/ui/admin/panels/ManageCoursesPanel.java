package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.service.AdminService;

import javax.swing.*;
import java.awt.*;

public class ManageCoursesPanel extends JPanel {
    public ManageCoursesPanel(AdminService adminService) {
        setLayout(new BorderLayout());
        add(new JLabel("Manage Courses"), BorderLayout.NORTH);
    }
}
