package edu.univ.erp.ui.common;

import javax.swing.*;

public class DialogUtils {

    public static void errorDialog (String message) {
        JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    public static void successDialog (String message) {
        JOptionPane.showMessageDialog(null, message, "Success!", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void infoDialog (String message) {
        JOptionPane.showMessageDialog(null, message, "Information!", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "Confirm?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
}
