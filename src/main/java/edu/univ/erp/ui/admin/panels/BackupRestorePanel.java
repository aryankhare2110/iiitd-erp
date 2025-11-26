package edu.univ.erp.ui.admin. panels;

import edu. univ.erp.data. DBConnection;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common. UIUtils;
import edu.univ.erp.ui.common. DatabaseBackupUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java. time.format.DateTimeFormatter;

public class BackupRestorePanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final JTextArea logArea = new JTextArea();

    private JButton backupButton;
    private JButton restoreButton;
    private JButton configButton;

    // Database config (auto-loaded)
    private String host = "localhost";
    private int port = 5432;
    private String database = "erp";
    private String username = "postgres";
    private String password = "";

    // PostgreSQL binary paths
    private String pgDumpPath = "pg_dump";
    private String pgRestorePath = "pg_restore";

    public BackupRestorePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeaderWithBadge("Database Backup & Restore",
                        "Create backups and restore your database with one click",
                        adminService. isMaintenanceMode(),
                        " ⚠ MAINTENANCE MODE "),
                BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Info card at top
        JPanel infoCard = createInfoCard();
        mainPanel.add(infoCard, BorderLayout. NORTH);

        // Log area in center
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons at bottom
        backupButton = UIUtils. primaryButton("Create Backup", e -> createBackup());
        restoreButton = UIUtils.primaryButton("Restore from Backup", e -> restoreBackup());
        configButton = UIUtils.secondaryButton("Configure PostgreSQL Path", e -> configurePaths());

        add(UIUtils. createButtonRow(
                backupButton,
                restoreButton,
                configButton,
                UIUtils.secondaryButton("Clear Log", e -> logArea.setText("Ready.\n"))
        ), BorderLayout.SOUTH);

        loadDatabaseConfig();
        detectPostgreSQLPaths();
        updateButtonStates();
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory. createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel content = new JPanel();
        content. setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel title = new JLabel("Database Configuration");
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel(String.format("<html><b>Host:</b> %s  |  <b>Port:</b> %d  |  <b>Database:</b> %s  |  <b>User:</b> %s</html>",
                host, port, database, username));
        info.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
        info.setForeground(new Color(73, 80, 87));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note = new JLabel("💡 Backups will be saved to your chosen location with automatic timestamped filenames");
        note.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        note.setForeground(new Color(108, 117, 125));
        note. setAlignmentX(Component. LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(info);
        content.add(Box.createVerticalStrut(8));
        content.add(note);

        card.add(content, BorderLayout. CENTER);
        return card;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Operation Log");
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        panel.add(title, BorderLayout. NORTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setText("Ready.  Click 'Create Backup' or 'Restore from Backup' to begin.\n");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scroll.setPreferredSize(new Dimension(0, 300));
        panel. add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadDatabaseConfig() {
        try {
            String url = DBConnection.getErpConnection().getMetaData().getURL();
            String[] parts = url.split("//")[1].split("/");
            String[] hostPort = parts[0].split(":");

            host = hostPort[0];
            port = hostPort. length > 1 ? Integer. parseInt(hostPort[1]) : 5432;
            database = parts[1]. split("\\?")[0];
            username = "postgres";

            appendLog("✓ Database configuration loaded: " + database + " @ " + host + ":" + port);
        } catch (Exception e) {
            appendLog("⚠ Warning: Could not auto-load database config.  Using defaults.");
        }
    }

    private void detectPostgreSQLPaths() {
        // Common PostgreSQL installation paths on macOS
        String[] commonPaths = {
                "/Library/PostgreSQL/17/bin/pg_dump",
                "/Library/PostgreSQL/16/bin/pg_dump",
                "/Library/PostgreSQL/15/bin/pg_dump",
                "/opt/homebrew/bin/pg_dump",
                "/usr/local/bin/pg_dump",
                "/Applications/Postgres. app/Contents/Versions/latest/bin/pg_dump"
        };

        for (String path : commonPaths) {
            File pgDump = new File(path);
            if (pgDump.exists() && pgDump.canExecute()) {
                pgDumpPath = path;
                pgRestorePath = path. replace("pg_dump", "pg_restore");
                appendLog("✓ PostgreSQL binaries detected at: " + new File(path).getParent());
                return;
            }
        }

        appendLog("⚠ PostgreSQL binaries not found in common locations.");
        appendLog("  Click 'Configure PostgreSQL Path' to set manually.");
    }

    private void configurePaths() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Enter the full path to pg_dump binary:"));
        panel.add(new JLabel("(e.g., /Library/PostgreSQL/17/bin/pg_dump)"));

        JTextField pathField = new JTextField(pgDumpPath);
        panel.add(pathField);

        JButton browseButton = new JButton("Browse.. .");
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select pg_dump binary");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (chooser.showOpenDialog(this) == JFileChooser. APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        panel.add(browseButton);

        if (JOptionPane.showConfirmDialog(this, panel, "Configure PostgreSQL Path",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane. OK_OPTION) {

            String newPath = pathField.getText().trim();
            File pgDump = new File(newPath);

            if (! pgDump.exists()) {
                DialogUtils.errorDialog("File does not exist: " + newPath);
                return;
            }

            if (!pgDump.canExecute()) {
                DialogUtils.errorDialog("File is not executable: " + newPath);
                return;
            }

            pgDumpPath = newPath;
            pgRestorePath = newPath.replace("pg_dump", "pg_restore");

            appendLog("✓ PostgreSQL path configured: " + newPath);
            DialogUtils.successDialog("PostgreSQL path configured successfully!\n\n" +
                    "pg_dump: " + pgDumpPath + "\n" +
                    "pg_restore: " + pgRestorePath);
        }
    }

    private void updateButtonStates() {
        boolean maintenance = adminService.isMaintenanceMode();
        backupButton.setEnabled(! maintenance);
        restoreButton. setEnabled(!maintenance);
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea. append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void createBackup() {
        if (adminService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot create backup during maintenance mode.");
            return;
        }

        // Check if pg_dump path is valid
        if (! new File(pgDumpPath).exists()) {
            DialogUtils.errorDialog("pg_dump not found!\n\n" +
                    "Current path: " + pgDumpPath + "\n\n" +
                    "Click 'Configure PostgreSQL Path' to set the correct path.");
            return;
        }

        // Auto-generate filename
        String timestamp = LocalDateTime. now().format(DateTimeFormatter. ofPattern("yyyyMMdd_HHmmss"));
        String filename = database + "_backup_" + timestamp + ".dump";

        // Choose save location
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Backup As");
        chooser.setSelectedFile(new File(filename));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Backup files (*. dump)", "dump"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File backupFile = chooser.getSelectedFile();
        if (! backupFile.getName().endsWith(".dump")) {
            backupFile = new File(backupFile.getAbsolutePath() + ".dump");
        }

        final String outFile = backupFile.getAbsolutePath();

        appendLog("\n" + "=". repeat(60));
        appendLog("STARTING BACKUP");
        appendLog("=".repeat(60));
        appendLog("Target file: " + outFile);
        appendLog("Database: " + database + " @ " + host + ":" + port);
        appendLog("pg_dump: " + pgDumpPath);
        appendLog("Time: " + LocalDateTime.now(). format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        appendLog("");

        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);

        new SwingWorker<DatabaseBackupUtil. Result, Void>() {
            @Override
            protected DatabaseBackupUtil.Result doInBackground() {
                try {
                    return DatabaseBackupUtil.backupDatabase(
                            pgDumpPath, host, port, database, username, password, outFile, 300);
                } catch (Exception ex) {
                    return new DatabaseBackupUtil.Result(false, "", "Error: " + ex.getMessage());
                }
            }

            @Override
            protected void done() {
                backupButton.setEnabled(true);
                restoreButton.setEnabled(true);

                try {
                    DatabaseBackupUtil.Result result = get();

                    if (result.isSuccess()) {
                        appendLog("✓ BACKUP COMPLETED SUCCESSFULLY!");
                        appendLog("✓ Saved to: " + outFile);
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.successDialog("Backup created successfully!\n\nLocation:\n" + outFile);
                    } else {
                        appendLog("✗ BACKUP FAILED!");
                        if (! result.getStderr().isEmpty()) {
                            appendLog("Error details:\n" + result.getStderr());
                        }
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.errorDialog("Backup failed. Check the log for details.");
                    }
                } catch (Exception ex) {
                    appendLog("✗ UNEXPECTED ERROR: " + ex.getMessage());
                    appendLog("=". repeat(60) + "\n");
                    DialogUtils.errorDialog("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void restoreBackup() {
        if (adminService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot restore database during maintenance mode.");
            return;
        }

        // Check if pg_restore path is valid
        if (!new File(pgRestorePath).exists()) {
            DialogUtils.errorDialog("pg_restore not found!\n\n" +
                    "Current path: " + pgRestorePath + "\n\n" +
                    "Click 'Configure PostgreSQL Path' to set the correct path.");
            return;
        }

        // Choose backup file
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Backup File to Restore");
        chooser. setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Backup files (*.dump)", "dump"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File backupFile = chooser.getSelectedFile();

        if (! backupFile.exists()) {
            DialogUtils.errorDialog("Selected file does not exist.");
            return;
        }

        // Confirm restore
        if (! DialogUtils.confirmDialog(
                "⚠️  WARNING: DATABASE RESTORE\n\n" +
                        "This will OVERWRITE the current database:\n\n" +
                        "  • Database: " + database + "\n" +
                        "  • Host: " + host + "\n" +
                        "  • Backup file: " + backupFile.getName() + "\n\n" +
                        "This action CANNOT be undone!\n\n" +
                        "Are you absolutely sure you want to continue?")) {
            return;
        }

        final String dumpFile = backupFile.getAbsolutePath();

        appendLog("\n" + "=". repeat(60));
        appendLog("STARTING RESTORE");
        appendLog("=".repeat(60));
        appendLog("Source file: " + dumpFile);
        appendLog("Database: " + database + " @ " + host + ":" + port);
        appendLog("pg_restore: " + pgRestorePath);
        appendLog("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        appendLog("");

        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);

        new SwingWorker<DatabaseBackupUtil. Result, Void>() {
            @Override
            protected DatabaseBackupUtil.Result doInBackground() {
                try {
                    return DatabaseBackupUtil.restoreDatabase(
                            pgRestorePath, host, port, database, username, password, dumpFile, 300);
                } catch (Exception ex) {
                    return new DatabaseBackupUtil.Result(false, "", "Error: " + ex.getMessage());
                }
            }

            @Override
            protected void done() {
                backupButton. setEnabled(true);
                restoreButton.setEnabled(true);

                try {
                    DatabaseBackupUtil.Result result = get();

                    if (result. isSuccess()) {
                        appendLog("✓ RESTORE COMPLETED SUCCESSFULLY!");
                        appendLog("✓ Database has been restored from: " + dumpFile);
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.successDialog("Database restored successfully!");
                    } else {
                        appendLog("✗ RESTORE FAILED!");
                        if (!result.getStderr().isEmpty()) {
                            appendLog("Error details:\n" + result. getStderr());
                        }
                        appendLog("=". repeat(60) + "\n");
                        DialogUtils.errorDialog("Restore failed. Check the log for details.");
                    }
                } catch (Exception ex) {
                    appendLog("✗ UNEXPECTED ERROR: " + ex.getMessage());
                    appendLog("=".repeat(60) + "\n");
                    DialogUtils.errorDialog("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    public void refresh() {
        updateButtonStates();
        revalidate();
        repaint();
    }
}