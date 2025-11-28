package edu.univ.erp.service;

import edu.univ.erp.test.BaseServiceTest;
import edu.univ.erp.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AdminService Tests")
class AdminServiceTest extends BaseServiceTest {

    private int testDepartmentId;

    @BeforeEach
    public void setUp() throws java.sql.SQLException {
        super.setUp();

        // Set up test data
        testDepartmentId = TestDataFactory.createTestDepartment(
                getErpConnection(), "CSE", "Computer Science");
    }

    @Test
    @DisplayName("Should manage system-wide statistics (students, faculty, courses)")
    void testSystemStatistics() throws Exception {
        // Given - Create test data
        TestDataFactory.createTestStudent(
                getErpConnection(), 100, "B.Tech", "CSE", 2, "Monsoon", "2024001", "Student 1");
        TestDataFactory.createTestStudent(
                getErpConnection(), 101, "B.Tech", "CSE", 2, "Monsoon", "2024002", "Student 2");

        TestDataFactory.createTestFaculty(
                getErpConnection(), 200, testDepartmentId, "Professor", "Faculty 1");

        TestDataFactory.createTestCourse(getErpConnection(), testDepartmentId, "CSE101", "Course 1", 4, null);

        // When - Get counts
        String studentsSql = "SELECT COUNT(*) FROM students";
        String facultySql = "SELECT COUNT(*) FROM faculty";
        String coursesSql = "SELECT COUNT(*) FROM courses";

        try (Connection conn = getErpConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(studentsSql);
                    ResultSet rs = ps.executeQuery()) {
                rs.next();
                assertThat(rs.getInt(1)).isEqualTo(2);
            }

            try (PreparedStatement ps = conn.prepareStatement(facultySql);
                    ResultSet rs = ps.executeQuery()) {
                rs.next();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(coursesSql);
                    ResultSet rs = ps.executeQuery()) {
                rs.next();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }
        }
    }

    @Test
    @DisplayName("Should manage user access (activate/deactivate accounts)")
    void testUserAccessManagement() throws Exception {
        // Given - Create user
        int userId = TestDataFactory.createTestUser(
                getAuthConnection(), "user@example.com", "STUDENT", "hash");

        // When - Deactivate user
        String updateSql = "UPDATE users_auth SET status = ? WHERE user_id = ?";
        try (Connection conn = getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, "INACTIVE");
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            assertThat(rows).isEqualTo(1);
        }

        // Then - Verify status changed
        String selectSql = "SELECT status FROM users_auth WHERE user_id = ?";
        try (Connection conn = getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("status")).isEqualTo("INACTIVE");
        }

        // And - Reactivate user
        try (Connection conn = getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, "ACTIVE");
            ps.setInt(2, userId);
            ps.executeUpdate();
        }

        try (Connection conn = getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            assertThat(rs.getString("status")).isEqualTo("ACTIVE");
        }
    }

    @Test
    @DisplayName("Should manage course catalog (create, update, delete)")
    void testCourseCatalogManagement() throws Exception {
        // Given - Create course
        int courseId = TestDataFactory.createTestCourse(
                getErpConnection(), testDepartmentId, "CSE101", "Original Title", 4, null);

        // When - Update course
        String updateSql = "UPDATE courses SET title = ?, credits = ? WHERE course_id = ?";
        try (Connection conn = getErpConnection();
                PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, "Updated Title");
            ps.setInt(2, 3);
            ps.setInt(3, courseId);
            int rows = ps.executeUpdate();
            assertThat(rows).isEqualTo(1);
        }

        // Then - Verify update
        String selectSql = "SELECT title, credits FROM courses WHERE course_id = ?";
        try (Connection conn = getErpConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("title")).isEqualTo("Updated Title");
            assertThat(rs.getInt("credits")).isEqualTo(3);
        }

        // And - Delete course
        String deleteSql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = getErpConnection();
                PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }

        try (Connection conn = getErpConnection();
                PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isFalse();
        }
    }
}
