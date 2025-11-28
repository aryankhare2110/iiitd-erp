package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig; //HikariConfig -> Connection settings for connections from the pool
import com.zaxxer.hikari.HikariDataSource; //HikariDataSource -> Connection pool (multiple connections)
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static final String username = "postgres";
    private static final String password = "123456";
    private static final String url1 = "jdbc:postgresql://localhost:5432/auth_db";
    private static final String url2 = "jdbc:postgresql://localhost:5432/erp_db";

    private static final HikariDataSource authDS; //Auth Data Source
    private static final HikariDataSource erpDS; //ERP Data Source

    static {

        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl(url1);
        authConfig.setUsername(username);
        authConfig.setPassword(password);
        authConfig.setMaximumPoolSize(10);
        authConfig.setPoolName("authDBPool");
        authDS = new HikariDataSource(authConfig);

        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl(url2);
        erpConfig.setUsername(username);
        erpConfig.setPassword(password);
        erpConfig.setMaximumPoolSize(10);
        erpConfig.setPoolName("erpDBPool");
        erpDS = new HikariDataSource(erpConfig);

    }

    public static Connection getAuthConnection() throws SQLException {
        return authDS.getConnection();
    }

    public static Connection getErpConnection() throws SQLException {
        return erpDS.getConnection();
    }
    
}
