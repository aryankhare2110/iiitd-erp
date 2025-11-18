package edu.univ.erp.domain;

public class ComponentScore {
    private int scoreId;
    private int enrollmentId;
    private int componentId;
    private double score;

    public ComponentScore(int scoreId, int enrollmentId, int componentId, double score) {
        this.scoreId = scoreId;
        this.enrollmentId = enrollmentId;
        this.componentId = componentId;
        this.score = score;
    }

    public int getScoreId() {
        return scoreId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getComponentId() {
        return componentId;
    }

    public double getScore() {
        return score;
    }

}