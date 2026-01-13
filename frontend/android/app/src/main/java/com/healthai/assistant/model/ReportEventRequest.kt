package com.healthai.assistant.model

/**
 * 埋点事件上报请求
 */
data class ReportEventRequest(
    val eventId: String,
    val timestamp: Long,
    val deviceId: String,
    val osVersion: String,
    val appVersion: String,
    val extraParams: Map<String, String> = emptyMap()
)
