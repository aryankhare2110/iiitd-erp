package edu.univ.erp.service;

import edu.univ. erp.domain.Course;
import edu.univ. erp.domain.Student;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter. api.Assertions.*;

@TestMethodOrder(MethodOrderer. OrderAnnotation.class)
class StudentServiceTest {

    private static StudentService studentService;

    @BeforeAll
    static void setUp() {
        studentService = new StudentService();
        System.out.println("Starting StudentService tests...");
    }

    @Test
    @Order(1)
    @DisplayName("Test: Browse Courses")
    void testBrowseCourses() {
        List<Course> courses = studentService. browseCourses();

        assertNotNull(courses, "Courses list should not be null");
        assertTrue(courses.size() > 0, "Should have at least one course");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Get Add/Drop Deadline")
    void testGetAddDropDeadline() {
        java.time.LocalDate deadline = studentService.getAddDropDeadline();

        // Deadline can be null or a valid date
        if (deadline != null) {
            System.out.println("Current deadline: " + deadline);
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test: Check Maintenance Mode")
    void testIsMaintenanceMode() {
        boolean maintenanceMode = studentService.isMaintenanceMode();

        // Should return boolean (either true or false)
        assertNotNull(maintenanceMode);
        System.out.println("Maintenance mode: " + maintenanceMode);
    }

    @Test
    @Order(4)
    @DisplayName("Test: Get Department Name")
    void testGetDepartmentName() {
        String deptName = studentService.getDepartmentName(1);

        assertNotNull(deptName, "Department name should not be null");
        assertFalse(deptName. equals("Unknown"), "Department should exist");
    }

    @AfterAll
    static void tearDown() {
        System.out. println("StudentService tests completed.");
    }
}