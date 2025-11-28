package edu.univ.erp.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

public abstract class BaseServiceTest {

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        System.out.println("Setting up test databases for service tests...");
        TestDatabaseManager.initializeTestDatabases();
    }

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseManager.cleanAllDatabases();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        TestDatabaseManager.cleanAllDatabases();
    }

    protected java.sql.Connection getAuthConnection() throws SQLException {
        return TestDBConnection.getAuthConnection();
    }

    protected java.sql.Connection getErpConnection() throws SQLException {
        return TestDBConnection.getErpConnection();
    }
}