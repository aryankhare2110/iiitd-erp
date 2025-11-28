package edu.univ.erp.test;

public class SetupTestDatabases {

    public static void main(String[] args) {
        try {
            System.out.println("Setting up test databases...");
            TestDatabaseManager.initializeTestDatabases();
            System.out.println("Test databases created.");
            System.out.println("Schemas applied.");
            System.out.println("Run: mvn test");
        } catch (Exception e) {
            System.err.println("Error setting up test databases:");
            e.printStackTrace();
        }
    }

}