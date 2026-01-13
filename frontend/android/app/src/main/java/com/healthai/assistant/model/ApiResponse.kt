package com.healthai.assistant.model

/**
 * API响应基类
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
)

/**
 * 基础响应类
 */
data class BaseResponse(
    val success: Boolean
)

/**
 * 发送验证码响应
 */
data class SendCodeResponse(
    val sendTime: Long
)

/**
 * 登录响应
 */
data class LoginResponse(
    val token: String,
    val userId: String
)

/**
 * 发送验证码请求
 */
data class SendCodeRequest(
    val phone: String
)

/**
 * 登录请求
 */
data class LoginRequest(
    val phone: String,
    val code: String
)

/**
 * 激活用户请求
 */
data class ActivateUserRequest(
    val userId: String,
    val token: String
)
