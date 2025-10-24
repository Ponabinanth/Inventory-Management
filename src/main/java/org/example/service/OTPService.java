package org.example.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPService {

    private final EmailUtil emailUtil;
    private static final Map<String, String> otpStore = new HashMap<>();

    public OTPService(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    public void generateAndSendOTP(String userIdentifier, String recipientEmail) {
        Random random = new Random();
        int otpInt = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpInt);

        otpStore.put(userIdentifier, otp);
        System.out.println("\nDEBUG: Sent OTP [" + otp + "] to " + recipientEmail);

        String subject = "Inventory System: One-Time Password (OTP) for Verification";
        String content = "Your one-time password (OTP) is: " + otp +
                "\nThis code is valid for 5 minutes.";

        emailUtil.sendReport(recipientEmail, subject, content);
    }

    public boolean verifyOTP(String userIdentifier, String enteredOTP) {
        String storedOTP = otpStore.get(userIdentifier);

        if (storedOTP == null) return false;

        boolean match = storedOTP.equals(enteredOTP);
        if (match) {
            otpStore.remove(userIdentifier);
            System.out.println("✅ OTP Verification successful.");
        } else {
            System.err.println("❌ OTP Verification failed: Incorrect code.");
        }
        return match;
    }
}