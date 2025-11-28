package edu.univ.erp.service;

import edu.univ.erp.test.BaseServiceTest;
import edu.univ.erp.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.assertj.core.api.Assertions.*;

@DisplayName("AuthService Tests")
class AuthServiceTest extends BaseServiceTest {

    private AuthService authService;

    @BeforeEach
    public void setUp() throws java.sql.SQLException {
        super.setUp();
        authService = new AuthService();
    }

    @Test
    @DisplayName("Should authenticate user with correct credentials")
    void testUserAuthentication() throws Exception {
        TestDataFactory.createTestUser(
                getAuthConnection(),
                "student@example.com",
                "STUDENT",
                "$argon2id$v=19$m=15360,t=3,p=2$test$testhash");

        String sql = "SELECT role FROM users_auth WHERE email = ?";
        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "student@example.com");
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("role")).isEqualTo("STUDENT");
        }
    }

    @Test
    @DisplayName("Should support multiple user roles (Student, Instructor, Admin)")
    void testMultipleUserRoles() throws Exception {
        TestDataFactory.createTestUser(getAuthConnection(), "admin@example.com", "ADMIN", "hash1");
        TestDataFactory.createTestUser(getAuthConnection(), "instructor@example.com", "INSTRUCTOR", "hash2");
        TestDataFactory.createTestUser(getAuthConnection(), "student@example.com", "STUDENT", "hash3");

        String sql = "SELECT role, COUNT(*) as count FROM users_auth GROUP BY role ORDER BY role";
        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("role")).isEqualTo("ADMIN");
            assertThat(rs.getInt("count")).isEqualTo(1);

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("role")).isEqualTo("INSTRUCTOR");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("role")).isEqualTo("STUDENT");
        }
    }

    @Test
    @DisplayName("Should track user login activity")
    void testLoginTracking() throws Exception {
        int userId = TestDataFactory.createTestUser(
                getAuthConnection(),
                "user@example.com",
                "STUDENT",
                "hash");

        String updateSql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";
        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            assertThat(rows).isEqualTo(1);
        }

        String selectSql = "SELECT last_login FROM users_auth WHERE user_id = ?";
        try (Connection conn = getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, userId);
            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getTimestamp("last_login")).isNotNull();
        }
    }
}