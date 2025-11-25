package edu.univ.erp.ui.student.panels;

import javax.swing.*;
import java.awt.*;

public class EnrollmentPanel extends JPanel {

    public EnrollmentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("My Enrollments (Coming Soon)");
        label.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label, BorderLayout.CENTER);
    }
}