package edu.univ.erp.service;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp. dao.*;
import edu.univ.erp.domain.*;
import java.util.*;

public class FacultyService {

    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private SectionComponentDAO sectionComponentDAO = new SectionComponentDAO();
    private GradesDAO gradesDAO = new GradesDAO();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private ComponentTypeDAO componentTypeDAO = new ComponentTypeDAO();
    private ComponentScoreDAO componentScoreDAO = new ComponentScoreDAO();
    private SettingsDAO settingsDAO = new SettingsDAO();

    public Faculty getMyProfile() {
        return facultyDAO.getFacultyByUserId(UserSession.getUserID());
    }

    public List<Section> getMySections() {
        Faculty faculty = getMyProfile();
        if (faculty == null) return new ArrayList<>();
        return sectionDAO.getSectionsByInstructor(faculty.getFacultyId());
    }

    public List<Section> mySections(int instructorId) {
        return sectionDAO.getSectionsByInstructor(instructorId);
    }

    public int getTotalSections() {
        Faculty faculty = getMyProfile();
        if (faculty == null) return 0;
        return mySections(faculty.getFacultyId()).size();
    }

    public int getTotalStudents() {
        Faculty faculty = getMyProfile();
        if (faculty == null) return 0;

        List<Section> sections = mySections(faculty.getFacultyId());
        int total = 0;
        for (Section s : sections) {
            total += enrollmentDAO.getEnrollmentsBySection(s.getSectionID()).size();
        }
        return total;
    }

    public String getDepartmentName(int id) {
        return departmentDAO.getDepartmentNameById(id);
    }

    public List<SectionComponent> getComponents(int sectionId) {
        return sectionComponentDAO.getComponentsBySection(sectionId);
    }

    public boolean addComponent(SectionComponent newComponent) {
        return sectionComponentDAO.insertComponent(newComponent);
    }

    public boolean updateComponent(SectionComponent newComponent) {
        return sectionComponentDAO.updateComponent(newComponent);
    }

    public boolean deleteComponent(int componentId) {
        return sectionComponentDAO.deleteComponent(componentId);
    }

    public boolean enterScore(ComponentScore componentScore) {
        return componentScoreDAO.insertScore(componentScore);
    }

    public boolean updateScore(ComponentScore componentScore) {
        return componentScoreDAO.updateScore(componentScore);
    }

    public ComponentScore getScore(int enrollmentId, int componentId) {
        return componentScoreDAO.getScore(enrollmentId, componentId);
    }

    public List<List<ComponentScore>> viewScores(int sectionId) {
        List<List<ComponentScore>> L = new ArrayList<>();
        List<Enrollment> list = enrollmentDAO.getEnrollmentsBySection(sectionId);
        for (Enrollment e : list) {
            L.add(componentScoreDAO.getScoresByEnrollment(e. getEnrollmentId()));
        }
        return L;
    }

    public boolean finalizeGrade(int enrollmentId, double score, String gradeLabel) {
        return gradesDAO.insertGrade(enrollmentId, score, gradeLabel);
    }

    public Grade getGrade(int enrollmentId) {
        return gradesDAO. getGradeByEnrollment(enrollmentId);
    }

    public Map<Integer, Double> classStatistics(int sectionId) {
        Map<Integer, Double> map = new HashMap<>();
        List<Enrollment> list = enrollmentDAO.getEnrollmentsBySection(sectionId);
        for (Enrollment e : list) {
            Grade g = gradesDAO.getGradeByEnrollment(e. getEnrollmentId());
            if (g != null) {
                map.put(g.getEnrollmentId(), g.getTotalScore());
            }
        }
        return map;
    }

    public boolean calculateAndStoreGrade(int enrollmentId, int sectionId) {
        List<SectionComponent> components = sectionComponentDAO.getComponentsBySection(sectionId);
        double totalScore = 0;
        double totalWeight = 0;

        for (SectionComponent sc : components) {
            if (sc.getWeight() == 0) continue;

            ComponentScore score = componentScoreDAO. getScore(enrollmentId, sc.getComponentID());
            if (score != null) {
                totalScore += (score.getScore() * sc. getWeight() / 100);
                totalWeight += sc.getWeight();
            }
        }

        if (totalWeight < 100) {
            return false;
        }

        String letterGrade = getGradeFromScore(totalScore);
        return gradesDAO.insertOrUpdateGrade(enrollmentId, totalScore, letterGrade);
    }

    private String getGradeFromScore(double score) {
        if (score >= 90) return "A";
        if (score >= 85) return "A-";
        if (score >= 80) return "B";
        if (score >= 75) return "B-";
        if (score >= 70) return "C";
        if (score >= 65) return "C-";
        if (score >= 60) return "D";
        return "F";
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getCourseById(courseId);
    }

    public Section getSectionById(int sectionId) {
        return sectionDAO. getSectionById(sectionId);
    }

    public List<Enrollment> getEnrolledStudents(int sectionId) {
        return enrollmentDAO.getEnrollmentsBySection(sectionId);
    }

    public Student getStudentById(int studentId) {
        return studentDAO. getStudentById(studentId);
    }

    public List<Notification> getRecentNotifications(int limit) {
        NotificationDAO notificationDAO = new NotificationDAO();
        return notificationDAO.getRecentNotifications(limit);
    }

    public boolean isMaintenanceMode() {
        return settingsDAO.isMaintenanceMode();
    }

    public List<ComponentType> getAllComponentTypes() {
        return componentTypeDAO.getAllComponentTypes();
    }

    public String getComponentTypeName(int typeId) {
        return componentTypeDAO.getComponentTypeName(typeId);
    }

    public double getTotalComponentWeight(int sectionId) {
        List<SectionComponent> components = sectionComponentDAO.getComponentsBySection(sectionId);
        double total = 0;
        for (SectionComponent c : components) {
            total += c.getWeight();
        }
        return total;
    }

    public int finalizeAllGrades(int sectionId) {
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(sectionId);
        int successCount = 0;

        for (Enrollment e : enrollments) {
            if (calculateAndStoreGrade(e.getEnrollmentId(), sectionId)) {
                successCount++;
            }
        }

        return successCount;
    }
}