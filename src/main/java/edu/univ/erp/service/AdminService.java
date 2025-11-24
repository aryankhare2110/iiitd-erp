package edu.univ.erp.service;

import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Faculty;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;

public class AdminService {

    private AuthDAO authDAO = new AuthDAO();
    public CourseDAO courseDAO = new CourseDAO();
    public StudentDAO studentDAO = new StudentDAO();
    public FacultyDAO facultyDAO = new FacultyDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();

    public boolean createStudent (String email, String password, Student student) {
        String hashed = PasswordHasher.hash(password);
        boolean registeredStudent = authDAO.registerNewUser(email.toLowerCase(), "STUDENT", hashed);
        if (!registeredStudent) {
            return false;
        }
        Integer userID = authDAO.getUserId(email.toLowerCase());
        if (userID == null) {
            return false;
        }
        return studentDAO.insertStudent(userID, student);
    }

    public boolean createFaculty (String email, String password, Faculty faculty) {
        String hashed = PasswordHasher.hash(password);
        boolean registeredFaculty = authDAO.registerNewUser(email.toLowerCase(), "INSTRUCTOR", hashed);
        if (!registeredFaculty) {
            return false;
        }
        Integer userID = authDAO.getUserId(email.toLowerCase());
        if (userID == null) {
            return false;
        }
        return facultyDAO.insertFaculty(userID, faculty);
    }

    public boolean createCourse (Course course) {
        return courseDAO.insertCourse(course);
    }

    public boolean createAdmin(String email, String password) {
        String hashed = PasswordHasher.hash(password);

        boolean ok = authDAO.registerNewUser(email.toLowerCase(), "ADMIN", hashed);
        if (!ok) {
            return false;
        }

        return true;
    }

    public boolean updateCourse (Course course) {
        return courseDAO.updateCourse(course);
    }

    public boolean createSection (Section section) {
        return sectionDAO.insertSection(section);
    }

    public boolean assignInstructor(int sectionId, int instructorId) {
        return sectionDAO.updateInstructor(sectionId, instructorId);
    }

    public boolean setUserStatus(int userId, boolean active) {
        return authDAO.updateStatus(userId, active ? "ACTIVE" : "INACTIVE");
    }

    public boolean setMaintenanceMode(boolean on) {
        return settingsDAO.setMaintenanceMode(on);
    }

    public int getStudentCount() {
        return studentDAO.countStudents();
    }

    public int getFacultyCount() {
        return facultyDAO.countFaculty();
    }

    public int getCourseCount() {
        return courseDAO.countCourses();
    }

    public boolean isMaintenanceMode() {
        return settingsDAO.isMaintenanceMode();
    }

}
