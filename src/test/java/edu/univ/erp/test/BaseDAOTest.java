package edu.univ.erp.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

/**
 * Base class for all DAO tests.
 * Provides common setup and cleanup functionality.
 */
public abstract class BaseDAOTest {

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        System.out.println("Setting up test databases...");
        TestDatabaseManager.initializeTestDatabases();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        // Clean databases before each test to ensure isolation
        TestDatabaseManager.cleanAllDatabases();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up after each test
        TestDatabaseManager.cleanAllDatabases();
    }

    // Don't close connection pools between test classes - let JVM handle cleanup
    // This allows multiple test classes to share the same database connection pools

    /**
     * Helper method to get auth database connection for tests.
     */
    protected java.sql.Connection getAuthConnection() throws SQLException {
        return TestDBConnection.getAuthConnection();
    }

    /**
     * Helper method to get ERP database connection for tests.
     */
    protected java.sql.Connection getErpConnection() throws SQLException {
        return TestDBConnection.getErpConnection();
    }
}
