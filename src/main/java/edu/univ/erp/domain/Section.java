package edu.univ.erp.domain;

public class Section {

    private int sectionID;
    private int courseID;
    private int instructorID;
    private String term;
    private int year;
    private String room;
    private int capacity;

    public Section(int sectionID, int courseID, int instructorID, String term, int year, String room, int capacity) {
        this.sectionID = sectionID;
        this.courseID = courseID;
        this.instructorID = instructorID;
        this.term = term;
        this.year = year;
        this.room = room;
        this.capacity = capacity;
    }

    public int getSectionID() {
        return sectionID;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public String getTerm() {
        return term;
    }

    public int getYear() {
        return year;
    }

    public String getRoom() {
        return room;
    }

    public int getCapacity() {
        return capacity;
    }

}
