package edu.univ.erp.domain;

public class ComponentType {

    private int typeID;
    private String name;

    public ComponentType(int typeID, String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

}
