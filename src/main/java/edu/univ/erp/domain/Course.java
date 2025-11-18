package edu.univ.erp.domain;

public class Course {

    private int courseID;
    private int departmentID;
    private String code;
    private String title;
    private int credits;
    private String prerequisites;

    public Course(int courseID, int departmentID, String code, String title, int credits, String prerequisites) {
        this.courseID = courseID;
        this.departmentID = departmentID;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.prerequisites = prerequisites;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getDepartmentID() {
        return departmentID;
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
