package edu.univ.erp.dao;

import edu.univ.erp.domain.Course;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter. api.Assertions.*;

@TestMethodOrder(MethodOrderer. OrderAnnotation.class)
class CourseDAOTest {

    private static CourseDAO courseDAO;
    private static int testCourseId;

    @BeforeAll
    static void setUp() {
        courseDAO = new CourseDAO();
        System.out. println("Starting CourseDAO tests.. .");
    }

    @Test
    @Order(1)
    @DisplayName("Test: Insert Course")
    void testInsertCourse() {
        Course course = new Course(
                0,
                1, // department_id
                "TEST101",
                "Test Course",
                4,
                "None"
        );

        boolean result = courseDAO.insertCourse(course);
        assertTrue(result, "Course should be inserted successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Get Course by Code")
    void testGetCourseByCode() {
        Course course = courseDAO.getCourseByCode("TEST101");

        assertNotNull(course, "Course should not be null");
        assertEquals("Test Course", course. getTitle(), "Course title should match");
        assertEquals(4, course.getCredits(), "Credits should match");

        testCourseId = course.getCourseID();
    }

    @Test
    @Order(3)
    @DisplayName("Test: Get Course by ID")
    void testGetCourseById() {
        Course course = courseDAO.getCourseById(testCourseId);

        assertNotNull(course, "Course should not be null");
        assertEquals("TEST101", course.getCode(), "Course code should match");
    }

    @Test
    @Order(4)
    @DisplayName("Test: Get All Courses")
    void testGetAllCourses() {
        List<Course> courses = courseDAO.getAllCourses();

        assertNotNull(courses, "Courses list should not be null");
        assertTrue(courses.size() > 0, "Should have at least one course");
    }

    @Test
    @Order(5)
    @DisplayName("Test: Count Courses")
    void testCountCourses() {
        int count = courseDAO.countCourses();

        assertTrue(count > 0, "Course count should be greater than 0");
    }

    @Test
    @Order(6)
    @DisplayName("Test: Update Course")
    void testUpdateCourse() {
        Course course = courseDAO.getCourseById(testCourseId);
        course.setTitle("Updated Test Course");

        boolean result = courseDAO.updateCourse(course);
        assertTrue(result, "Course should be updated successfully");

        Course updated = courseDAO. getCourseById(testCourseId);
        assertEquals("Updated Test Course", updated.getTitle(), "Title should be updated");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("CourseDAO tests completed.");
    }
}