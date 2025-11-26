package edu.univ.erp.ui.admin.panels;

import edu.univ.erp.ui.common.DialogUtils;
import edu.univ.erp.ui.common.UIUtils;
import edu.univ.erp.ui.common.DatabaseBackupUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Admin panel to backup and restore a PostgreSQL database using pg_dump/pg_restore.
 *
 * Usage:
 * - Enter DB connection details (host/port/database/user/password)
 * - Optionally provide full path to pg_dump / pg_restore binaries (if not in PATH)
 * - Click "Choose Backup File" to pick destination (for backup) or source (for restore)
 * - Click Backup or Restore. Progress and logs will show in the text area.
 *
 * Security note:
 * - This panel uses PGPASSWORD environment variable for the child process. Consider using .pgpass
 *   for production or avoid storing password in UI if security is a concern.
 */
public class BackupRestorePanel extends JPanel {

    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("5432");
    private final JTextField dbField = new JTextField("erp");
    private final JTextField userField = new JTextField("postgres");
    private final JPasswordField passwordField = new JPasswordField();

    private final JTextField pgDumpPathField = new JTextField("pg_dump");       // can be full path
    private final JTextField pgRestorePathField = new JTextField("pg_restore"); // can be full path

    private final JTextField fileField = new JTextField();
    private final JTextArea logArea = new JTextArea();

    public BackupRestorePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeader("Database Backup & Restore", "Backup (pg_dump) and restore (pg_restore) PostgreSQL databases"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.setBackground(Color.WHITE);

        form.add(new JLabel("Host:")); form.add(hostField);
        form.add(new JLabel("Port:")); form.add(portField);
        form.add(new JLabel("Database:")); form.add(dbField);
        form.add(new JLabel("User:")); form.add(userField);
        form.add(new JLabel("Password:")); form.add(passwordField);

        form.add(new JLabel("pg_dump binary (or 'pg_dump' if in PATH):")); form.add(pgDumpPathField);
        form.add(new JLabel("pg_restore binary (or 'pg_restore' if in PATH):")); form.add(pgRestorePathField);

        JPanel fileRow = new JPanel(new BorderLayout(8, 0));
        fileRow.setBackground(Color.WHITE);
        fileRow.add(fileField, BorderLayout.CENTER);
        JButton chooseBtn = UIUtils.secondaryButton("Choose File...", e -> chooseFile());
        fileRow.add(chooseBtn, BorderLayout.EAST);

        form.add(new JLabel("Backup / Restore file:")); form.add(fileRow);

        add(form, BorderLayout.CENTER);

        JPanel btnRow = UIUtils.createButtonRow(
                UIUtils.primaryButton("Backup", e -> onBackup()),
                UIUtils.primaryButton("Restore", e -> onRestore()),
                UIUtils.secondaryButton("Auto name & choose file", e -> autoChooseBackupFile())
        );
        add(btnRow, BorderLayout.SOUTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(800, 240));
        add(scroll, BorderLayout.EAST);
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select backup/restore file");
        int ret = chooser.showDialog(this, "Select");
        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            fileField.setText(f.getAbsolutePath());
        }
    }

    private void autoChooseBackupFile() {
        String db = dbField.getText().trim();
        if (db.isEmpty()) db = "database";
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String defaultName = db + "_" + stamp + ".dump";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save backup as...");
        chooser.setSelectedFile(new File(defaultName));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void appendLog(String s) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(s);
            logArea.append("\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void onBackup() {
        String outFile = fileField.getText().trim();
        if (outFile.isEmpty()) {
            DialogUtils.errorDialog("Choose an output file for the backup first.");
            return;
        }
        final String pgDump = pgDumpPathField.getText().trim();
        final String host = hostField.getText().trim();
        final int port = Integer.parseInt(portField.getText().trim());
        final String db = dbField.getText().trim();
        final String user = userField.getText().trim();
        final String pass = new String(passwordField.getPassword());

        appendLog("Starting backup: " + outFile);
        setEnabledRecursive(this, false);

        new SwingWorker<DatabaseBackupUtil.Result, Void>() {
            @Override
            protected DatabaseBackupUtil.Result doInBackground() {
                try {
                    return DatabaseBackupUtil.backupDatabase(pgDump, host, port, db, user, pass, outFile, 0);
                } catch (Exception ex) {
                    return new DatabaseBackupUtil.Result(false, "", ex.getMessage());
                }
            }

            @Override
            protected void done() {
                setEnabledRecursive(BackupRestorePanel.this, true);
                try {
                    DatabaseBackupUtil.Result res = get();
                    appendLog("Backup finished. Success=" + res.isSuccess());
                    appendLog("----- STDOUT -----");
                    appendLog(res.getStdout());
                    appendLog("----- STDERR -----");
                    appendLog(res.getStderr());
                    if (res.isSuccess()) {
                        DialogUtils.infoDialog("Backup completed: " + outFile);
                    } else {
                        DialogUtils.errorDialog("Backup failed. See logs.");
                    }
                } catch (Exception ex) {
                    appendLog("Unexpected error: " + ex.getMessage());
                    DialogUtils.errorDialog("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void onRestore() {
        String dumpFile = fileField.getText().trim();
        if (dumpFile.isEmpty()) {
            DialogUtils.errorDialog("Choose a dump file to restore from first.");
            return;
        }
        if (!new File(dumpFile).exists()) {
            DialogUtils.errorDialog("Dump file does not exist: " + dumpFile);
            return;
        }
        if (!DialogUtils.confirmDialog("Restoring will modify the target database. Continue?")) {
            return;
        }

        final String pgRestore = pgRestorePathField.getText().trim();
        final String host = hostField.getText().trim();
        final int port = Integer.parseInt(portField.getText().trim());
        final String db = dbField.getText().trim();
        final String user = userField.getText().trim();
        final String pass = new String(passwordField.getPassword());

        appendLog("Starting restore from: " + dumpFile);
        setEnabledRecursive(this, false);

        new SwingWorker<DatabaseBackupUtil.Result, Void>() {
            @Override
            protected DatabaseBackupUtil.Result doInBackground() {
                try {
                    return DatabaseBackupUtil.restoreDatabase(pgRestore, host, port, db, user, pass, dumpFile, 0);
                } catch (Exception ex) {
                    return new DatabaseBackupUtil.Result(false, "", ex.getMessage());
                }
            }

            @Override
            protected void done() {
                setEnabledRecursive(BackupRestorePanel.this, true);
                try {
                    DatabaseBackupUtil.Result res = get();
                    appendLog("Restore finished. Success=" + res.isSuccess());
                    appendLog("----- STDOUT -----");
                    appendLog(res.getStdout());
                    appendLog("----- STDERR -----");
                    appendLog(res.getStderr());
                    if (res.isSuccess()) {
                        DialogUtils.infoDialog("Restore completed successfully.");
                    } else {
                        DialogUtils.errorDialog("Restore failed. See logs.");
                    }
                } catch (Exception ex) {
                    appendLog("Unexpected error: " + ex.getMessage());
                    DialogUtils.errorDialog("Unexpected error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void setEnabledRecursive(Component comp, boolean enabled) {
        comp.setEnabled(enabled);
        if (comp instanceof Container) {
            for (Component c : ((Container) comp).getComponents()) {
                setEnabledRecursive(c, enabled);
            }
        }
    }
    public void refresh() {
        revalidate();
        repaint();
    }
}