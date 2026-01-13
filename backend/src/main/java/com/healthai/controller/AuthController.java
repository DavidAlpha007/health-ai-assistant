package com.healthai.controller;

import com.healthai.model.request.LoginRequest;
import com.healthai.model.request.SendCodeRequest;
import com.healthai.model.response.ApiResponse;
import com.healthai.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    public ApiResponse<Map<String, Long>> sendCode(@RequestBody SendCodeRequest request) {
        log.debug("Received send code request for phone: {}", request.getPhone());
        
        // Validate phone number format
        if (!isValidPhoneNumber(request.getPhone())) {
            return ApiResponse.error(10002, "手机号格式错误");
        }
        
        // Send verification code
        Long sendTime = authService.sendVerificationCode(request.getPhone());
        
        // Prepare response
        Map<String, Long> response = new HashMap<>();
        response.put("send_time", sendTime);
        
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest request) {
        log.debug("Received login request for phone: {}", request.getPhone());
        
        // Validate phone number format
        if (!isValidPhoneNumber(request.getPhone())) {
            return ApiResponse.error(10002, "手机号格式错误");
        }
        
        // Verify login code
        String token = authService.verifyLogin(request.getPhone(), request.getCode());
        if (token == null) {
            return ApiResponse.error(10003, "验证码错误或已过期");
        }
        
        // Create user ID from phone number (same logic as in AuthServiceImpl)
        String userId = "user_" + request.getPhone().substring(request.getPhone().length() - 6);
        
        // Prepare response
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("user_id", userId);
        
        return ApiResponse.success(response);
    }

    /**
     * Validate Chinese phone number format (11 digits, starting with 1)
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^1\\d{10}$");
    }
}
