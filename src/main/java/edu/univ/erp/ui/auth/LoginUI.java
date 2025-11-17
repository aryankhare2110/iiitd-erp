package edu.univ.erp.ui.auth;

import edu.univ.erp.service.AuthService;
import edu.univ.erp.ui.admin.AdminDashUI;
import edu.univ.erp.ui.common.*;
import com.formdev.flatlaf.*;
import edu.univ.erp.ui.instructor.InstrDashUI;
import edu.univ.erp.ui.student.StudentDashUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class LoginUI extends BaseFrame {

    private final AuthService authService = new AuthService(); // LoginUI owns its backend

    public LoginUI() {
        super("IIITD - ERP Login");

        //Panel - Image
        JPanel imagePanel = new JPanel(new BorderLayout());
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(LoginUI.class.getResource("/Images/Login_Photo.jpg")));
        Image scaled = imageIcon.getImage().getScaledInstance(800, 700, Image.SCALE_SMOOTH);
        imagePanel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.CENTER);

        //Panel - Login
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        //Logo
        ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(LoginUI.class.getResource("/Images/IIITD_Logo.png")));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        gbc.insets = new Insets(10, 0, 10, 0);
        loginPanel.add(new JLabel(new ImageIcon(scaledLogo)), gbc);

        //Title
        gbc.gridy++;
        JLabel heading = new JLabel("Welcome!", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        loginPanel.add(heading, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 30, 0);
        JLabel subheading = new JLabel("Sign in to IIITD ERP Portal", SwingConstants.CENTER);
        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheading.setForeground(Color.GRAY);
        loginPanel.add(subheading, gbc);

        //Email Box
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(createLabel("Email"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        JTextField emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(280, 42));
        emailField.putClientProperty("JTextField.placeholderText", "Enter your email");
        loginPanel.add(emailField, gbc);

        //Password Box
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        loginPanel.add(createLabel("Password"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(280, 42));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");
        loginPanel.add(passwordField, gbc);

        //Login Button
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        JButton loginButton = createButton();

        loginButton.addActionListener(e -> {

            String email = emailField.getText().trim().toLowerCase();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty()) {
                DialogUtils.errorDialog("Please Enter Your Email!");
                return;
            }
            if (password.isEmpty()) {
                DialogUtils.errorDialog("Please Enter Your Password!");
                return;
            }

            String role = authService.login(email, password);

            if (role == null) {
                DialogUtils.errorDialog("Invalid Email Or Password, Please Try Again!");
                return;
            }

            if (role.equalsIgnoreCase("INACTIVE")) {
                DialogUtils.errorDialog("Account Has Been Disabled, Please Contact Admin!");
                return;
            }

            if (role.equalsIgnoreCase("STUDENT")) {
                new StudentDashUI();
            } else if (role.equalsIgnoreCase("INSTRUCTOR")) {
                new InstrDashUI();
            } else if (role.equalsIgnoreCase("ADMIN")) {
                new AdminDashUI();
            }

            this.dispose();
        });

        loginPanel.add(loginButton, gbc);

        //Forgot Password
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel forgotLabel = new JLabel("<html><u>Forgot Password?</u></html>", SwingConstants.CENTER);
        forgotLabel.setForeground(new Color(13, 110, 253));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(forgotLabel, new Color(13, 110, 253), new Color(10, 88, 202));
        loginPanel.add(forgotLabel, gbc);

        //Support Info
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        JLabel infoLabel = new JLabel("<html><center>Need help? Contact IT Support<br/>support@iiitd.ac.in</center></html>",
                SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        loginPanel.add(infoLabel, gbc);

        //Combine panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imagePanel, loginPanel);
        splitPane.setDividerLocation(800);
        splitPane.setEnabled(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(0);

        add(splitPane);
        setVisible(true);
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(73, 80, 87));
        return label;
    }

    private static JButton createButton() {
        JButton button = new JButton("Sign In");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(280, 45));
        button.setBackground(new Color(13, 110, 253));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(button, new Color(13, 110, 253), new Color(11, 99, 227));
        return button;
    }

    private static void addHoverEffect(JComponent component, Color normalColor, Color hoverColor) {
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (component instanceof JButton) {
                    component.setBackground(hoverColor);
                } else {
                    component.setForeground(hoverColor);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (component instanceof JButton) {
                    component.setBackground(normalColor);
                } else {
                    component.setForeground(normalColor);
                }
            }
        });
    }

    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        new LoginUI();
    }
}