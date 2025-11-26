package edu.univ.erp.service;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;

import java.time.LocalDate;
import java.util.*;

public class StudentService {

    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private SectionComponentDAO sectionComponentDAO = new SectionComponentDAO();
    private GradesDAO gradesDAO = new GradesDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private ComponentTypeDAO componentTypeDAO = new ComponentTypeDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();

    public Student getMyProfile() {
        return studentDAO.getStudentByUserId(UserSession.getUserID());
    }

    public List<Course> browseCourses() {
        return courseDAO.getAllCourses();
    }

    public List<Section> getSectionsForCourse(int courseId) {
        return sectionDAO.getSectionsByCourse(courseId);
    }

    public boolean registerForSection(int studentId, int sectionId) {
        if (settingsDAO.isMaintenanceMode())
            return false;
        LocalDate deadline = settingsDAO.getAddDropDeadline();
        if (deadline != null && LocalDate.now().isAfter(deadline))
            return false;
        if (enrollmentDAO.isEnrolled(studentId, sectionId))
            return false;
        int cap = sectionDAO.getCapacity(sectionId);
        int filled = enrollmentDAO.countEnrollments(sectionId);
        if (filled >= cap)
            return false;
        return enrollmentDAO.enrollStudent(studentId, sectionId);
    }

    public boolean dropSection(int studentId, int sectionId) {
        if (settingsDAO.isMaintenanceMode())
            return false;
        LocalDate deadline = settingsDAO.getAddDropDeadline();
        if (deadline != null && LocalDate.now().isAfter(deadline))
            return false;
        if (!enrollmentDAO.isEnrolled(studentId, sectionId))
            return false;
        return enrollmentDAO.dropEnrollment(studentId, sectionId);
    }

    public List<TimetableEntry> getTimetable(int studentId) {
        List<TimetableEntry> list = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section == null) continue;
            Course course = courseDAO.getCourseById(section.getCourseID());
            if (course == null) continue;
            Faculty faculty = facultyDAO.getFacultyById(section.getInstructorID());
            List<SectionComponent> components = sectionComponentDAO.getComponentsBySection(e.getSectionId());
            for (SectionComponent sc : components) {
                String type = componentTypeDAO.getComponentTypeName(sc.getTypeID());
                if (!"LECTURE".equals(type) && !"TUTORIAL".equals(type) && !"LAB".equals(type)) {
                    continue;
                }
                String instructor = (faculty == null) ? "TBA" : faculty.getFullName();
                list.add(new TimetableEntry(
                        course.getCode(), type, instructor,
                        section.getRoom(), sc.getDay(),
                        sc.getStartTime(), sc.getEndTime(),
                        sc.getDescription()
                ));
            }
        }
        return list;
    }

    public int getEnrolledCoursesCount() {
        Student student = getMyProfile();
        if (student == null) return 0;
        return enrollmentDAO.getEnrolledCoursesCount(student.getStudentId());
    }

    public int getTotalCredits() {
        Student student = getMyProfile();
        if (student == null) return 0;

        int totalCredits = 0;
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(student.getStudentId());

        for (Enrollment e : enrollments) {
            Section section = sectionDAO.getSectionById(e.getSectionId());
            if (section != null) {
                Course course = courseDAO.getCourseById(section.getCourseID());
                if (course != null) {
                    totalCredits += course.getCredits();
                }
            }
        }
        return totalCredits;
    }

    public List<Notification> getRecentNotifications(int limit) {
        NotificationDAO notificationDAO = new NotificationDAO();
        return notificationDAO.getRecentNotifications(limit);
    }

    public boolean isMaintenanceMode() {
        return settingsDAO.isMaintenanceMode();
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        AuthService authService = new AuthService();
        String email = UserSession.getUserEmail();

        if (authService.login(email, oldPassword) == null) {
            return false;
        }
        return authService.resetPassword(email, newPassword);
    }

    public boolean isEnrolled(int studentId, int sectionId) {
        return enrollmentDAO.isEnrolled(studentId, sectionId);
    }

    public int getEnrollmentCount(int sectionId) {
        return enrollmentDAO.countEnrollments(sectionId);
    }

    public Faculty getFacultyForSection(int facultyId) {
        if (facultyId <= 0) return null;
        return facultyDAO.getFacultyById(facultyId);
    }

    public int getMyStudentId() {
        Student s = getMyProfile();
        return (s == null) ? -1 : s.getStudentId();
    }

    public String getDepartmentName(int deptId) {
        DepartmentDAO deptDAO = new DepartmentDAO();
        return deptDAO.getDepartmentNameById(deptId);
    }

    public List<Grade> getGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : enrollments) {
            Grade g = gradesDAO.getGradeByEnrollment(e.getEnrollmentId());
            if (g != null) grades.add(g);
        }
        return grades;
    }

    public LocalDate getAddDropDeadline() {
        return settingsDAO. getAddDropDeadline();
    }

    public List<Enrollment> getMyEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }
}