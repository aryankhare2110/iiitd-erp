package edu.univ.erp.dao;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.test.BaseDAOTest;
import edu.univ.erp. test.TestDataHelper;
import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter. api.Assertions.*;

/**
 * JUnit 5 tests for AuthDAO using local PostgreSQL test database.
 */
class AuthDAOTest extends BaseDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        authDAO = new AuthDAO();
    }

    @Test
    void testRegisterNewUser() {
        // Act
        boolean result = authDAO. registerNewUser(
                "newuser@iiitd.ac.in",
                "STUDENT",
                "$argon2id$v=19$m=15360,t=3,p=2$testhash"
        );

        // Assert
        assertTrue(result, "User should be registered successfully");
        assertTrue(authDAO.emailChecker("newuser@iiitd.ac.in"), "User should exist after registration");
    }

    @Test
    void testEmailChecker() throws SQLException {
        // Arrange
        TestDataHelper.insertTestUser("existing@iiitd.ac. in", "STUDENT", "hash123");

        // Act & Assert
        assertTrue(authDAO. emailChecker("existing@iiitd.ac.in"), "Existing email should return true");
        assertFalse(authDAO.emailChecker("nonexistent@iiitd.ac.in"), "Non-existent email should return false");
    }

    @Test
    void testGetUserId() throws SQLException {
        // Arrange
        int userId = TestDataHelper.insertTestUser("getid@iiitd.ac. in", "FACULTY", "hash456");

        // Act
        Integer retrievedId = authDAO.getUserId("getid@iiitd.ac.in");

        // Assert
        assertNotNull(retrievedId, "User ID should be found");
        assertEquals(userId, retrievedId, "Retrieved user ID should match");
    }

    @Test
    void testGetRole() throws SQLException {
        // Arrange
        TestDataHelper.insertTestUser("faculty@iiitd.ac. in", "FACULTY", "hash789");

        // Act
        String role = authDAO.getRole("faculty@iiitd.ac.in");

        // Assert
        assertEquals("FACULTY", role, "Role should be FACULTY");
    }

    @Test
    void testGetStatus() throws SQLException {
        // Arrange
        TestDataHelper.insertTestUser("status@iiitd.ac.in", "ADMIN", "hash000");

        // Act
        String status = authDAO.getStatus("status@iiitd.ac.in");

        // Assert
        assertEquals("ACTIVE", status, "Default status should be ACTIVE");
    }

    @Test
    void testGetHashedPassword() throws SQLException {
        // Arrange
        String passwordHash = "$argon2id$v=19$m=15360,t=3,p=2$specialhash";
        TestDataHelper.insertTestUser("password@iiitd.ac. in", "STUDENT", passwordHash);

        // Act
        String retrievedHash = authDAO.getHashedPassword("password@iiitd.ac.in");

        // Assert
        assertEquals(passwordHash, retrievedHash, "Password hash should match");
    }

    @Test
    void testResetPassword() throws SQLException {
        // Arrange
        TestDataHelper.insertTestUser("reset@iiitd.ac.in", "STUDENT", "oldhash");
        String newHash = "$argon2id$v=19$m=15360,t=3,p=2$newhash";

        // Act
        boolean result = authDAO.resetPassword("reset@iiitd.ac.in", newHash);

        // Assert
        assertTrue(result, "Password should be reset successfully");
        assertEquals(newHash, authDAO.getHashedPassword("reset@iiitd.ac.in"),
                "New password hash should be stored");
    }

    @Test
    void testUpdateStatus() throws SQLException {
        // Arrange
        int userId = TestDataHelper.insertTestUser("statuschange@iiitd.ac. in", "STUDENT", "hash");

        // Act
        boolean result = authDAO.updateStatus(userId, "SUSPENDED");

        // Assert
        assertTrue(result, "Status should be updated successfully");
        assertEquals("SUSPENDED", authDAO.getStatusByUserId(userId), "Status should be SUSPENDED");
    }

    @Test
    void testGetEmailByUserId() throws SQLException {
        // Arrange
        int userId = TestDataHelper.insertTestUser("findemail@iiitd.ac.in", "FACULTY", "hash");

        // Act
        String email = authDAO.getEmailByUserId(userId);

        // Assert
        assertEquals("findemail@iiitd.ac.in", email, "Email should match");
    }

    @Test
    void testUpdateLastLogin() throws SQLException {
        // Arrange
        TestDataHelper.insertTestUser("login@iiitd.ac. in", "STUDENT", "hash");

        // Act
        boolean result = authDAO.updateLastLogin("login@iiitd.ac.in");

        // Assert
        assertTrue(result, "Last login timestamp should be updated");
    }
}