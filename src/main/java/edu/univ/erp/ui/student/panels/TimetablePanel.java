package edu.univ.erp.ui.student.panels;

import javax.swing.*;
import java.awt.*;

public class TimetablePanel extends JPanel {

    public TimetablePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel label = new JLabel("Timetable (Coming Soon)");
        label.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(label, BorderLayout.CENTER);
    }
}