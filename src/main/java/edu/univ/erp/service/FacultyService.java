package edu.univ.erp.service;

import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.dao.*;
import edu.univ.erp.domain.*;
import java.util.*;

public class FacultyService {

    private CourseDAO courseDAO = new CourseDAO();
    private StudentDAO studentDAO = new StudentDAO();
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

    public List<Section> mySections(int instructorId) {
        return sectionDAO.getSectionsByInstructor(instructorId);
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

    public boolean enterScore(ComponentScore componentScore) {
        return componentScoreDAO.insertScore(componentScore);
    }

    public boolean finalizeGrade(int enrollmentId, double score, String gradeLabel) {
        return gradesDAO.insertGrade(enrollmentId, score, gradeLabel);
    }

    public List<List<ComponentScore>> viewScores(int sectionId) {
        List<List<ComponentScore>> L = new ArrayList<>();
        List<Enrollment> list = enrollmentDAO.getEnrollmentsBySection(sectionId);
        for (Enrollment e : list) {
            L.add(componentScoreDAO.getScoresByEnrollment(e.getEnrollmentId()));
        }
        return L;
    }

    public Map<Integer, Double> classStatistics(int sectionId) {
        Map<Integer, Double> map = new HashMap<>();
        List<Enrollment> list = enrollmentDAO.getEnrollmentsBySection(sectionId);
        for (Enrollment e : list) {
            Grade g = gradesDAO.getGradeByEnrollment(e.getEnrollmentId());
            if (g != null) {
                map.put(g.getEnrollmentId(), g.getTotalScore());
            }
        }
        return map;
    }
}