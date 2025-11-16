package edu.univ.erp.auth.hash;

import com.password4j.*;

public class PasswordHasher {

    public static String hash(String plain) { //To hash the password
        Hash h = Password.hash(plain).addRandomSalt().withArgon2();
        return h.getResult();
    }

    public static boolean verify(String input, String stored) { //To verify the password
        return Password.check(input, stored).withArgon2();
    }

}
