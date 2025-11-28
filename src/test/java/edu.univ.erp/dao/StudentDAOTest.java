package edu.univ.erp.dao;

import edu.univ.erp.data.TestDBConnection;
import edu.univ.erp.domain.Student;
import edu.univ.erp.test.BaseDAOTest;
import edu.univ.erp.test.TestDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java. sql.SQLException;
import java. util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for StudentDAO using local PostgreSQL test database.
 */
class StudentDAOTest extends BaseDAOTest {

    private StudentDAO studentDAO;
    private int testUserId;

    @BeforeEach
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        studentDAO = new StudentDAO();

        // Create a test user in auth_db_test
        testUserId = TestDataHelper.insertTestUser(
                "test. student@iiitd.ac.in",
                "STUDENT",
                "$argon2id$v=19$m=15360,t=3,p=2$hash" // dummy hash
        );
    }

    @Test
    void testInsertStudent() throws SQLException {
        // Arrange
        Student student = new Student(
                0, // ID will be auto-generated
                testUserId,
                "B. Tech",
                "CSE",
                2,
                "Spring",
                "2024001",
                "Test Student"
        );

        // Act
        boolean result = studentDAO.insertStudent(testUserId, student);

        // Assert
        assertTrue(result, "Student should be inserted successfully");

        // Verify in database
        try (Connection conn = TestDBConnection.getErpConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM students WHERE user_id = ?")) {
            ps. setInt(1, testUserId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Student should exist in database");
                assertEquals("2024001", rs.getString("roll_no"));
                assertEquals("Test Student", rs.getString("full_name"));
            }
        }
    }

    @Test
    void testGetStudentByUserId() throws SQLException {
        // Arrange
        int studentId = TestDataHelper.insertTestStudent(
                testUserId, "B.Tech", "CSE", 2, "Spring", "2024002", "John Doe"
        );

        // Act
        Student student = studentDAO.getStudentByUserId(testUserId);

        // Assert
        assertNotNull(student, "Student should be found");
        assertEquals(testUserId, student.getUserId());
        assertEquals("2024002", student.getRollNo());
        assertEquals("John Doe", student.getFullName());
        assertEquals("CSE", student.getBranch());
    }

    @Test
    void testGetStudentByUserId_NotFound() {
        // Act
        Student student = studentDAO.getStudentByUserId(99999);

        // Assert
        assertNull(student, "Student should not be found for non-existent user ID");
    }

    @Test
    void testGetAllStudents() throws SQLException {
        // Arrange
        int userId2 = TestDataHelper.insertTestUser("test2@iiitd.ac.in", "STUDENT", "hash");
        int userId3 = TestDataHelper.insertTestUser("test3@iiitd.ac.in", "STUDENT", "hash");

        TestDataHelper.insertTestStudent(testUserId, "B.Tech", "CSE", 2, "Spring", "2024003", "Student One");
        TestDataHelper.insertTestStudent(userId2, "B.Tech", "ECE", 3, "Fall", "2024004", "Student Two");
        TestDataHelper.insertTestStudent(userId3, "M.Tech", "CSE", 1, "Spring", "2024005", "Student Three");

        // Act
        List<Student> students = studentDAO.getAllStudents();

        // Assert
        assertNotNull(students);
        assertEquals(3, students.size(), "Should have 3 students");
    }

    @Test
    void testUpdateStudent() throws SQLException {
        // Arrange
        int studentId = TestDataHelper.insertTestStudent(
                testUserId, "B. Tech", "CSE", 2, "Spring", "2024006", "Original Name"
        );

        Student updatedStudent = new Student(
                studentId,
                testUserId,
                "B.Tech",
                "ECE", // Changed branch
                3,     // Changed year
                "Fall", // Changed term
                "2024006",
                "Updated Name" // Changed name
        );

        // Act
        boolean result = studentDAO.updateStudent(updatedStudent);

        // Assert
        assertTrue(result, "Student should be updated successfully");

        // Verify changes
        Student retrieved = studentDAO.getStudentById(studentId);
        assertNotNull(retrieved);
        assertEquals("ECE", retrieved.getBranch());
        assertEquals(3, retrieved. getYear());
        assertEquals("Fall", retrieved.getTerm());
        assertEquals("Updated Name", retrieved.getFullName());
    }

    @Test
    void testRollExists() throws SQLException {
        // Arrange
        TestDataHelper.insertTestStudent(
                testUserId, "B. Tech", "CSE", 2, "Spring", "2024007", "Test Student"
        );

        // Act & Assert
        assertTrue(studentDAO.rollExists("2024007"), "Roll number should exist");
        assertFalse(studentDAO.rollExists("9999999"), "Non-existent roll should return false");
    }

    @Test
    void testInsertStudent_DuplicateRollNo() throws SQLException {
        // Arrange
        TestDataHelper. insertTestStudent(
                testUserId, "B.Tech", "CSE", 2, "Spring", "2024008", "First Student"
        );

        int userId2 = TestDataHelper.insertTestUser("test4@iiitd.ac. in", "STUDENT", "hash");
        Student duplicateStudent = new Student(
                0, userId2, "B.Tech", "ECE", 2, "Spring", "2024008", "Second Student"
        );

        // Act
        boolean result = studentDAO.insertStudent(userId2, duplicateStudent);

        // Assert
        assertFalse(result, "Should not insert student with duplicate roll number");
    }

    @Test
    void testCountStudents() throws SQLException {
        // Arrange
        int userId2 = TestDataHelper. insertTestUser("test5@iiitd.ac.in", "STUDENT", "hash");
        TestDataHelper.insertTestStudent(testUserId, "B.Tech", "CSE", 2, "Spring", "2024009", "Student One");
        TestDataHelper. insertTestStudent(userId2, "B.Tech", "ECE", 3, "Fall", "2024010", "Student Two");

        // Act
        int count = studentDAO.countStudents();

        // Assert
        assertEquals(2, count, "Should count 2 students");
    }

    @Test
    void testGetStudentByRollNo() throws SQLException {
        // Arrange
        TestDataHelper.insertTestStudent(
                testUserId, "B.Tech", "CSE", 2, "Spring", "2024011", "Roll Test Student"
        );

        // Act
        Student student = studentDAO. getStudentByRollNo("2024011");

        // Assert
        assertNotNull(student, "Student should be found by roll number");
        assertEquals("2024011", student.getRollNo());
        assertEquals("Roll Test Student", student.getFullName());
    }
}