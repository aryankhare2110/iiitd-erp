package edu.univ.erp.auth.store;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Admin;

import java.sql.*;
import java.util.*;

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

    public Integer getUserId(String email) {
        String sql = "SELECT user_id FROM users_auth WHERE email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getStatus (String email) { //To enable and disable accounts
        String sql = "SELECT status FROM users_auth WHERE email = ?";
        try (Connection c = DBConnection.getAuthConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        String sql = "SELECT role FROM users_auth WHERE email = ?";
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

    public String getEmailByUserId(int userId) {
        String sql = "SELECT email FROM users_auth WHERE user_id = ?";
        try (Connection c = DBConnection.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Admin> getAllAdmins() {
        String sql = "SELECT user_id, email, status FROM users_auth WHERE role = 'ADMIN'";
        List<Admin> list = new ArrayList<>();

        try (Connection c = DBConnection.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Admin(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getStatusByUserId(int userId) {
        String sql = "SELECT status FROM users_auth WHERE user_id = ?";
        try (Connection c = DBConnection.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users_auth SET status = ? WHERE user_id = ?";
        try (Connection c = DBConnection.getAuthConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
