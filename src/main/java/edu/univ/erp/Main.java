package edu.univ.erp;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.univ.erp.ui.auth.LoginUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatIntelliJLaf. setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf Look and Feel");
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginUI();
        });
    }
}