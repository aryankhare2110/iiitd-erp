package edu.univ.erp.service;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;

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
        if (settingsDAO.isMaintenanceMode()) {
            return false;
        }
        if (enrollmentDAO.isEnrolled(studentId, sectionId)) {
            return false;
        }
        int capacity = sectionDAO.getCapacity(sectionId);
        int filled = enrollmentDAO.countEnrollments(sectionId);
        if (filled >= capacity) {
            return false;
        }
        return enrollmentDAO.enrollStudent(studentId, sectionId);
    }

    public boolean dropSection(int studentId, int sectionId) {
        if (settingsDAO.isMaintenanceMode()) {
            return false;
        }
        if (!enrollmentDAO.isEnrolled(studentId, sectionId)) {
            return false;
        }
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

    public List<Grade> getGrades(int studentId) {
        List<Grade> grades = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment e : enrollments) {
            Grade g = gradesDAO.getGradeByEnrollment(e.getEnrollmentId());
            if (g != null) grades.add(g);
        }
        return grades;
    }
}