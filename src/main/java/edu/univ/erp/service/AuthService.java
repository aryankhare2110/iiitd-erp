package edu.univ.erp.service;

import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.session.UserSession;
import edu.univ.erp.auth.store.AuthDAO;

public class AuthService {

    private final AuthDAO authDAO = new AuthDAO();

    public String login (String email, String password) { //For user login, updates lastLogin and returns the user role
        if (!authDAO.emailChecker(email)) {
            return null;
        }
        String status = authDAO.getStatus(email);
        if (status == null || status.equalsIgnoreCase("INACTIVE")) {
            return "INACTIVE";
        }
        String storedHash = authDAO.getHashedPassword(email);
        if (storedHash == null) {
            return null;
        }
        boolean verified = PasswordHasher.verify(password, storedHash);
        if (!verified) {
            return null;
        }
        int uid = authDAO.getUserId(email);
        String role = authDAO.getRole(email);
        UserSession.startSession(uid, email, role);
        authDAO.updateLastLogin(email);
        return role;
    }

    public boolean register (String email, String role, String plainPassword) { //Registers new user
        if (authDAO.emailChecker(email)) { //Ensures no duplicates
            return false;
        }
        String newHash = PasswordHasher.hash(plainPassword);
        return authDAO.registerNewUser(email, role, newHash);
    }

    public boolean resetPassword (String email, String plainPassword) { //To reset user password
        if (!authDAO.emailChecker(email)) {
            return false;
        }
        String newHash = PasswordHasher.hash(plainPassword);
        return authDAO.resetPassword(email, newHash);
    }

}
