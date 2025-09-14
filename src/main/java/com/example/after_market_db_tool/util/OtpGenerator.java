package com.example.after_market_db_tool.util;

import java.security.SecureRandom;

public class OtpGenerator {
    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Generates a 6-digit number
        return String.valueOf(otp);
    }
}
