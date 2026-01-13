package com.healthai.assistant.network

import com.healthai.assistant.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * 发送验证码
     */
    @POST("/auth/send-code")
    suspend fun sendVerificationCode(@Body request: SendCodeRequest): ApiResponse<SendCodeResponse>

    /**
     * 登录
     */
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    /**
     * 激活用户
     */
    @POST("/user/activate")
    suspend fun activateUser(@Body request: ActivateUserRequest): ApiResponse<BaseResponse>

    /**
     * 上报埋点事件
     */
    @POST("/analytics/report")
    suspend fun reportEvent(@Body request: ReportEventRequest): ApiResponse<BaseResponse>
}
