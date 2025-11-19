package edu.univ.erp.dao;

import edu.univ.erp.data.DBConnection;
import edu.univ.erp.domain.Faculty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public Faculty getFacultyByUserId(int userId) {
        String sql = "SELECT faculty_id, user_id, department_id, designation, full_name FROM faculty WHERE user_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Faculty(
                            rs.getInt("faculty_id"),
                            rs.getInt("user_id"),
                            rs.getInt("department_id"),
                            rs.getString("designation"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Faculty getFacultyById(int facultyId) {
        String sql = "SELECT faculty_id, user_id, department_id, designation, full_name FROM faculty WHERE faculty_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Faculty(
                            rs.getInt("faculty_id"),
                            rs.getInt("user_id"),
                            rs.getInt("department_id"),
                            rs.getString("designation"),
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Faculty> getAllFaculty() {
        String sql = "SELECT faculty_id, user_id, department_id, designation, full_name FROM faculty ORDER BY full_name";
        List<Faculty> list = new ArrayList<>();
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Faculty(
                        rs.getInt("faculty_id"),
                        rs.getInt("user_id"),
                        rs.getInt("department_id"),
                        rs.getString("designation"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertFaculty(Faculty f) {
        String sql = "INSERT INTO faculty (user_id, department_id, designation, full_name) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, f.getUserId());
            ps.setInt(2, f.getDepartmentId());
            ps.setString(3, f.getDesignation());
            ps.setString(4, f.getFullName());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                System.out.println("Faculty with this user_id already exists!");
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFaculty(Faculty f) {
        String sql = "UPDATE faculty SET department_id = ?, designation = ?, full_name = ? WHERE faculty_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, f.getDepartmentId());
            ps.setString(2, f.getDesignation());
            ps.setString(3, f.getFullName());
            ps.setInt(4, f.getFacultyId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFaculty(int facultyId) {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        try (Connection c = DBConnection.getErpConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}