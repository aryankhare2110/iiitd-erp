package edu.univ.erp.domain;

public class Grade {

    private int gradeId;
    private int enrollmentId;
    private double totalScore;
    private String gradeLabel;

    public Grade(int gradeId, int enrollmentId, double totalScore, String gradeLabel) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.totalScore = totalScore;
        this.gradeLabel = gradeLabel;
    }

    public int getGradeId() {
        return gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public String getGradeLabel() {
        return gradeLabel;
    }

}