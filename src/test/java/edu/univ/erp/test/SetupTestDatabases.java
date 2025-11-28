package edu.univ.erp.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utility to set up test databases.
 * Run this once before running tests.
 */
public class SetupTestDatabases {

    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        try {
            System.out.println("Setting up test databases...");
            TestDatabaseManager.initializeTestDatabases();
            System.out.println("✓ Test databases created and initialized successfully!");
            System.out.println("✓ Schemas applied");
            System.out.println("\nYou can now run: mvn test");
        } catch (Exception e) {
            System.err.println("Error setting up test databases:");
            e.printStackTrace();
            System.err.println("\nPlease ensure:");
            System.err.println("1. PostgreSQL is running on localhost:5432");
            System.err.println("2. Username 'postgres' with password '123456' has access");
            System.err.println("3. User has permission to create databases");
        }
    }
}
