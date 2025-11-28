package edu.univ.erp.dao;

import edu.univ.erp.domain.Grade;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api. Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GradesDAOTest {

    private static GradesDAO gradesDAO;
    private static int testEnrollmentId = 1; // Assume enrollment exists

    @BeforeAll
    static void setUp() {
        gradesDAO = new GradesDAO();
        System.out.println("Starting GradesDAO tests...");
    }

    @Test
    @Order(1)
    @DisplayName("Test: Insert Grade")
    void testInsertGrade() {
        boolean result = gradesDAO.insertGrade(testEnrollmentId, 85.5, "A-");

        assertTrue(result, "Grade should be inserted successfully");
    }

    @Test
    @Order(2)
    @DisplayName("Test: Get Grade by Enrollment")
    void testGetGradeByEnrollment() {
        Grade grade = gradesDAO.getGradeByEnrollment(testEnrollmentId);

        assertNotNull(grade, "Grade should not be null");
        assertEquals(85.5, grade.getTotalScore(), "Total score should match");
        assertEquals("A-", grade.getGradeLabel(), "Grade label should match");
    }

    @Test
    @Order(3)
    @DisplayName("Test: Update Grade")
    void testUpdateGrade() {
        boolean result = gradesDAO.updateGrade(testEnrollmentId, 90.0, "A");

        assertTrue(result, "Grade should be updated successfully");

        Grade updated = gradesDAO.getGradeByEnrollment(testEnrollmentId);
        assertEquals(90.0, updated.getTotalScore(), "Score should be updated");
        assertEquals("A", updated.getGradeLabel(), "Grade should be updated");
    }

    @Test
    @Order(4)
    @DisplayName("Test: Insert or Update Grade")
    void testInsertOrUpdateGrade() {
        boolean result = gradesDAO. insertOrUpdateGrade(testEnrollmentId, 92.0, "A");

        assertTrue(result, "Grade should be inserted or updated");

        Grade grade = gradesDAO.getGradeByEnrollment(testEnrollmentId);
        assertEquals(92.0, grade.getTotalScore(), "Score should match");
    }

    @AfterAll
    static void tearDown() {
        System.out. println("GradesDAO tests completed.");
    }
}