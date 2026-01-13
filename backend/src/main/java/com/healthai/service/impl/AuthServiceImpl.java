package com.healthai.service.impl;

import com.healthai.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    // In-memory storage for verification codes (MVP version)
    // In production, this should be replaced with a proper distributed cache
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    // Simple verification code length
    private static final int CODE_LENGTH = 6;
    
    // Verification code expiration time (5 minutes)
    private static final long CODE_EXPIRATION = TimeUnit.MINUTES.toMillis(5);

    @Override
    public Long sendVerificationCode(String phone) {
        log.debug("Sending verification code to phone: {}", phone);
        
        // Generate a random verification code
        String code = generateRandomCode(CODE_LENGTH);
        
        // Store the code with expiration time
        long timestamp = System.currentTimeMillis();
        verificationCodes.put(phone, new VerificationCode(code, timestamp + CODE_EXPIRATION));
        
        // In a real production environment, this would send an SMS with the code
        // For MVP, we'll just log the code
        log.info("Generated verification code for {}: {}", phone, code);
        
        return timestamp;
    }

    @Override
    public String verifyLogin(String phone, String code) {
        log.debug("Verifying login for phone: {}, code: {}", phone, code);
        
        // Check if the code exists and is valid
        VerificationCode storedCode = verificationCodes.get(phone);
        if (storedCode == null) {
            log.warn("No verification code found for phone: {}", phone);
            return null;
        }
        
        // Check if the code has expired
        long now = System.currentTimeMillis();
        if (now > storedCode.expirationTime) {
            log.warn("Verification code expired for phone: {}", phone);
            verificationCodes.remove(phone);
            return null;
        }
        
        // Check if the code matches
        if (!storedCode.code.equals(code)) {
            log.warn("Invalid verification code for phone: {}", phone);
            return null;
        }
        
        // Code is valid, generate JWT token
        String token = generateJwtToken(phone);
        
        // Remove the used code
        verificationCodes.remove(phone);
        
        log.debug("Login verified successfully for phone: {}", phone);
        return token;
    }

    /**
     * Generate a random verification code
     * @param length the length of the code
     * @return the generated code
     */
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * Generate a JWT token for the given phone number
     * @param phone the phone number
     * @return the generated JWT token
     */
    private String generateJwtToken(String phone) {
        // Create user ID from phone number (simple implementation for MVP)
        String userId = "user_" + phone.substring(phone.length() - 6);
        
        // Create signing key from secret
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(Base64.getEncoder().encodeToString(jwtSecret.getBytes())));
        
        // Build JWT token
        return Jwts.builder()
                .setSubject(userId)
                .claim("phone", phone)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Inner class to store verification code and expiration time
     */
    private static class VerificationCode {
        private final String code;
        private final long expirationTime;

        public VerificationCode(String code, long expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }
}
