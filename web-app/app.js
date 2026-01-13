// 应用状态管理
const appState = {
    isLoggedIn: false,
    token: null,
    userId: null,
    currentScene: null,
    isPlaying: false,
    isVoiceEnabled: true,
    remainingTime: 3 * 60, // 3分钟，单位秒
    totalTime: 3 * 60,
    timerInterval: null
};

// API配置
const API_BASE_URL = 'http://localhost:8080/api/v1';

// 设备信息
const deviceInfo = {
    deviceId: 'web_device_' + Math.random().toString(36).substr(2, 9),
    osVersion: navigator.userAgent,
    appVersion: '1.0.0'
};

// DOM元素
const screens = {
    splash: document.getElementById('splash-screen'),
    login: document.getElementById('login-screen'),
    scene: document.getElementById('scene-screen'),
    plan: document.getElementById('plan-screen')
};

const toast = document.getElementById('toast');

// 页面切换函数
function showScreen(screenName) {
    // 隐藏所有屏幕
    Object.values(screens).forEach(screen => {
        screen.classList.remove('active');
    });
    // 显示指定屏幕
    screens[screenName].classList.add('active');
    
    // 上报埋点
    if (screenName === 'login') {
        reportEvent('MVP_Login_Enter');
    } else if (screenName === 'scene') {
        reportEvent('MVP_Scene_Enter');
    }
}

// Toast提示函数
function showToast(message) {
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => {
        toast.classList.remove('show');
    }, 2000);
}

// 启动页跳过
function skipSplash() {
    showScreen('login');
}

// 启动页自动跳转
setTimeout(() => {
    showScreen('login');
}, 3000);

// 登录相关功能
const phoneInput = document.getElementById('phone');
const codeInput = document.getElementById('code');
const getCodeBtn = document.getElementById('get-code-btn');
const loginBtn = document.getElementById('login-btn');

// 监听输入变化，更新登录按钮状态
function updateLoginBtnState() {
    const phone = phoneInput.value.trim();
    const code = codeInput.value.trim();
    loginBtn.disabled = !(phone.length === 11 && code.length === 6);
}

phoneInput.addEventListener('input', updateLoginBtnState);
codeInput.addEventListener('input', updateLoginBtnState);

// 发送验证码
async function sendCode() {
    const phone = phoneInput.value.trim();
    if (phone.length !== 11) {
        showToast('请输入正确的手机号');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/send-code`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ phone })
        });
        
        const data = await response.json();
        if (data.code === 0) {
            showToast('验证码已发送');
            // 开始倒计时
            startCountdown();
        } else {
            showToast(data.message || '发送验证码失败');
        }
    } catch (error) {
        console.error('发送验证码失败:', error);
        showToast('网络异常，请稍后重试');
    }
}

// 验证码倒计时
function startCountdown() {
    let countdown = 60;
    getCodeBtn.disabled = true;
    getCodeBtn.textContent = `${countdown}秒后重试`;
    
    const interval = setInterval(() => {
        countdown--;
        if (countdown <= 0) {
            clearInterval(interval);
            getCodeBtn.disabled = false;
            getCodeBtn.textContent = '获取验证码';
        } else {
            getCodeBtn.textContent = `${countdown}秒后重试`;
        }
    }, 1000);
}

// 登录
async function login() {
    const phone = phoneInput.value.trim();
    const code = codeInput.value.trim();
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ phone, code })
        });
        
        const data = await response.json();
        if (data.code === 0 && data.data) {
            appState.isLoggedIn = true;
            appState.token = data.data.token;
            appState.userId = data.data.userId;
            
            // 上报登录成功埋点
            reportEvent('MVP_Login_Succ', { userId: appState.userId });
            
            showToast('登录成功');
            showScreen('scene');
        } else {
            showToast(data.message || '登录失败');
        }
    } catch (error) {
        console.error('登录失败:', error);
        showToast('网络异常，请稍后重试');
    }
}

// 场景选择
function selectScene(sceneId, sceneName) {
    appState.currentScene = { id: sceneId, name: sceneName };
    
    // 上报场景点击埋点
    reportEvent(`MVP_Scene_Click_${sceneId.charAt(0).toUpperCase() + sceneId.slice(1)}`, {
        sceneId, sceneName
    });
    
    showScreen('plan');
    initPlan();
}

// 初始化方案详情页
function initPlan() {
    // 更新方案名称
    document.getElementById('plan-name').textContent = appState.currentScene.name;
    
    // 重置计时器
    appState.remainingTime = appState.totalTime;
    updateTimerDisplay();
    updateProgress();
    
    // 更新动画区域
    updateAnimation();
    
    // 重置按钮状态
    const playBtn = document.getElementById('play-btn');
    playBtn.textContent = '开始';
    playBtn.className = 'control-btn play-btn';
    appState.isPlaying = false;
    
    // 禁用打卡按钮
    document.getElementById('punch-btn').disabled = true;
}

// 语音提示内容
const voicePrompts = {
    neck: {
        start: '开始肩颈拉伸，跟着动画一起做',
        middle: '保持姿势，深呼吸',
        end: '肩颈拉伸完成，感觉怎么样？'
    },
    eye: {
        start: '开始眼部放松，跟着动画一起眨眨眼',
        middle: '眼球转动，缓解疲劳',
        end: '眼部放松完成，眼睛感觉舒服了吗？'
    },
    back: {
        start: '开始腰背拉伸，保持身体直立',
        middle: '慢慢伸展，感受肌肉放松',
        end: '腰背拉伸完成，身体感觉轻松了吗？'
    }
};

// 播放语音提示
function playVoicePrompt(sceneId, type) {
    if (!appState.isVoiceEnabled || !('speechSynthesis' in window)) {
        return;
    }
    
    const prompt = voicePrompts[sceneId][type];
    if (!prompt) return;
    
    const utterance = new SpeechSynthesisUtterance(prompt);
    utterance.lang = 'zh-CN';
    utterance.rate = 0.9;
    utterance.pitch = 1;
    utterance.volume = 1;
    
    speechSynthesis.speak(utterance);
}

// 更新动画区域
function updateAnimation() {
    const animationArea = document.getElementById('animation-area');
    const iconMap = {
        neck: '💪',
        eye: '👁️',
        back: '🩻'
    };
    
    // 移除所有动画类
    animationArea.className = 'animation-area';
    
    // 设置图标和动画类
    animationArea.textContent = iconMap[appState.currentScene.id] || '💪';
    animationArea.classList.add(`animation-${appState.currentScene.id}`);
    
    // 如果暂停状态，添加暂停类
    if (!appState.isPlaying) {
        animationArea.classList.add('animation-paused');
    }
}

// 开始/暂停计时器
function togglePlay() {
    const playBtn = document.getElementById('play-btn');
    const animationArea = document.getElementById('animation-area');
    
    if (appState.isPlaying) {
        // 暂停
        clearInterval(appState.timerInterval);
        appState.isPlaying = false;
        playBtn.textContent = '开始';
        playBtn.className = 'control-btn play-btn';
        
        // 暂停动画
        animationArea.classList.add('animation-paused');
    } else {
        // 开始
        startTimer();
        appState.isPlaying = true;
        playBtn.textContent = '暂停';
        playBtn.className = 'control-btn pause-btn';
        
        // 继续动画
        animationArea.classList.remove('animation-paused');
        
        // 播放开始语音提示
        playVoicePrompt(appState.currentScene.id, 'start');
    }
}

// 开始计时器
function startTimer() {
    let middlePromptPlayed = false;
    
    appState.timerInterval = setInterval(() => {
        appState.remainingTime--;
        updateTimerDisplay();
        updateProgress();
        
        // 播放中间语音提示（剩余时间为总时间的一半时）
        if (!middlePromptPlayed && appState.remainingTime === Math.floor(appState.totalTime / 2)) {
            playVoicePrompt(appState.currentScene.id, 'middle');
            middlePromptPlayed = true;
        }
        
        if (appState.remainingTime <= 0) {
            // 时间到
            clearInterval(appState.timerInterval);
            appState.isPlaying = false;
            document.getElementById('play-btn').textContent = '开始';
            document.getElementById('play-btn').className = 'control-btn play-btn';
            
            // 暂停动画
            document.getElementById('animation-area').classList.add('animation-paused');
            
            // 启用打卡按钮
            document.getElementById('punch-btn').disabled = false;
            
            // 上报方案完成埋点
            reportEvent('MVP_Plan_Finish', {
                sceneId: appState.currentScene.id,
                duration: appState.totalTime
            });
            
            // 播放结束语音提示
            playVoicePrompt(appState.currentScene.id, 'end');
            
            showToast('方案完成！');
        }
    }, 1000);
}

// 更新计时器显示
function updateTimerDisplay() {
    const minutes = Math.floor(appState.remainingTime / 60);
    const seconds = appState.remainingTime % 60;
    const timeStr = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    document.getElementById('plan-timer').textContent = timeStr;
}

// 更新进度条
function updateProgress() {
    const progress = ((appState.totalTime - appState.remainingTime) / appState.totalTime) * 100;
    document.getElementById('progress').style.width = `${progress}%`;
}

// 切换语音开关
function toggleVoice() {
    appState.isVoiceEnabled = !appState.isVoiceEnabled;
    const voiceBtn = document.getElementById('voice-btn');
    voiceBtn.textContent = appState.isVoiceEnabled ? '🔊 语音' : '🔇 静音';
    showToast(appState.isVoiceEnabled ? '语音已开启' : '语音已关闭');
}

// 一键打卡
async function punchCard() {
    if (!appState.token || !appState.userId) {
        showToast('登录状态已失效，请重新登录');
        showScreen('login');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/user/activate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                userId: appState.userId,
                token: appState.token
            })
        });
        
        const data = await response.json();
        if (data.code === 0) {
            // 上报激活成功埋点
            reportEvent('MVP_Active_Succ', {
                userId: appState.userId,
                sceneId: appState.currentScene.id
            });
            
            showToast('打卡成功！');
            showScreen('scene');
        } else {
            showToast(data.message || '打卡失败');
        }
    } catch (error) {
        console.error('打卡失败:', error);
        showToast('网络异常，请稍后重试');
    }
}

// 上报埋点事件
async function reportEvent(eventId, extraParams = {}) {
    try {
        await fetch(`${API_BASE_URL}/analytics/report`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                eventId,
                timestamp: Date.now(),
                ...deviceInfo,
                extraParams
            })
        });
        console.log(`埋点 ${eventId} 上报成功`);
    } catch (error) {
        console.error(`埋点 ${eventId} 上报失败:`, error);
    }
}

// 页面加载完成后初始化
window.addEventListener('DOMContentLoaded', () => {
    console.log('健康AI助手Web应用已加载');
    
    // 测试API连接
    testApiConnection();
});

// 测试API连接
async function testApiConnection() {
    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        const data = await response.json();
        if (data.code === 0) {
            console.log('API连接成功');
        } else {
            console.error('API连接失败:', data.message);
            showToast('API连接失败，请检查服务器状态');
        }
    } catch (error) {
        console.error('API连接失败:', error);
        showToast('API连接失败，请检查服务器状态');
    }
}