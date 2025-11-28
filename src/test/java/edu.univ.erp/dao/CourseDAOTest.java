package edu.univ.erp.dao;

import edu. univ.erp.domain.Course;
import edu.univ.erp.test.BaseDAOTest;
import edu. univ.erp.test. TestDataHelper;
import org. junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter. api.Assertions.*;

/**
 * JUnit 5 tests for CourseDAO using local PostgreSQL test database.
 */
class CourseDAOTest extends BaseDAOTest {

    private CourseDAO courseDAO;
    private int testDepartmentId;

    @BeforeEach
    @Override
    public void setUp() throws SQLException {
        super.setUp();
        courseDAO = new CourseDAO();

        // Create a test department
        testDepartmentId = TestDataHelper.insertTestDepartment("Computer Science");
    }

    @Test
    void testInsertCourse() {
        // Arrange
        Course course = new Course(
                0, // ID will be auto-generated
                testDepartmentId,
                "CSE101",
                "Introduction to Programming",
                4,
                null
        );

        // Act
        boolean result = courseDAO.insertCourse(course);

        // Assert
        assertTrue(result, "Course should be inserted successfully");

        // Verify
        Course retrieved = courseDAO.getCourseByCode("CSE101");
        assertNotNull(retrieved);
        assertEquals("Introduction to Programming", retrieved. getTitle());
    }

    @Test
    void testGetCourseById() throws SQLException {
        // Arrange
        int courseId = TestDataHelper.insertTestCourse(
                testDepartmentId, "CSE202", "Data Structures", 4, "CSE101"
        );

        // Act
        Course course = courseDAO. getCourseById(courseId);

        // Assert
        assertNotNull(course, "Course should be found");
        assertEquals("CSE202", course.getCode());
        assertEquals("Data Structures", course.getTitle());
        assertEquals(4, course.getCredits());
    }

    @Test
    void testGetCourseByCode() throws SQLException {
        // Arrange
        TestDataHelper.insertTestCourse(
                testDepartmentId, "CSE303", "Algorithms", 4, "CSE202"
        );

        // Act
        Course course = courseDAO. getCourseByCode("CSE303");

        // Assert
        assertNotNull(course, "Course should be found by code");
        assertEquals("Algorithms", course.getTitle());
    }

    @Test
    void testGetAllCourses() throws SQLException {
        // Arrange
        TestDataHelper.insertTestCourse(testDepartmentId, "CSE401", "Machine Learning", 4, null);
        TestDataHelper.insertTestCourse(testDepartmentId, "CSE402", "Deep Learning", 4, "CSE401");
        TestDataHelper.insertTestCourse(testDepartmentId, "CSE403", "Computer Vision", 4, "CSE401");

        // Act
        List<Course> courses = courseDAO. getAllCourses();

        // Assert
        assertNotNull(courses);
        assertEquals(3, courses.size(), "Should have 3 courses");
    }

    @Test
    void testUpdateCourse() throws SQLException {
        // Arrange
        int courseId = TestDataHelper.insertTestCourse(
                testDepartmentId, "CSE501", "Original Title", 3, null
        );

        Course updatedCourse = new Course(
                courseId,
                testDepartmentId,
                "CSE501",
                "Updated Title",
                4, // Changed credits
                "CSE303" // Added prerequisites
        );

        // Act
        boolean result = courseDAO.updateCourse(updatedCourse);

        // Assert
        assertTrue(result, "Course should be updated successfully");

        // Verify
        Course retrieved = courseDAO. getCourseById(courseId);
        assertEquals("Updated Title", retrieved.getTitle());
        assertEquals(4, retrieved.getCredits());
        assertEquals("CSE303", retrieved.getPrerequisites());
    }

    @Test
    void testDeleteCourse() throws SQLException {
        // Arrange
        int courseId = TestDataHelper.insertTestCourse(
                testDepartmentId, "CSE601", "To Be Deleted", 3, null
        );

        // Act
        boolean result = courseDAO.deleteCourse(courseId);

        // Assert
        assertTrue(result, "Course should be deleted successfully");
        assertNull(courseDAO.getCourseById(courseId), "Deleted course should not be found");
    }

    @Test
    void testCountCourses() throws SQLException {
        // Arrange
        TestDataHelper.insertTestCourse(testDepartmentId, "CSE701", "Course One", 4, null);
        TestDataHelper.insertTestCourse(testDepartmentId, "CSE702", "Course Two", 3, null);

        // Act
        int count = courseDAO.countCourses();

        // Assert
        assertEquals(2, count, "Should count 2 courses");
    }

    @Test
    void testHasSections() throws SQLException {
        // Arrange
        int courseId = TestDataHelper.insertTestCourse(
                testDepartmentId, "CSE801", "Course with Section", 4, null
        );

        // Initially should have no sections
        assertFalse(courseDAO.hasSections(courseId), "New course should have no sections");

        // Add a section
        int facultyUserId = TestDataHelper.insertTestUser("faculty@test.com", "FACULTY", "hash");
        int facultyId = TestDataHelper.insertTestFaculty(facultyUserId, testDepartmentId, "Professor", "Test Faculty");
        TestDataHelper.insertTestSection(courseId, facultyId, "Spring", 2024, "Room 101", 30);

        // Now should have sections
        assertTrue(courseDAO.hasSections(courseId), "Course should have sections after adding one");
    }
}