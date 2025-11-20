package edu.univ.erp.domain;

public class TimetableEntry {
    private String code;
    private String name;
    private String fullName;
    private String room;
    private String day;
    private String startTime;
    private String endTime;
    private String description;

    public TimetableEntry(String code, String name, String fullname, String room, String day, String startTime, String endTime, String description) {
        this.code=code;
        this.name=name;
        this.fullName=fullname;
        this.room=room;
        this.day=day;
        this.startTime=startTime;
        this.endTime=endTime;
        this.description=description;
    }

    public String getCode() {
        return code;
    }
    public String getname() {
        return name;
    }
    public String getFullName() {
        return fullName;
    }
    public String getRoom() {
        return room;
    }
    public String getDay() {
        return day;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getDescription() {
        return description;
    }
}