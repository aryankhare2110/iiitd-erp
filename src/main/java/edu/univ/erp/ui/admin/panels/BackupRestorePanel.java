package edu.univ.erp.ui. admin. panels;

import edu.univ.erp.data.DBConnection;
import edu. univ.erp.service. AdminService;
import edu.univ.erp.ui.common. DialogUtils;
import edu.univ.erp.ui.common. UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BackupRestorePanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final JTextArea logArea = new JTextArea();
    private JButton backupButton;
    private JButton restoreButton;

    public BackupRestorePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        add(UIUtils.createHeaderWithBadge("Database Backup & Restore",
                        "Create backups and restore your databases (ERP + Auth)",
                        adminService.isMaintenanceMode(),
                        " ⚠ MAINTENANCE MODE "),
                BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(new Color(248, 249, 250));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Log area
        JPanel logPanel = new JPanel(new BorderLayout(0, 10));
        logPanel.setBackground(Color.WHITE);
        logPanel. setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("Operation Log");
        title.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        logPanel.add(title, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font. PLAIN, 12));
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setText("Ready.  Click 'Create Backup' or 'Restore from Backup' to begin.\n");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        scroll.setPreferredSize(new Dimension(0, 300));
        logPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(logPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        backupButton = UIUtils.primaryButton("Create Backup", e -> createBackup());
        restoreButton = UIUtils.primaryButton("Restore from Backup", e -> restoreBackup());

        add(UIUtils.createButtonRow(
                backupButton,
                restoreButton,
                UIUtils. secondaryButton("Clear Log", e -> logArea.setText("Ready.\n"))
        ), BorderLayout. SOUTH);

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean maintenance = adminService.isMaintenanceMode();
        backupButton.setEnabled(! maintenance);
        restoreButton. setEnabled(!maintenance);
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void createBackup() {
        if (adminService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot create backup during maintenance mode.");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "full_backup_" + timestamp + ". sql";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Backup As");
        chooser.setSelectedFile(new File(filename));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File backupFile = chooser.getSelectedFile();
        if (! backupFile.getName().endsWith(".sql")) {
            backupFile = new File(backupFile.getAbsolutePath() + ".sql");
        }

        final String outFile = backupFile.getAbsolutePath();

        appendLog("\n" + "=". repeat(60));
        appendLog("STARTING FULL BACKUP (ERP + Auth)");
        appendLog("=".repeat(60));
        appendLog("Target: " + outFile);
        appendLog("");

        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {

                    writer.println("-- Full Database Backup (ERP + Auth)");
                    writer.println("-- Generated: " + LocalDateTime. now());
                    writer.println();

                    // Backup ERP Database
                    appendLog(">>> Backing up ERP Database");
                    writer.println("-- ========================================");
                    writer.println("-- ERP Database");
                    writer.println("-- ========================================");
                    writer.println();

                    boolean erpSuccess = backupDatabase(DBConnection.getErpConnection(), writer, "ERP");

                    if (! erpSuccess) {
                        appendLog("ERP backup failed!");
                        return false;
                    }

                    writer.println();
                    writer.println();

                    // Backup Auth Database
                    appendLog(">>> Backing up Auth Database");
                    writer.println("-- ========================================");
                    writer.println("-- Auth Database");
                    writer. println("-- ========================================");
                    writer.println();

                    boolean authSuccess = backupDatabase(DBConnection.getAuthConnection(), writer, "Auth");

                    if (!authSuccess) {
                        appendLog("Auth backup failed!");
                        return false;
                    }

                    writer.println();
                    writer.println("-- Full backup completed successfully");
                    appendLog("Full backup complete");
                    return true;

                } catch (Exception e) {
                    appendLog("Error: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                backupButton. setEnabled(true);
                restoreButton.setEnabled(true);

                try {
                    if (get()) {
                        appendLog("✓ SUCCESS");
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.successDialog("Full backup created!\n\n" + outFile);
                    } else {
                        appendLog("✗ FAILED");
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.errorDialog("Backup failed. Check log.");
                    }
                } catch (Exception ex) {
                    appendLog("✗ ERROR: " + ex.getMessage());
                    appendLog("=".repeat(60) + "\n");
                    DialogUtils.errorDialog("Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private boolean backupDatabase(Connection conn, PrintWriter writer, String dbName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData. getTables(null, "public", "%", new String[]{"TABLE"});

            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (! tableName.startsWith("pg_") && !tableName.startsWith("sql_")) {
                    tableNames.add(tableName);
                }
            }
            tables. close();

            appendLog(dbName + ": Found " + tableNames.size() + " tables");

            for (String tableName : tableNames) {
                appendLog(dbName + ": Backing up " + tableName);

                writer.println("DROP TABLE IF EXISTS " + tableName + " CASCADE;");
                writer.println(getCreateTableStatement(conn, tableName));
                writer. println();

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData. getColumnCount();
                int rowCount = 0;

                while (rs.next()) {
                    StringBuilder insert = new StringBuilder("INSERT INTO " + tableName + " VALUES (");

                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);

                        if (value == null) {
                            insert.append("NULL");
                        } else if (value instanceof String || value instanceof Date ||
                                value instanceof Time || value instanceof Timestamp) {
                            insert.append("'").append(value. toString(). replace("'", "''")).append("'");
                        } else if (value instanceof Boolean) {
                            insert.append((Boolean) value ? "TRUE" : "FALSE");
                        } else {
                            insert.append(value);
                        }

                        if (i < columnCount) insert.append(", ");
                    }

                    insert.append(");");
                    writer.println(insert. toString());
                    rowCount++;
                }

                writer.println();
                appendLog(dbName + ":   → " + rowCount + " rows");
                rs.close();
                stmt.close();
            }

            appendLog(dbName + ": Complete (" + tableNames.size() + " tables)");
            return true;

        } catch (Exception e) {
            appendLog(dbName + ": Error - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String getCreateTableStatement(Connection conn, String tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (\n");

        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet columns = metaData. getColumns(null, "public", tableName, null);
        List<String> columnDefs = new ArrayList<>();

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            String isNullable = columns.getString("IS_NULLABLE");

            StringBuilder col = new StringBuilder("    " + columnName + " ");

            switch (columnType. toUpperCase()) {
                case "SERIAL": col.append("SERIAL"); break;
                case "VARCHAR": col.append("VARCHAR("). append(columnSize).append(")"); break;
                case "INT4": case "INTEGER": col.append("INTEGER"); break;
                case "INT8": case "BIGINT": col.append("BIGINT"); break;
                case "FLOAT8": case "DOUBLE PRECISION": col.append("DOUBLE PRECISION"); break;
                case "BOOL": case "BOOLEAN": col.append("BOOLEAN"); break;
                case "DATE": col.append("DATE"); break;
                case "TIME": col.append("TIME"); break;
                case "TIMESTAMP": col. append("TIMESTAMP"); break;
                case "TEXT": col.append("TEXT"); break;
                default: col.append(columnType);
            }

            if ("NO".equals(isNullable)) col.append(" NOT NULL");
            columnDefs.add(col.toString());
        }
        columns.close();

        sql.append(String.join(",\n", columnDefs));

        ResultSet pks = metaData.getPrimaryKeys(null, "public", tableName);
        List<String> pkColumns = new ArrayList<>();
        while (pks.next()) {
            pkColumns.add(pks. getString("COLUMN_NAME"));
        }
        pks.close();

        if (! pkColumns.isEmpty()) {
            sql.append(",\n    PRIMARY KEY (").append(String.join(", ", pkColumns)).append(")");
        }

        sql. append("\n);");
        return sql. toString();
    }

    private void restoreBackup() {
        if (adminService.isMaintenanceMode()) {
            DialogUtils.errorDialog("Cannot restore during maintenance mode.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Backup File");

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File backupFile = chooser.getSelectedFile();
        if (!backupFile.exists()) {
            DialogUtils.errorDialog("File does not exist.");
            return;
        }

        if (!DialogUtils.confirmDialog(
                "⚠️  WARNING\n\n" +
                        "This will OVERWRITE BOTH databases (ERP + Auth)!\n\n" +
                        "File: " + backupFile.getName() + "\n\n" +
                        "Continue?")) {
            return;
        }

        final String dumpFile = backupFile.getAbsolutePath();

        appendLog("\n" + "=".repeat(60));
        appendLog("STARTING FULL RESTORE");
        appendLog("=".repeat(60));
        appendLog("Source: " + dumpFile);
        appendLog("");

        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try (BufferedReader reader = new BufferedReader(new FileReader(dumpFile))) {

                    Connection currentConn = null;
                    Statement stmt = null;
                    StringBuilder sqlBuilder = new StringBuilder();
                    String line;
                    int count = 0;
                    String currentDatabase = "ERP"; // Start with ERP

                    // Open initial connection
                    currentConn = DBConnection.getErpConnection();
                    currentConn.setAutoCommit(false);
                    stmt = currentConn. createStatement();

                    appendLog("Restoring to ERP database...");

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        // Check if we're switching to Auth database
                        if (line. contains("-- Auth Database")) {
                            // Close current connection and switch
                            if (stmt != null) {
                                currentConn.commit();
                                stmt.close();
                            }
                            if (currentConn != null) {
                                currentConn.close();
                            }

                            appendLog("Switching to Auth database...");
                            currentDatabase = "Auth";
                            currentConn = DBConnection. getAuthConnection();
                            currentConn.setAutoCommit(false);
                            stmt = currentConn.createStatement();
                            continue;
                        }

                        if (line.isEmpty() || line.startsWith("--")) continue;

                        sqlBuilder.append(line). append(" ");

                        if (line.endsWith(";")) {
                            try {
                                stmt.execute(sqlBuilder.toString());
                                count++;
                                if (count % 100 == 0) {
                                    appendLog("Executed " + count + " statements.. .");
                                }
                            } catch (SQLException e) {
                                appendLog("Warning: " + e.getMessage());
                            }
                            sqlBuilder = new StringBuilder();
                        }
                    }

                    // Commit and close final connection
                    if (stmt != null) {
                        currentConn.commit();
                        stmt.close();
                    }
                    if (currentConn != null) {
                        currentConn.close();
                    }

                    appendLog("Executed " + count + " statements total");
                    return true;

                } catch (Exception e) {
                    appendLog("Error: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                backupButton.setEnabled(true);
                restoreButton. setEnabled(true);

                try {
                    if (get()) {
                        appendLog("✓ SUCCESS");
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils.successDialog("Full database restored!");
                    } else {
                        appendLog("✗ FAILED");
                        appendLog("=".repeat(60) + "\n");
                        DialogUtils. errorDialog("Restore failed. Check log.");
                    }
                } catch (Exception ex) {
                    appendLog("✗ ERROR: " + ex.getMessage());
                    appendLog("=". repeat(60) + "\n");
                    DialogUtils.errorDialog("Error: " + ex.getMessage());
                }
            }
        }. execute();
    }

    public void refresh() {
        updateButtonStates();
        revalidate();
        repaint();
    }
}