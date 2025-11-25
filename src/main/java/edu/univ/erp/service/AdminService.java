package edu.univ.erp.service;

import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;

import java.util.List;

public class AdminService {

    private AuthDAO authDAO = new AuthDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();

    public boolean createStudent(String email, String password, Student student) {
        String hashed = PasswordHasher.hash(password);
        boolean registered = authDAO.registerNewUser(email.toLowerCase(), "STUDENT", hashed);
        if (!registered) return false;
        Integer userId = authDAO.getUserId(email.toLowerCase());
        if (userId == null) return false;
        return studentDAO.insertStudent(userId, student);
    }

    public boolean createFaculty(String email, String password, Faculty faculty) {
        String hashed = PasswordHasher.hash(password);
        boolean registered = authDAO.registerNewUser(email.toLowerCase(), "INSTRUCTOR", hashed);
        if (!registered) return false;
        Integer userId = authDAO.getUserId(email.toLowerCase());
        if (userId == null) return false;
        return facultyDAO.insertFaculty(userId, faculty);
    }

    public boolean createAdmin(String email, String pwd) {
        String hashed = PasswordHasher.hash(pwd);
        return authDAO.registerNewUser(email.toLowerCase(), "ADMIN", hashed);
    }

    public boolean createCourse(Course course) {
        return courseDAO.insertCourse(course);
    }

    public boolean updateCourse(Course course) {
        return courseDAO.updateCourse(course);
    }

    public boolean deleteCourse(int courseId) {
        if (courseDAO.hasEnrollments(courseId)) return false;
        if (courseDAO.hasSections(courseId)) return false;
        return courseDAO.deleteCourse(courseId);
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    public List<Faculty> getAllFaculty() {
        return facultyDAO.getAllFaculty();
    }

    public List<Section> getAllSections() {
        return sectionDAO.getAllSections();
    }

    public boolean createSection(Section section) {
        return sectionDAO.insertSection(section);
    }

    public boolean updateSection(Section section) {
        return sectionDAO.updateSection(section);
    }

    public boolean deleteSection(int sectionId) {
        EnrollmentDAO ed = new EnrollmentDAO();
        if (ed.countEnrollments(sectionId) > 0) return false;
        return sectionDAO.deleteSection(sectionId);
    }

    public boolean assignInstructor(int sectionId, int instructorId) {
        return sectionDAO.updateInstructor(sectionId, instructorId);
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

    public boolean setUserStatus(int userId, boolean active) {
        return authDAO.updateStatus(userId, active ? "ACTIVE" : "INACTIVE");
    }

    public boolean setMaintenanceMode(boolean on) {
        return settingsDAO.setMaintenanceMode(on);
    }

    public boolean isMaintenanceMode() {
        return settingsDAO.isMaintenanceMode();
    }
}