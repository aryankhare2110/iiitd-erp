package edu.univ.erp.test;

import edu.univ.erp.domain.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Factory class for creating test data.
 * Provides convenient methods to insert test data into the database.
 */
public class TestDataFactory {

    /**
     * Create a test department and return its ID.
     */
    public static int createTestDepartment(Connection conn, String code, String name) throws SQLException {
        String sql = "INSERT INTO departments (code, name) VALUES (?, ?) RETURNING department_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create department");
        }
    }

    /**
     * Create a test user in auth database and return user ID.
     */
    public static int createTestUser(Connection authConn, String email, String role, String passwordHash)
            throws SQLException {
        String sql = "INSERT INTO users_auth (email, role, password_hash) VALUES (?, ?, ?) RETURNING user_id";
        try (PreparedStatement ps = authConn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, role);
            ps.setString(3, passwordHash);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create user");
        }
    }

    /**
     * Create a test student and return student ID.
     */
    public static int createTestStudent(Connection erpConn, int userId, String degreeLevel,
            String branch, int year, String term,
            String rollNo, String fullName) throws SQLException {
        String sql = "INSERT INTO students (user_id, degree_level, branch, year, term, roll_no, full_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING student_id";
        try (PreparedStatement ps = erpConn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, degreeLevel);
            ps.setString(3, branch);
            ps.setInt(4, year);
            ps.setString(5, term);
            ps.setString(6, rollNo);
            ps.setString(7, fullName);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create student");
        }
    }

    /**
     * Create a test faculty and return faculty ID.
     */
    public static int createTestFaculty(Connection erpConn, int userId, int departmentId,
            String designation, String fullName) throws SQLException {
        String sql = "INSERT INTO faculty (user_id, department_id, designation, full_name) " +
                "VALUES (?, ?, ?, ?) RETURNING faculty_id";
        try (PreparedStatement ps = erpConn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, departmentId);
            ps.setString(3, designation);
            ps.setString(4, fullName);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create faculty");
        }
    }

    /**
     * Create a test course and return course ID.
     */
    public static int createTestCourse(Connection erpConn, int departmentId, String code,
            String title, int credits, String prerequisites) throws SQLException {
        String sql = "INSERT INTO courses (department_id, code, title, credits, prerequisites) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING course_id";
        try (PreparedStatement ps = erpConn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setString(2, code);
            ps.setString(3, title);
            ps.setInt(4, credits);
            ps.setString(5, prerequisites);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create course");
        }
    }

    /**
     * Create a test section and return section ID.
     */
    public static int createTestSection(Connection erpConn, int courseId, int instructorId,
            String term, int year, String room, int capacity) throws SQLException {
        String sql = "INSERT INTO sections (course_id, instructor_id, term, year, room, capacity) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING section_id";
        try (PreparedStatement ps = erpConn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, instructorId);
            ps.setString(3, term);
            ps.setInt(4, year);
            ps.setString(5, room);
            ps.setInt(6, capacity);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create section");
        }
    }

    /**
     * Create a test enrollment and return enrollment ID.
     */
    public static int createTestEnrollment(Connection erpConn, int studentId, int sectionId,
            String status) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) " +
                "VALUES (?, ?, ?) RETURNING enrollment_id";
        try (PreparedStatement ps = erpConn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.setString(3, status);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to create enrollment");
        }
    }

    /**
     * Convenience method to create section with default term/year.
     */
    public static int createTestSection(Connection erpConn, int courseId, int instructorId,
            String sectionName, int capacity, String room) throws SQLException {
        return createTestSection(erpConn, courseId, instructorId, "Monsoon", 2024, room, capacity);
    }

    /**
     * Convenience method to create enrollment with default ENROLLED status.
     */
    public static int createTestEnrollment(Connection erpConn, int studentId, int sectionId) throws SQLException {
        return createTestEnrollment(erpConn, studentId, sectionId, "ENROLLED");
    }

    /**
     * Get a sample Student object for testing.
     */
    public static Student getSampleStudent(int userId) {
        return new Student(
                0, // student_id will be set by database
                userId,
                "B.Tech",
                "CSE",
                2,
                "Monsoon",
                "2024001",
                "Test Student");
    }
}
