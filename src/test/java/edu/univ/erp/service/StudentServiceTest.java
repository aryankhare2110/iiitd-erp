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

@DisplayName("StudentService Tests")
class StudentServiceTest extends BaseServiceTest {

        private int testStudentId;
        private int testCourseId;
        private int testSectionId;

        @BeforeEach
        public void setUp() throws java.sql.SQLException {
                super.setUp();

                // Set up test data
                int deptId = TestDataFactory.createTestDepartment(getErpConnection(), "CSE", "Computer Science");
                testStudentId = TestDataFactory.createTestStudent(
                                getErpConnection(), 100, "B.Tech", "CSE", 2, "Monsoon", "2024001", "Test Student");

                testCourseId = TestDataFactory.createTestCourse(
                                getErpConnection(), deptId, "CSE101", "Intro to CS", 4, null);

                int facultyId = TestDataFactory.createTestFaculty(
                                getErpConnection(), 200, deptId, "Professor", "Dr. Smith");

                testSectionId = TestDataFactory.createTestSection(
                                getErpConnection(), testCourseId, facultyId, "A", 50, "R101");
        }

        @Test
        @DisplayName("Should enroll student in course section")
        void testEnrollStudentInCourse() throws Exception {
                // When - Enroll student
                int enrollmentId = TestDataFactory.createTestEnrollment(
                                getErpConnection(), testStudentId, testSectionId);

                // Then - Verify enrollment
                String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
                try (Connection conn = getErpConnection();
                                PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, enrollmentId);
                        ResultSet rs = ps.executeQuery();
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getInt("student_id")).isEqualTo(testStudentId);
                        assertThat(rs.getInt("section_id")).isEqualTo(testSectionId);
                        assertThat(rs.getString("status")).isEqualTo("ENROLLED");
                }
        }

        @Test
        @DisplayName("Should calculate total credits for enrolled courses")
        void testCalculateTotalCredits() throws Exception {
                // Given - Enroll in multiple courses
                TestDataFactory.createTestEnrollment(getErpConnection(), testStudentId, testSectionId); // 4 credits

                int mathDept = TestDataFactory.createTestDepartment(getErpConnection(), "MATH", "Mathematics");
                int mathCourse = TestDataFactory.createTestCourse(getErpConnection(), mathDept, "MATH101", "Calculus",
                                3, null);
                int mathFaculty = TestDataFactory.createTestFaculty(getErpConnection(), 201, mathDept, "Professor",
                                "Dr. Math");
                int mathSection = TestDataFactory.createTestSection(getErpConnection(), mathCourse, mathFaculty, "A",
                                50,
                                "R102");
                TestDataFactory.createTestEnrollment(getErpConnection(), testStudentId, mathSection); // 3 credits

                // When - Calculate total credits
                String sql = "SELECT SUM(c.credits) FROM enrollments e " +
                                "JOIN sections s ON e.section_id = s.section_id " +
                                "JOIN courses c ON s.course_id = c.course_id " +
                                "WHERE e.student_id = ?";
                try (Connection conn = getErpConnection();
                                PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, testStudentId);
                        ResultSet rs = ps.executeQuery();

                        // Then - Should have 7 total credits (4 + 3)
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getInt(1)).isEqualTo(7);
                }
        }

        @Test
        @DisplayName("Should view student timetable with course and section details")
        void testViewTimetable() throws Exception {
                // Given - Enroll student in course
                TestDataFactory.createTestEnrollment(getErpConnection(), testStudentId, testSectionId);

                // When - Get enrollment details (simulating timetable data)
                String sql = "SELECT c.code, c.title, s.room " +
                                "FROM enrollments e " +
                                "JOIN sections s ON e.section_id = s.section_id " +
                                "JOIN courses c ON s.course_id = c.course_id " +
                                "WHERE e.student_id = ?";
                try (Connection conn = getErpConnection();
                                PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, testStudentId);
                        ResultSet rs = ps.executeQuery();

                        // Then - Should have timetable entry
                        assertThat(rs.next()).isTrue();
                        assertThat(rs.getString("code")).isEqualTo("CSE101");
                        assertThat(rs.getString("title")).isEqualTo("Intro to CS");
                        assertThat(rs.getString("room")).isEqualTo("R101");
                }
        }
}
