package edu.univ.erp.domain;

public class Faculty {

    private int facultyId;
    private int userId;
    private int departmentId;
    private String designation;
    private String fullName;

    public Faculty(int facultyId, int userId, int departmentId, String designation, String fullName) {
        this.facultyId = facultyId;
        this.userId = userId;
        this.departmentId = departmentId;
        this.designation = designation;
        this.fullName = fullName;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public int getUserId() {
        return userId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDesignation() {
        return designation;
    }

    public String getFullName() {
        return fullName;
    }

}