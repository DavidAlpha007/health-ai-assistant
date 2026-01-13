package com.healthai.assistant.service

import android.content.Context
import android.os.Build
import android.util.Log
import com.healthai.assistant.model.ReportEventRequest
import com.healthai.assistant.network.ApiClient
import com.healthai.assistant.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AnalyticsService(private val context: Context) {

    private val apiService: ApiService = ApiClient.createService(ApiService::class.java)
    private val deviceId: String by lazy {
        // 生成设备ID
        "device_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16)
    }

    companion object {
        private const val TAG = "AnalyticsService"

        // 埋点事件ID常量
        const val EVENT_LOGIN_ENTER = "MVP_Login_Enter"
        const val EVENT_LOGIN_SUCC = "MVP_Login_Succ"
        const val EVENT_SCENE_ENTER = "MVP_Scene_Enter"
        const val EVENT_SCENE_CLICK_NECK = "MVP_Scene_Click_Neck"
        const val EVENT_SCENE_CLICK_EYE = "MVP_Scene_Click_Eye"
        const val EVENT_SCENE_CLICK_BACK = "MVP_Scene_Click_Back"
        const val EVENT_PLAN_FINISH = "MVP_Plan_Finish"
        const val EVENT_ACTIVE_SUCC = "MVP_Active_Succ"
    }

    /**
     * 上报埋点事件
     */
    fun reportEvent(eventId: String, extraParams: Map<String, String> = emptyMap()) {
        val timestamp = System.currentTimeMillis()
        Log.d(TAG, "Reporting event: $eventId at $timestamp")

        // 构建埋点请求
        val request = ReportEventRequest(
            eventId = eventId,
            timestamp = timestamp,
            deviceId = deviceId,
            osVersion = Build.VERSION.RELEASE,
            appVersion = getAppVersion(),
            extraParams = extraParams
        )

        // 异步上报埋点
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.reportEvent(request)
                if (response.code == 0) {
                    Log.d(TAG, "Event reported successfully: $eventId")
                } else {
                    Log.e(TAG, "Failed to report event: $eventId, error: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reporting event: $eventId", e)
                // TODO: 实现失败重试机制，将失败的埋点存储到本地，后续重试
            }
        }
    }

    /**
     * 获取应用版本
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get app version", e)
            "1.0.0"
        }
    }
}
