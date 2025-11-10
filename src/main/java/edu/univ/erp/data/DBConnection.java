package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig; //HikariConfig -> Connection settings for connections from the pool
import com.zaxxer.hikari.HikariDataSource; //HikariDataSource -> Connection pool (multiple connections)
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    //PostgreSQL details
    private static final String username = "postgres";
    private static final String password = "123456";
    private static final String url = "jdbc:postgresql://localhost:5432/auth_db";

    //Actual connection pools (Separate)
    private static final HikariDataSource authDS; //Auth Data Source
    private static final HikariDataSource erpDS; //ERP Data Source

    static { //Runs once

        //auth_db connection config
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl(url);
        authConfig.setUsername(username);
        authConfig.setPassword(password);
        authConfig.setMaximumPoolSize(10);
        authConfig.setPoolName("authDBPool");
        authDS = new HikariDataSource(authConfig);

        //erp_db connection config
        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl(url);
        erpConfig.setUsername(username);
        erpConfig.setPassword(password);
        erpConfig.setMaximumPoolSize(10);
        erpConfig.setPoolName("erpDBPool");
        erpDS = new HikariDataSource(erpConfig);

    }

    //Getter function for authConnection
    public static Connection getAuthConnection() throws SQLException {
        return authDS.getConnection();
    }

    //Getter function for erpConnection
    public static Connection getErpConnection() throws SQLException {
        return erpDS.getConnection();
    }

    public static void close() {
        if (authDS != null) {
            authDS.close();
        }
        if (erpDS != null) {
            erpDS.close();
        }
    }

    //Test
    public static void main(String[] args) {
        try(Connection conn = getAuthConnection()) {
            System.out.println("Connected to AuthDB: " + !conn.isClosed());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
