package edu.univ.erp.domain;

public class Courses {

    private int courseID;
    private String code;
    private String title;
    private int credits;
    private String prerequisites;

    public Courses(int courseID, String code, String title, int credits, String prerequisites) {
        this.courseID = courseID;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.prerequisites = prerequisites;
    }

    public int getCourseID() {
        return courseID;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

}
