package com.healthai.service;

public interface AuthService {

    /**
     * Send verification code to a phone number
     * @param phone the phone number
     * @return the timestamp when the code was sent
     */
    Long sendVerificationCode(String phone);

    /**
     * Verify login with phone number and verification code
     * @param phone the phone number
     * @param code the verification code
     * @return a JWT token if verification is successful, null otherwise
     */
    String verifyLogin(String phone, String code);
}
