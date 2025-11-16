package edu.univ.erp.auth.store;

import edu.univ.erp.data.DBConnection;
import java.sql.*;

//authDAO is ONLY for auth_db SQL!!

public class AuthDAO {

    public boolean emailChecker (String email) { //To check if email exists
        String sql = "SELECT 1 FROM users_auth WHERE email = ?"; //"?" replaced with email in setString
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql); //Makes sure no faulty command goes to SQL
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); //True if exists, false if not
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getHashedPassword (String email) { //To get the hashed password from db
        String sql = "SELECT password_hash FROM users_auth WHERE email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash"); //Returns password as a string
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRole (String email) { //To get the role from db
        String sql = "SELECT role FROM users_auth where email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); //Returns role as a string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerNewUser (String email, String role, String password_hash) { //To add a new user to db
        String sql = "INSERT INTO users_auth (email, role, password_hash) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, role);
            ps.setString(3, password_hash);
            int rows = ps.executeUpdate(); //Adds a new row to db
            return rows == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword (String email, String new_password_hash) { //To reset password
        String sql = "UPDATE users_auth SET password_hash = ? WHERE email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, new_password_hash);
            ps.setString(2, email);
            int rows = ps.executeUpdate(); //Updates the password column in the row
            return rows == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLastLogin (String email) { //To update last_login
        String sql = "UPDATE users_auth SET last_login = CURRENT_TIMESTAMP WHERE email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
