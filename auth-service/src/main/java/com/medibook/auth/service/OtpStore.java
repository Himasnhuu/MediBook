package com.medibook.auth.service;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class OtpStore {

    // Map: email → {otp, expiry}
    private final Map<String, String> otpMap = new HashMap<>();
    private final Map<String, LocalDateTime> expiryMap = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpMap.put(email, otp);
        expiryMap.put(email, LocalDateTime.now().plusMinutes(10)); // 10 min expiry
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String stored = otpMap.get(email);
        LocalDateTime expiry = expiryMap.get(email);
        if (stored == null || expiry == null) return false;
        if (LocalDateTime.now().isAfter(expiry)) {
            otpMap.remove(email);
            expiryMap.remove(email);
            return false;
        }
        return stored.equals(otp);
    }

    public void clearOtp(String email) {
        otpMap.remove(email);
        expiryMap.remove(email);
    }
}