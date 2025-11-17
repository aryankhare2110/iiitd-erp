package edu.univ.erp.auth.session;

public class UserSession {

    private static int userID;
    private static String userEmail;
    private static String userRole;

    public static void startSession (int id, String email, String role) {
        userID = id;
        userEmail = email;
        userRole = role;
    }

    public static int getUserID() {
        return userID;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getUserRole() {
        return userRole;
    }

    public static void clear() {
        userID = 0;
        userRole = null;
        userEmail = null;
    }

    public static boolean isLoggedIn() {
        return userEmail != null;
    }
}
