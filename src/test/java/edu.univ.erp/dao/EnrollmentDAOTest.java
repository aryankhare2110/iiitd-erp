package edu.univ. erp.dao;

import edu.univ.erp.domain.Enrollment;
import edu. univ.erp.test. BaseDAOTest;
import edu.univ.erp.test.TestDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for EnrollmentDAO using local PostgreSQL test database.
 */
class EnrollmentDAOTest extends BaseDAOTest {

    private EnrollmentDAO enrollmentDAO;
    private int testStudentId;
    private int testSectionId;
    private int testCourseId;

    @BeforeEach
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        enrollmentDAO = new EnrollmentDAO();

        // Set up test data
        int deptId = TestDataHelper.insertTestDepartment("Computer Science");
        int studentUserId = TestDataHelper.insertTestUser("student@test.com", "STUDENT", "hash");
        testStudentId = TestDataHelper.insertTestStudent(
                studentUserId, "B.Tech", "CSE", 2, "Spring", "2024999", "Test Student"
        );

        testCourseId = TestDataHelper. insertTestCourse(deptId, "TEST101", "Test Course", 4, null);

        int facultyUserId = TestDataHelper. insertTestUser("faculty@test. com", "FACULTY", "hash");
        int facultyId = TestDataHelper.insertTestFaculty(facultyUserId, deptId, "Professor", "Test Faculty");
        testSectionId = TestDataHelper.insertTestSection(testCourseId, facultyId, "Spring", 2024, "Room 101", 30);
    }

    @Test
    void testEnrollStudent() {
        // Act
        boolean result = enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Assert
        assertTrue(result, "Student should be enrolled successfully");
        assertTrue(enrollmentDAO.isEnrolledInSection(testStudentId, testSectionId),
                "Student should be enrolled in section");
    }

    @Test
    void testIsEnrolledInSection() {
        // Arrange
        enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Act & Assert
        assertTrue(enrollmentDAO.isEnrolledInSection(testStudentId, testSectionId),
                "Should return true for enrolled student");
        assertFalse(enrollmentDAO. isEnrolledInSection(99999, testSectionId),
                "Should return false for non-enrolled student");
    }

    @Test
    void testIsEnrolled() {
        // Arrange
        enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Act & Assert
        assertTrue(enrollmentDAO. isEnrolled(testStudentId, testCourseId),
                "Should return true when student is enrolled in course");
        assertFalse(enrollmentDAO.isEnrolled(testStudentId, 99999),
                "Should return false for non-enrolled course");
    }

    @Test
    void testDropEnrollment() {
        // Arrange
        enrollmentDAO. enrollStudent(testStudentId, testSectionId);
        assertTrue(enrollmentDAO.isEnrolledInSection(testStudentId, testSectionId));

        // Act
        boolean result = enrollmentDAO.dropEnrollment(testStudentId, testSectionId);

        // Assert
        assertTrue(result, "Enrollment should be dropped successfully");
        assertFalse(enrollmentDAO.isEnrolledInSection(testStudentId, testSectionId),
                "Student should no longer be enrolled");
    }

    @Test
    void testGetEnrollmentsByStudent() throws SQLException {
        // Arrange
        int course2Id = TestDataHelper.insertTestCourse(
                TestDataHelper.insertTestDepartment("Mathematics"), "MATH101", "Calculus", 4, null
        );
        int facultyUserId = TestDataHelper. insertTestUser("faculty2@test.com", "FACULTY", "hash");
        int facultyId = TestDataHelper.insertTestFaculty(
                facultyUserId,
                TestDataHelper.insertTestDepartment("Math Dept"),
                "Professor",
                "Math Faculty"
        );
        int section2Id = TestDataHelper.insertTestSection(course2Id, facultyId, "Spring", 2024, "Room 102", 30);

        enrollmentDAO.enrollStudent(testStudentId, testSectionId);
        enrollmentDAO.enrollStudent(testStudentId, section2Id);

        // Act
        List<Enrollment> enrollments = enrollmentDAO. getEnrollmentsByStudent(testStudentId);

        // Assert
        assertNotNull(enrollments);
        assertEquals(2, enrollments.size(), "Student should have 2 enrollments");
    }

    @Test
    void testGetEnrollmentsBySection() throws SQLException {
        // Arrange
        int student2UserId = TestDataHelper.insertTestUser("student2@test.com", "STUDENT", "hash");
        int student2Id = TestDataHelper.insertTestStudent(
                student2UserId, "B.Tech", "CSE", 2, "Spring", "2024998", "Student Two"
        );

        enrollmentDAO.enrollStudent(testStudentId, testSectionId);
        enrollmentDAO.enrollStudent(student2Id, testSectionId);

        // Act
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(testSectionId);

        // Assert
        assertNotNull(enrollments);
        assertEquals(2, enrollments.size(), "Section should have 2 enrollments");
    }

    @Test
    void testCountEnrollments() {
        // Arrange
        enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Act
        int count = enrollmentDAO.countEnrollments(testSectionId);

        // Assert
        assertEquals(1, count, "Section should have 1 enrollment");
    }

    @Test
    void testGetEnrolledCoursesCount() throws SQLException {
        // Arrange
        int course2Id = TestDataHelper.insertTestCourse(
                TestDataHelper.insertTestDepartment("Physics"), "PHY101", "Physics", 4, null
        );
        int facultyUserId = TestDataHelper. insertTestUser("faculty3@test.com", "FACULTY", "hash");
        int facultyId = TestDataHelper.insertTestFaculty(
                facultyUserId,
                TestDataHelper.insertTestDepartment("Physics Dept"),
                "Professor",
                "Physics Faculty"
        );
        int section2Id = TestDataHelper.insertTestSection(course2Id, facultyId, "Spring", 2024, "Room 103", 30);

        enrollmentDAO.enrollStudent(testStudentId, testSectionId);
        enrollmentDAO.enrollStudent(testStudentId, section2Id);

        // Act
        int count = enrollmentDAO.getEnrolledCoursesCount(testStudentId);

        // Assert
        assertEquals(2, count, "Student should be enrolled in 2 courses");
    }

    @Test
    void testGetEnrollmentId() {
        // Arrange
        enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Act
        Integer enrollmentId = enrollmentDAO.getEnrollmentId(testStudentId, testSectionId);

        // Assert
        assertNotNull(enrollmentId, "Enrollment ID should be found");
        assertTrue(enrollmentId > 0, "Enrollment ID should be positive");
    }

    @Test
    void testEnrollStudent_Duplicate() {
        // Arrange
        enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Act
        boolean result = enrollmentDAO.enrollStudent(testStudentId, testSectionId);

        // Assert
        assertFalse(result, "Should not allow duplicate enrollment");
    }
}