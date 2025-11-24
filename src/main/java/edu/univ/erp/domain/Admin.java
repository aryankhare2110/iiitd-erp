package edu.univ.erp.domain;

public class Admin {

    private final int userId;
    private final String email;
    private final String status;

    public Admin(int userId, String email, String status) {
        this.userId = userId;
        this.email = email;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}