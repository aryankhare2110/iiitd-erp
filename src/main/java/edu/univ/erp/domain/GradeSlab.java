package edu.univ.erp. domain;

public class GradeSlab {
    private int slabId;
    private int courseId;
    private String gradeLabel;
    private double minScore;
    private double maxScore;

    public GradeSlab(int slabId, int courseId, String gradeLabel, double minScore, double maxScore) {
        this.slabId = slabId;
        this.courseId = courseId;
        this. gradeLabel = gradeLabel;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getSlabId() { return slabId; }
    public int getCourseId() { return courseId; }
    public String getGradeLabel() { return gradeLabel; }
    public double getMinScore() { return minScore; }
    public double getMaxScore() { return maxScore; }
}