package edu.univ.erp.dao;

import edu.univ.erp.domain.Student;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDAOTest {

    private static StudentDAO studentDAO;
    private static int testUserId = 999;
    private static int testStudentId;

    @BeforeAll
    static void setUp() {
        studentDAO = new StudentDAO();
        System.out.println("Starting StudentDAO tests...");
    }

    @Test
    @Order(1)
    @DisplayName("Test: Insert Student")
    void testInsertStudent() {
        Student student = new Student(
                0,
                testUserId,
                "B. Tech",
                "CSE",
                2024,
                "Fall",
                "2024001",
                "Test Student"
        );

        boolean result = studentDAO.insertStudent(testUserId, student);
        assertTrue(result, "Student should be inserted successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Get Student by User ID")
    void testGetStudentByUserId() {
        Student student = studentDAO.getStudentByUserId(testUserId);

        assertNotNull(student, "Student should not be null");
        assertEquals("Test Student", student.getFullName(), "Student name should match");
        assertEquals("2024001", student.getRollNo(), "Roll number should match");

        testStudentId = student.getStudentId();
    }

    @Test
    @Order(3)
    @DisplayName("Test: Get Student by ID")
    void testGetStudentById() {
        Student student = studentDAO.getStudentById(testStudentId);

        assertNotNull(student, "Student should not be null");
        assertEquals(testStudentId, student.getStudentId(), "Student ID should match");
    }

    @Test
    @Order(4)
    @DisplayName("Test: Get All Students")
    void testGetAllStudents() {
        List<Student> students = studentDAO.getAllStudents();

        assertNotNull(students, "Students list should not be null");
        assertTrue(students.size() > 0, "Should have at least one student");
    }

    @Test
    @Order(5)
    @DisplayName("Test: Count Students")
    void testCountStudents() {
        int count = studentDAO.countStudents();

        assertTrue(count > 0, "Student count should be greater than 0");
    }

    @Test
    @Order(6)
    @DisplayName("Test: Update Student")
    void testUpdateStudent() {
        Student student = studentDAO.getStudentById(testStudentId);
        student.setFullName("Updated Test Student");

        boolean result = studentDAO.updateStudent(student);
        assertTrue(result, "Student should be updated successfully");

        Student updated = studentDAO.getStudentById(testStudentId);
        assertEquals("Updated Test Student", updated.getFullName(), "Name should be updated");
    }

    @AfterAll
    static void tearDown() {
        // Clean up test data
        System.out.println("StudentDAO tests completed.");
    }
}