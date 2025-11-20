package edu.univ.erp.service;

import edu.univ.erp.auth.store.AuthDAO;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private SectionComponentDAO sectionComponentDAO = new SectionComponentDAO();
    private GradesDAO gradesDAO = new GradesDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private ComponentTypeDAO componentTypeDAO = new ComponentTypeDAO();

    public List<Course> browseCourses(){
        List<Course> list = courseDAO.getAllCourses();
        return list;
    }
    public List<Section> getSectionsForCourse(int courseId){
        List<Section> list = sectionDAO.getSectionsByCourse(courseId);
        return list;
    }
    public boolean registerForSection(int studentId, int sectionId){
        return enrollmentDAO.enrollStudent(studentId, sectionId);
    }
    public boolean dropSection(int studentId, int sectionId){
        return enrollmentDAO.dropEnrollment(studentId, sectionId);
    }
    public List<TimetableEntry> getTimetable(int studentId) {
        List<TimetableEntry> list = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);
        for (Enrollment enrollment : enrollments) {
            int secID = enrollment.getSectionId();
            Section section = sectionDAO.getSectionById(secID);
            Course course = courseDAO.getCourseById(section.getCourseID());
            Faculty faculty = facultyDAO.getFacultyById(section.getInstructorID());
            List<SectionComponent> secComponents = sectionComponentDAO.getComponentsBySection(secID);

            for (SectionComponent sc : secComponents) {
                String typeName = componentTypeDAO.getComponentTypeName(sc.getTypeID());
                if (!"LECTURE".equals(typeName) && !"TUTORIAL".equals(typeName) && !"LAB".equals(typeName)) {
                    continue;
                }
                TimetableEntry entry = new TimetableEntry(course.getCode(), typeName, faculty.getFullName(), section.getRoom(), sc.getDay(), sc.getStartTime(), sc.getEndTime(), sc.getDescription());
                list.add(entry);
            }
        }
        return list;
    }
    public Grade getGrades(int enrollmentId) {
        return gradesDAO.getGradeByEnrollment(enrollmentId);
    }
}
