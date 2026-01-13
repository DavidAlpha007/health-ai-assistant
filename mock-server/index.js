const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 8080;

// Middleware
app.use(cors());
app.use(express.json());

// Mock data
const mockUsers = new Map();

// Health check endpoint
app.get('/api/v1/health', (req, res) => {
  res.json({
    code: 0,
    message: 'success',
    data: {
      status: 'UP',
      service: 'Health AI Assistant',
      version: '1.0.0'
    }
  });
});

// Send verification code endpoint
app.post('/api/v1/auth/send-code', (req, res) => {
  const { phone } = req.body;
  
  console.log(`Sending verification code to: ${phone}`);
  
  // Simulate sending code
  mockUsers.set(phone, { code: '123456', sendTime: Date.now() });
  
  res.json({
    code: 0,
    message: 'success',
    data: { send_time: Date.now() }
  });
});

// Login endpoint
app.post('/api/v1/auth/login', (req, res) => {
  const { phone, code } = req.body;
  
  console.log(`Login attempt for: ${phone} with code: ${code}`);
  
  // Verify code
  const user = mockUsers.get(phone);
  if (user && user.code === code) {
    // Generate mock token
    const token = `mock_token_${Date.now()}`;
    const userId = `user_${phone.substring(phone.length - 6)}`;
    
    res.json({
      code: 0,
      message: 'success',
      data: { token, userId }
    });
  } else {
    res.json({
      code: 10003,
      message: '验证码错误或已过期'
    });
  }
});

// Activate user endpoint
app.post('/api/v1/user/activate', (req, res) => {
  const { userId, token } = req.body;
  
  console.log(`Activating user: ${userId} with token: ${token}`);
  
  res.json({
    code: 0,
    message: 'success',
    data: { success: true }
  });
});

// Report event endpoint
app.post('/api/v1/analytics/report', (req, res) => {
  const { eventId, timestamp, deviceId, osVersion, appVersion, extraParams } = req.body;
  
  console.log(`Received event: ${eventId} from device: ${deviceId} at ${new Date(timestamp).toISOString()}`);
  console.log(`Extra params: ${JSON.stringify(extraParams)}`);
  
  res.json({
    code: 0,
    message: 'success',
    data: { success: true }
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`Mock server running on port ${PORT}`);
  console.log('Available endpoints:');
  console.log('- GET /api/v1/health');
  console.log('- POST /api/v1/auth/send-code');
  console.log('- POST /api/v1/auth/login');
  console.log('- POST /api/v1/user/activate');
  console.log('- POST /api/v1/analytics/report');
});