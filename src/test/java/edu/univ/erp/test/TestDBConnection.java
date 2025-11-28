package edu.univ.erp.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test-specific database connection manager.
 * Uses separate test databases to avoid interfering with development data.
 */
public class TestDBConnection {

    private static final HikariDataSource authDS;
    private static final HikariDataSource erpDS;
    private static Properties dbProps;

    static {
        // Load test database properties
        dbProps = new Properties();
        try (InputStream input = TestDBConnection.class.getClassLoader()
                .getResourceAsStream("test-db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find test-db.properties");
            }
            dbProps.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test database properties", e);
        }

        // Initialize auth database connection pool
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl(dbProps.getProperty("db.auth.url"));
        authConfig.setUsername(dbProps.getProperty("db.username"));
        authConfig.setPassword(dbProps.getProperty("db.password"));
        authConfig.setMaximumPoolSize(Integer.parseInt(
                dbProps.getProperty("db.pool.max.size", "5")));
        authConfig.setPoolName(dbProps.getProperty("db.pool.name.auth"));
        authDS = new HikariDataSource(authConfig);

        // Initialize ERP database connection pool
        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl(dbProps.getProperty("db.erp.url"));
        erpConfig.setUsername(dbProps.getProperty("db.username"));
        erpConfig.setPassword(dbProps.getProperty("db.password"));
        erpConfig.setMaximumPoolSize(Integer.parseInt(
                dbProps.getProperty("db.pool.max.size", "5")));
        erpConfig.setPoolName(dbProps.getProperty("db.pool.name.erp"));
        erpDS = new HikariDataSource(erpConfig);
    }

    /**
     * Get a connection to the test auth database.
     */
    public static Connection getAuthConnection() throws SQLException {
        return authDS.getConnection();
    }

    /**
     * Get a connection to the test ERP database.
     */
    public static Connection getErpConnection() throws SQLException {
        return erpDS.getConnection();
    }

    /**
     * Close all connection pools.
     * Should be called in test suite cleanup if needed.
     */
    public static void close() {
        if (authDS != null && !authDS.isClosed()) {
            authDS.close();
        }
        if (erpDS != null && !erpDS.isClosed()) {
            erpDS.close();
        }
    }

    /**
     * Get database properties for testing purposes.
     */
    public static Properties getProperties() {
        return dbProps;
    }
}
