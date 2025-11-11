package edu.univ.erp.auth.hash;

import com.password4j.Hash;
import com.password4j.Password;

public class PasswordHasher {
    public static String hash(String plain) {
        Hash h = Password.hash(plain).addRandomSalt().withArgon2();
        return h.getResult();
    }

    public static boolean verify(String input, String stored) {
        return Password.check(input, stored).withArgon2();
    }


    // 🧪 Simple demo
    public static void main(String[] args) {
        String raw = "MySecurePassword123";
        String hashed = hash(raw);
        System.out.println("🔐 Hashed password: " + hashed);
        System.out.println("✅ Verify correct: " + verify("MySecurePassword123", hashed));
        System.out.println("❌ Verify wrong: " + verify("WrongPassword", hashed));
    }
}