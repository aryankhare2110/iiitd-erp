package edu. univ.erp.test;

import edu.univ.erp.data.TestDBConnection;
import org.junit.jupiter.api.AfterEach;
import org. junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class for DAO tests providing common setup and cleanup utilities.
 */
public abstract class BaseDAOTest {

    @BeforeEach
    public void setUp() throws SQLException {
        // Override this method in subclasses if you need specific setup
        cleanDatabase();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up test data after each test
        cleanDatabase();
    }

    /**
     * Clean all data from test databases while preserving schema.
     */
    protected void cleanDatabase() throws SQLException {
        cleanErpDatabase();
        cleanAuthDatabase();
    }

    protected void cleanErpDatabase() throws SQLException {
        try (Connection conn = TestDBConnection. getErpConnection();
             Statement stmt = conn.createStatement()) {

            // Disable foreign key checks temporarily
            stmt.execute("SET CONSTRAINTS ALL DEFERRED");

            // Delete data in reverse order of dependencies
            stmt.execute("DELETE FROM component_scores");
            stmt.execute("DELETE FROM grade_components");
            stmt.execute("DELETE FROM enrollments");
            stmt.execute("DELETE FROM sections");
            stmt.execute("DELETE FROM courses");
            stmt.execute("DELETE FROM students");
            stmt.execute("DELETE FROM faculty");
            stmt.execute("DELETE FROM departments");
            stmt.execute("DELETE FROM component_types");
            stmt.execute("DELETE FROM settings");

            // Re-enable foreign key checks
            stmt.execute("SET CONSTRAINTS ALL IMMEDIATE");
        }
    }

    protected void cleanAuthDatabase() throws SQLException {
        try (Connection conn = TestDBConnection.getAuthConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users_auth");
        }
    }

    /**
     * Execute a SQL script for test data setup.
     */
    protected void executeSql(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}