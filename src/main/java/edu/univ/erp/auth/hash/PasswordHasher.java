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

    //Admin123 - Admin
    //Aryan@123 - Aryan Khare (aryan24124@iiitd.ac.in)
    //Ravi&123 - Ravi Sharma (ravi.sharma@iiitd.ac.in)
    //Meera123$ - Meera Bansal (meera.bansal@iiitd.ac.in)
    //AnanyaVerma#123 - Ananya Verma (ananya23102@iiitd.ac.in)
    //Mehra@789 - Rohan Mehra (rohan22457@iiitd.ac.in)

}
