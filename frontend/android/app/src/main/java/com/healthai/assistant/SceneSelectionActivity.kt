package com.healthai.assistant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.healthai.assistant.databinding.ActivitySceneSelectionBinding
import com.healthai.assistant.service.AnalyticsService

class SceneSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySceneSelectionBinding
    private lateinit var analyticsService: AnalyticsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySceneSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化埋点服务
        analyticsService = AnalyticsService(this)

        // 上报场景选择页进入事件
        analyticsService.reportEvent(AnalyticsService.EVENT_SCENE_ENTER)

        // 肩颈拉伸场景点击事件
        binding.neckScene.setOnClickListener {
            Toast.makeText(this, "肩颈拉伸", Toast.LENGTH_SHORT).show()
            // 上报肩颈场景点击事件
            analyticsService.reportEvent(AnalyticsService.EVENT_SCENE_CLICK_NECK)
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("scene_id", "neck")
            intent.putExtra("scene_name", "肩颈拉伸")
            startActivity(intent)
        }

        // 眼部放松场景点击事件
        binding.eyeScene.setOnClickListener {
            Toast.makeText(this, "眼部放松", Toast.LENGTH_SHORT).show()
            // 上报眼部场景点击事件
            analyticsService.reportEvent(AnalyticsService.EVENT_SCENE_CLICK_EYE)
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("scene_id", "eye")
            intent.putExtra("scene_name", "眼部放松")
            startActivity(intent)
        }

        // 腰背拉伸场景点击事件
        binding.backScene.setOnClickListener {
            Toast.makeText(this, "腰背拉伸", Toast.LENGTH_SHORT).show()
            // 上报腰背场景点击事件
            analyticsService.reportEvent(AnalyticsService.EVENT_SCENE_CLICK_BACK)
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("scene_id", "back")
            intent.putExtra("scene_name", "腰背拉伸")
            startActivity(intent)
        }
    }
}
