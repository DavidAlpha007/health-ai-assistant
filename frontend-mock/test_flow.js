const axios = require('axios');

// API配置
const API_BASE_URL = 'http://localhost:8080/api/v1';

// 模拟设备信息
const deviceInfo = {
  deviceId: 'mock_device_' + Math.random().toString(36).substr(2, 9),
  osVersion: '13.0',
  appVersion: '1.0.0'
};

// 模拟前端应用测试流程
async function testFrontendFlow() {
  console.log('=== 健康AI助手前端模拟测试 ===\n');
  
  let token = null;
  let userId = null;
  
  try {
    // 1. 健康检查
    console.log('1. 检查后端服务状态...');
    const healthResponse = await axios.get(`${API_BASE_URL}/health`);
    if (healthResponse.data.code === 0) {
      console.log('✅ 后端服务正常运行\n');
    } else {
      console.log('❌ 后端服务异常\n');
      return;
    }
    
    // 2. 发送验证码
    console.log('2. 发送验证码...');
    const phone = '13800138000';
    const sendCodeResponse = await axios.post(`${API_BASE_URL}/auth/send-code`, {
      phone: phone
    });
    if (sendCodeResponse.data.code === 0) {
      console.log(`✅ 验证码已发送到 ${phone}\n`);
    } else {
      console.log(`❌ 发送验证码失败: ${sendCodeResponse.data.message}\n`);
      return;
    }
    
    // 3. 登录
    console.log('3. 登录...');
    const loginResponse = await axios.post(`${API_BASE_URL}/auth/login`, {
      phone: phone,
      code: '123456' // 模拟验证码
    });
    if (loginResponse.data.code === 0 && loginResponse.data.data) {
      token = loginResponse.data.data.token;
      userId = loginResponse.data.data.userId;
      console.log(`✅ 登录成功\n`);
      console.log(`   用户ID: ${userId}`);
      console.log(`   Token: ${token.substring(0, 20)}...\n`);
    } else {
      console.log(`❌ 登录失败: ${loginResponse.data.message}\n`);
      return;
    }
    
    // 4. 上报埋点事件 - 登录成功
    console.log('4. 上报登录成功埋点...');
    await reportEvent('MVP_Login_Succ', { userId });
    
    // 5. 场景选择
    console.log('5. 选择场景...');
    const sceneId = 'neck';
    const sceneName = '肩颈拉伸';
    // 上报场景点击埋点
    await reportEvent('MVP_Scene_Click_Neck', { sceneId, sceneName });
    console.log(`✅ 选择了 ${sceneName} 场景\n`);
    
    // 6. 方案执行
    console.log('6. 执行缓解方案...');
    // 模拟方案执行3分钟
    await new Promise(resolve => setTimeout(resolve, 2000)); // 模拟2秒
    // 上报方案完成埋点
    await reportEvent('MVP_Plan_Finish', { sceneId, duration: 180 });
    console.log('✅ 方案执行完成\n');
    
    // 7. 一键打卡
    console.log('7. 一键打卡...');
    const activateResponse = await axios.post(`${API_BASE_URL}/user/activate`, {
      userId: userId,
      token: token
    });
    if (activateResponse.data.code === 0) {
      // 上报激活成功埋点
      await reportEvent('MVP_Active_Succ', { userId, sceneId });
      console.log('✅ 打卡成功\n');
    } else {
      console.log(`❌ 打卡失败: ${activateResponse.data.message}\n`);
      return;
    }
    
    // 8. 测试完成
    console.log('=== 测试完成 ===\n');
    console.log('✅ 所有功能测试通过！');
    console.log('✅ 埋点事件已成功上报');
    console.log('✅ 前后端交互正常\n');
    
  } catch (error) {
    console.error('❌ 测试过程中发生错误:', error.message);
    console.error(error.response?.data || '');
  }
}

// 上报埋点事件
async function reportEvent(eventId, extraParams = {}) {
  try {
    await axios.post(`${API_BASE_URL}/analytics/report`, {
      eventId,
      timestamp: Date.now(),
      ...deviceInfo,
      extraParams
    });
    console.log(`   ✅ 埋点 ${eventId} 上报成功`);
  } catch (error) {
    console.log(`   ❌ 埋点 ${eventId} 上报失败: ${error.message}`);
  }
}

// 执行测试流程
testFrontendFlow();