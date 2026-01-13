package com.healthai.assistant

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.healthai.assistant.databinding.ActivityPlanDetailBinding
import com.healthai.assistant.model.ActivateUserRequest
import com.healthai.assistant.network.ApiClient
import com.healthai.assistant.network.ApiService
import com.healthai.assistant.service.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanDetailBinding
    private lateinit var apiService: ApiService
    private lateinit var analyticsService: AnalyticsService
    private lateinit var sharedPreferences: SharedPreferences
    private var isPlaying = false
    private var isVoiceEnabled = true
    private var timer: CountDownTimer? = null
    private val totalTime = 3 * 60 * 1000L // 3 minutes
    private var remainingTime = totalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化API服务
        apiService = ApiClient.createService(ApiService::class.java)
        // 初始化埋点服务
        analyticsService = AnalyticsService(this)
        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences("health_ai_prefs", MODE_PRIVATE)

        // 获取传递的场景信息
        val sceneId = intent.getStringExtra("scene_id") ?: "neck"
        val sceneName = intent.getStringExtra("scene_name") ?: "肩颈拉伸"

        // 设置场景名称
        binding.sceneName.text = sceneName

        // 设置动画资源
        setupAnimation(sceneId)

        // 开始/暂停按钮点击事件
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // 语音开关按钮点击事件
        binding.voiceToggleButton.setOnClickListener {
            toggleVoice()
        }

        // 一键打卡按钮点击事件
        binding.punchCardButton.setOnClickListener {
            punchCard()
        }
    }

    /**
     * 设置动画资源
     */
    private fun setupAnimation(sceneId: String) {
        val animationResId = when (sceneId) {
            "neck" -> R.drawable.neck_animation
            "eye" -> R.drawable.eye_animation
            "back" -> R.drawable.back_animation
            else -> R.drawable.neck_animation
        }
        binding.animationView.setImageResource(animationResId)
    }

    /**
     * 开始计时器
     */
    private fun startTimer() {
        isPlaying = true
        binding.playPauseButton.text = "暂停"

        timer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
                updateProgressBar(millisUntilFinished)
            }

            override fun onFinish() {
                isPlaying = false
                binding.playPauseButton.text = "开始"
                binding.timerDisplay.text = "00:00"
                binding.progressBar.progress = 180
                binding.punchCardButton.isEnabled = true
                Toast.makeText(this@PlanDetailActivity, "方案完成！", Toast.LENGTH_SHORT).show()
                
                // 上报方案完成事件
                analyticsService.reportEvent(AnalyticsService.EVENT_PLAN_FINISH)
            }
        }.start()
    }

    /**
     * 暂停计时器
     */
    private fun pauseTimer() {
        isPlaying = false
        binding.playPauseButton.text = "开始"
        timer?.cancel()
    }

    /**
     * 更新计时器显示
     */
    private fun updateTimerDisplay(millisUntilFinished: Long) {
        val seconds = (millisUntilFinished / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        binding.timerDisplay.text = String.format("%02d:%02d", minutes, remainingSeconds)
    }

    /**
     * 更新进度条
     */
    private fun updateProgressBar(millisUntilFinished: Long) {
        val totalSeconds = totalTime / 1000
        val remainingSeconds = millisUntilFinished / 1000
        val progress = (totalSeconds - remainingSeconds).toInt()
        binding.progressBar.progress = progress
    }

    /**
     * 切换语音开关
     */
    private fun toggleVoice() {
        isVoiceEnabled = !isVoiceEnabled
        binding.voiceToggleButton.text = if (isVoiceEnabled) "语音" else "静音"
        Toast.makeText(this, if (isVoiceEnabled) "语音已开启" else "语音已关闭", Toast.LENGTH_SHORT).show()
    }

    /**
     * 一键打卡
     */
    private fun punchCard() {
        binding.punchCardButton.isEnabled = false

        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getString("userId", null)

        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Toast.makeText(this, "登录状态已失效，请重新登录", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = ActivateUserRequest(userId, token)
                val response = apiService.activateUser(request)
                
                runOnUiThread {
                    if (response.code == 0) {
                        // 上报激活成功事件
                        analyticsService.reportEvent(AnalyticsService.EVENT_ACTIVE_SUCC)
                        
                        Toast.makeText(this@PlanDetailActivity, "打卡成功！", Toast.LENGTH_SHORT).show()
                        // 打卡成功后返回场景选择页
                        val intent = Intent(this@PlanDetailActivity, SceneSelectionActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@PlanDetailActivity, response.message, Toast.LENGTH_SHORT).show()
                        binding.punchCardButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PlanDetailActivity, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show()
                    binding.punchCardButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
