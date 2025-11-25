package edu.univ.erp.ui.student.panels;

import javax.swing.*;
import java.awt.*;

public class GradesPanel extends JPanel {

    public GradesPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("Grades (Coming Soon)");
        label.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label, BorderLayout.CENTER);
    }
}