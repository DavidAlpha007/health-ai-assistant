#!/bin/bash

# 测试后端服务是否正常运行

echo "Testing backend service..."

# 检查服务是否在运行
if curl -s http://localhost:8080/api/v1/health > /dev/null; then
    echo "✅ Backend service is running"
else
    echo "❌ Backend service is not running"
    exit 1
fi

# 测试登录接口
echo "Testing login endpoint..."
login_response=$(curl -s -X POST http://localhost:8080/api/v1/auth/send-code -H "Content-Type: application/json" -d '{"phone":"13800138000"}')

if echo "$login_response" | grep -q "success"; then
    echo "✅ Login endpoint is working"
else
    echo "❌ Login endpoint is not working"
    echo "Response: $login_response"
    exit 1
fi

echo "All tests passed! Backend service is running correctly."