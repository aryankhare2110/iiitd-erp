package edu.univ.erp.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class TestDatabaseManager {

    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "123456";
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String AUTH_DB_NAME = "auth_db_test";
    private static final String ERP_DB_NAME = "erp_db_test";

    public static void initializeTestDatabases() throws Exception {
        createDatabaseIfNotExists(AUTH_DB_NAME);
        createDatabaseIfNotExists(ERP_DB_NAME);
        setupAuthSchema();
        setupErpSchema();
    }

    private static void createDatabaseIfNotExists(String dbName) throws SQLException {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");

            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE " + dbName);
                System.out.println("Created test database: " + dbName);
            }
        }
    }

    private static void setupAuthSchema() throws Exception {
        String schema = loadResourceFile("/test-auth-schema.sql");
        executeSchema(TestDBConnection.getAuthConnection(), schema);
        System.out.println("Auth schema initialized");
    }

    private static void setupErpSchema() throws Exception {
        String schema = loadResourceFile("/test-erp-schema.sql");
        executeSchema(TestDBConnection.getErpConnection(), schema);
        System.out.println("ERP schema initialized");
    }

    private static void executeSchema(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static String loadResourceFile(String resourcePath) throws Exception {
        try (InputStream is = TestDatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);
            return new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    public static void cleanAuthDatabase() throws SQLException {
        try (Connection conn = TestDBConnection.getAuthConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE users_auth RESTART IDENTITY CASCADE");
        }
    }

    public static void cleanErpDatabase() throws SQLException {
        try (Connection conn = TestDBConnection.getErpConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(
                    "TRUNCATE TABLE grades, component_scores, section_components, enrollments, sections, component_types, courses, students, faculty, departments, settings, notifications RESTART IDENTITY CASCADE");
        }
    }

    public static void cleanAllDatabases() throws SQLException {
        cleanAuthDatabase();
        cleanErpDatabase();
    }

    public static void dropTestDatabases() throws SQLException {
        TestDBConnection.close();

        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                    "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '" +
                            AUTH_DB_NAME + "' AND pid <> pg_backend_pid()");

            stmt.execute(
                    "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '" +
                            ERP_DB_NAME + "' AND pid <> pg_backend_pid()");

            stmt.execute("DROP DATABASE IF EXISTS " + AUTH_DB_NAME);
            stmt.execute("DROP DATABASE IF EXISTS " + ERP_DB_NAME);

            System.out.println("Dropped test databases");
        }
    }
}