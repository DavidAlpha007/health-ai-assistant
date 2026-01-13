package com.healthai.controller;

import com.healthai.model.request.ActivateUserRequest;
import com.healthai.model.response.ApiResponse;
import com.healthai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/activate")
    public ApiResponse<Map<String, Object>> activate(@RequestBody ActivateUserRequest request) {
        log.debug("Received activate request for user: {}", request.getUser_id());
        
        // Validate user ID
        if (request.getUser_id() == null || request.getUser_id().isEmpty()) {
            return ApiResponse.error(10001, "无效请求参数");
        }
        
        // Activate the user
        boolean activated = userService.activateUser(request.getUser_id());
        
        // Check activation status
        if (!activated) {
            return ApiResponse.error(10005, "服务器内部错误");
        }
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("activated", true);
        response.put("activate_time", LocalDateTime.now());
        
        return ApiResponse.success(response);
    }
}
