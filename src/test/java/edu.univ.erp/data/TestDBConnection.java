package edu. univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java. sql.Connection;
import java. sql.SQLException;
import java. util.Properties;

public class TestDBConnection {

    private static final HikariDataSource authTestDS;
    private static final HikariDataSource erpTestDS;

    static {
        Properties props = new Properties();
        try (InputStream input = TestDBConnection. class.getClassLoader().getResourceAsStream("test-db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find test-db.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test database properties", e);
        }

        String username = props.getProperty("test.db.username");
        String password = props.getProperty("test.db.password");
        String authUrl = props.getProperty("test.db.auth.url");
        String erpUrl = props. getProperty("test.db.erp.url");

        // Auth test database connection pool
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl(authUrl);
        authConfig.setUsername(username);
        authConfig.setPassword(password);
        authConfig.setMaximumPoolSize(5);
        authConfig.setPoolName("authTestDBPool");
        authTestDS = new HikariDataSource(authConfig);

        // ERP test database connection pool
        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl(erpUrl);
        erpConfig.setUsername(username);
        erpConfig.setPassword(password);
        erpConfig.setMaximumPoolSize(5);
        erpConfig.setPoolName("erpTestDBPool");
        erpTestDS = new HikariDataSource(erpConfig);
    }

    public static Connection getAuthConnection() throws SQLException {
        return authTestDS. getConnection();
    }

    public static Connection getErpConnection() throws SQLException {
        return erpTestDS.getConnection();
    }

    public static void close() {
        if (authTestDS != null) {
            authTestDS.close();
        }
        if (erpTestDS != null) {
            erpTestDS.close();
        }
    }
}