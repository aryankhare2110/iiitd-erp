package edu.univ.erp.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Manages test database lifecycle including creation, schema setup, and
 * cleanup.
 */
public class TestDatabaseManager {

    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String AUTH_DB_NAME = "auth_db_test";
    private static final String ERP_DB_NAME = "erp_db_test";

    /**
     * Initialize test databases. Creates databases if they don't exist and sets up
     * schemas.
     */
    public static void initializeTestDatabases() throws Exception {
        createDatabaseIfNotExists(AUTH_DB_NAME);
        createDatabaseIfNotExists(ERP_DB_NAME);
        setupAuthSchema();
        setupErpSchema();
    }

    /**
     * Create a database if it doesn't exist.
     */
    private static void createDatabaseIfNotExists(String dbName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, DB_USERNAME, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Check if database exists
            String checkSql = String.format(
                    "SELECT 1 FROM pg_database WHERE datname = '%s'", dbName);
            var rs = stmt.executeQuery(checkSql);

            if (!rs.next()) {
                // Database doesn't exist, create it
                String createSql = String.format("CREATE DATABASE %s", dbName);
                stmt.executeUpdate(createSql);
                System.out.println("Created test database: " + dbName);
            }
        }
    }

    /**
     * Set up the auth database schema.
     */
    private static void setupAuthSchema() throws Exception {
        String schema = loadResourceFile("/test-auth-schema.sql");
        executeSchema(TestDBConnection.getAuthConnection(), schema);
        System.out.println("Auth schema initialized");
    }

    /**
     * Set up the ERP database schema.
     */
    private static void setupErpSchema() throws Exception {
        String schema = loadResourceFile("/test-erp-schema.sql");
        executeSchema(TestDBConnection.getErpConnection(), schema);
        System.out.println("ERP schema initialized");
    }

    /**
     * Execute SQL schema script.
     */
    private static void executeSchema(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Load a resource file as string.
     */
    private static String loadResourceFile(String resourcePath) throws Exception {
        try (InputStream is = TestDatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + resourcePath);
            }
            return new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * Clean all data from auth database tables.
     */
    public static void cleanAuthDatabase() throws SQLException {
        try (Connection conn = TestDBConnection.getAuthConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE users_auth RESTART IDENTITY CASCADE");
        }
    }

    /**
     * Clean all data from ERP database tables.
     */
    public static void cleanErpDatabase() throws SQLException {
        try (Connection conn = TestDBConnection.getErpConnection();
                Statement stmt = conn.createStatement()) {
            // Clean in reverse dependency order - all in one statement execution
            stmt.execute(
                    "TRUNCATE TABLE grades, component_scores, section_components, enrollments, sections, component_types, courses, students, faculty, departments, settings, notifications RESTART IDENTITY CASCADE");
        }
    }

    /**
     * Clean all test databases.
     */
    public static void cleanAllDatabases() throws SQLException {
        cleanAuthDatabase();
        cleanErpDatabase();
    }

    /**
     * Drop test databases (use with caution, typically only for complete teardown).
     */
    public static void dropTestDatabases() throws SQLException {
        // Close all connections first
        TestDBConnection.close();

        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, DB_USERNAME, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Terminate existing connections
            stmt.execute(String.format(
                    "SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                            "FROM pg_stat_activity " +
                            "WHERE pg_stat_activity.datname = '%s' " +
                            "AND pid <> pg_backend_pid()",
                    AUTH_DB_NAME));

            stmt.execute(String.format(
                    "SELECT pg_terminate_backend(pg_stat_activity.pid) " +
                            "FROM pg_stat_activity " +
                            "WHERE pg_stat_activity.datname = '%s' " +
                            "AND pid <> pg_backend_pid()",
                    ERP_DB_NAME));

            // Drop databases
            stmt.execute(String.format("DROP DATABASE IF EXISTS %s", AUTH_DB_NAME));
            stmt.execute(String.format("DROP DATABASE IF EXISTS %s", ERP_DB_NAME));

            System.out.println("Dropped test databases");
        }
    }
}
