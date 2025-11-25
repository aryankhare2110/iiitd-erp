package edu.univ.erp.domain;

public class Notification {

    private int id;
    private String message;
    private String sentByEmail;
    private String sentAt;

    public Notification(int id, String message, String sentByEmail, String sentAt) {
        this.id = id;
        this.message = message;
        this.sentByEmail = sentByEmail;
        this.sentAt = sentAt;
    }

    public int getId() { return id; }
    public String getMessage() { return message; }
    public String getSentByEmail() { return sentByEmail; }
    public String getSentAt() { return sentAt; }
}