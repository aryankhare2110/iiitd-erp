package edu.univ.erp.domain;

public class Student {

    private int studentId;
    private int userId;
    private String degreeLevel;
    private String branch;
    private int year;
    private String term;
    private String rollNo;
    private String fullName;

    public Student(int studentId, int userId, String degreeLevel, String branch, int year, String term, String rollNo, String fullName) {
        this.studentId = studentId;
        this.userId = userId;
        this.degreeLevel = degreeLevel;
        this.branch = branch;
        this.year = year;
        this.term = term;
        this.rollNo = rollNo;
        this.fullName = fullName;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getUserId() {
        return userId;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public String getBranch() {
        return branch;
    }

    public int getYear() {
        return year;
    }

    public String getTerm() {
        return term;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getFullName() {
        return fullName;
    }

}