package edu.univ.erp.test;

import edu.univ.erp.data.TestDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java. sql.SQLException;

/**
 * Helper class for creating test data in the database.
 */
public class TestDataHelper {

    /**
     * Insert a test user into auth_db_test and return the generated user_id.
     */
    public static int insertTestUser(String email, String role, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users_auth (email, role, password_hash, status) VALUES (?, ?, ?, 'ACTIVE') RETURNING user_id";
        try (Connection conn = TestDBConnection.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        throw new SQLException("Failed to insert test user");
    }

    /**
     * Insert a test department and return the generated department_id.
     */
    public static int insertTestDepartment(String name) throws SQLException {
        String sql = "INSERT INTO departments (name) VALUES (?) RETURNING department_id";
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps. executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("department_id");
                }
            }
        }
        throw new SQLException("Failed to insert test department");
    }

    /**
     * Insert a test student and return the generated student_id.
     */
    public static int insertTestStudent(int userId, String degreeLevel, String branch,
                                        int year, String term, String rollNo, String fullName) throws SQLException {
        String sql = "INSERT INTO students (user_id, degree_level, branch, year, term, roll_no, full_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING student_id";
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, degreeLevel);
            ps.setString(3, branch);
            ps.setInt(4, year);
            ps.setString(5, term);
            ps.setString(6, rollNo);
            ps.setString(7, fullName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs. getInt("student_id");
                }
            }
        }
        throw new SQLException("Failed to insert test student");
    }

    /**
     * Insert a test course and return the generated course_id.
     */
    public static int insertTestCourse(int departmentId, String code, String title,
                                       int credits, String prerequisites) throws SQLException {
        String sql = "INSERT INTO courses (department_id, code, title, credits, prerequisites) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING course_id";
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setString(2, code);
            ps.setString(3, title);
            ps.setInt(4, credits);
            ps.setString(5, prerequisites);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        }
        throw new SQLException("Failed to insert test course");
    }

    /**
     * Insert a test faculty member and return the generated faculty_id.
     */
    public static int insertTestFaculty(int userId, int departmentId, String designation, String fullName) throws SQLException {
        String sql = "INSERT INTO faculty (user_id, department_id, designation, full_name) " +
                "VALUES (?, ?, ?, ?) RETURNING faculty_id";
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, departmentId);
            ps.setString(3, designation);
            ps.setString(4, fullName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("faculty_id");
                }
            }
        }
        throw new SQLException("Failed to insert test faculty");
    }

    /**
     * Insert a test section and return the generated section_id.
     */
    public static int insertTestSection(int courseId, int instructorId, String term,
                                        int year, String room, int capacity) throws SQLException {
        String sql = "INSERT INTO sections (course_id, instructor_id, term, year, room, capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING section_id";
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps. setInt(2, instructorId);
            ps.setString(3, term);
            ps. setInt(4, year);
            ps.setString(5, room);
            ps.setInt(6, capacity);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("section_id");
                }
            }
        }
        throw new SQLException("Failed to insert test section");
    }
}