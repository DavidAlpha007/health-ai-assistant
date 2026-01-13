package com.healthai.assistant

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.healthai.assistant.databinding.ActivityLoginBinding
import com.healthai.assistant.model.LoginRequest
import com.healthai.assistant.model.SendCodeRequest
import com.healthai.assistant.network.ApiClient
import com.healthai.assistant.network.ApiService
import com.healthai.assistant.service.AnalyticsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: ApiService
    private lateinit var analyticsService: AnalyticsService
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化API服务
        apiService = ApiClient.createService(ApiService::class.java)
        // 初始化埋点服务
        analyticsService = AnalyticsService(this)
        // 初始化SharedPreferences
        sharedPreferences = getSharedPreferences("health_ai_prefs", MODE_PRIVATE)

        // 上报登录页进入事件
        analyticsService.reportEvent(AnalyticsService.EVENT_LOGIN_ENTER)

        // 设置按钮初始状态
        updateLoginButtonState()

        // 手机号输入监听
        binding.phoneInput.addTextChangedListener {
            updateLoginButtonState()
        }

        // 验证码输入监听
        binding.codeInput.addTextChangedListener {
            updateLoginButtonState()
        }

        // 获取验证码按钮点击事件
        binding.getCodeButton.setOnClickListener {
            val phone = binding.phoneInput.text.toString()
            if (isValidPhoneNumber(phone)) {
                sendVerificationCode(phone)
            } else {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
            }
        }

        // 登录按钮点击事件
        binding.loginButton.setOnClickListener {
            val phone = binding.phoneInput.text.toString()
            val code = binding.codeInput.text.toString()
            if (isValidPhoneNumber(phone) && isValidCode(code)) {
                login(phone, code)
            }
        }
    }

    /**
     * 更新登录按钮状态
     */
    private fun updateLoginButtonState() {
        val phone = binding.phoneInput.text.toString()
        val code = binding.codeInput.text.toString()
        binding.loginButton.isEnabled = isValidPhoneNumber(phone) && isValidCode(code)
    }

    /**
     * 验证手机号格式
     */
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1\\d{10}$"))
    }

    /**
     * 验证验证码格式
     */
    private fun isValidCode(code: String): Boolean {
        return code.matches(Regex("^\\d{6}$"))
    }

    /**
     * 发送验证码
     */
    private fun sendVerificationCode(phone: String) {
        binding.getCodeButton.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = SendCodeRequest(phone)
                val response = apiService.sendVerificationCode(request)
                
                runOnUiThread {
                    if (response.code == 0) {
                        Toast.makeText(this@LoginActivity, "验证码已发送", Toast.LENGTH_SHORT).show()
                        startCountdown()
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        binding.getCodeButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show()
                    binding.getCodeButton.isEnabled = true
                }
            }
        }
    }

    /**
     * 登录逻辑
     */
    private fun login(phone: String, code: String) {
        binding.loginButton.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = LoginRequest(phone, code)
                val response = apiService.login(request)
                
                runOnUiThread {
                    if (response.code == 0 && response.data != null) {
                        // 保存登录状态
                        with(sharedPreferences.edit()) {
                            putString("token", response.data.token)
                            putString("userId", response.data.userId)
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        // 上报登录成功事件
                        analyticsService.reportEvent(AnalyticsService.EVENT_LOGIN_SUCC)

                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        // 登录成功后跳转到场景选择页
                        val intent = Intent(this@LoginActivity, SceneSelectionActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        binding.loginButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show()
                    binding.loginButton.isEnabled = true
                }
            }
        }
    }

    /**
     * 验证码倒计时
     */
    private fun startCountdown() {
        val countdownTime = 60
        var remainingTime = countdownTime
        binding.getCodeButton.isEnabled = false

        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                remainingTime--
                if (remainingTime > 0) {
                    binding.getCodeButton.text = "$remainingTime秒后重试"
                    handler.postDelayed(this, 1000)
                } else {
                    binding.getCodeButton.text = "获取验证码"
                    binding.getCodeButton.isEnabled = true
                }
            }
        }
        handler.postDelayed(runnable, 1000)
    }
}
