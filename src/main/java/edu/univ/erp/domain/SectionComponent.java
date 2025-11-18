package edu.univ.erp.domain;

public class SectionComponent {

    private int componentID;
    private int sectionID;
    private int typeID;
    private String day;
    private String startTime;
    private String endTime;
    private double weight;
    private String description;

    public SectionComponent (int componentID, int sectionID, int typeID, String day, String startTime, String endTime, double weight, String description) {
        this.componentID = componentID;
        this.sectionID = sectionID;
        this.typeID = typeID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weight = weight;
        this.description = description;
    }

    public int getComponentID() {
        return componentID;
    }

    public int getSectionID() {
        return sectionID;
    }

    public int getTypeID() {
        return typeID;
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

    public double getWeight() {
        return weight;
    }

    public String getDescription() {
        return description;
    }

}


