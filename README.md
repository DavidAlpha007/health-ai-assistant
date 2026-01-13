# 健康AI助手

一个基于前后端分离架构的健康缓解助手应用，提供肩颈拉伸、眼部放松和腰背拉伸等缓解方案。

## 项目特点

- 📱 支持多端访问（Android原生应用 + Web应用）
- 🎯 3个核心缓解场景（肩颈、眼部、腰背）
- ⏱️ 智能计时和进度追踪
- 📊 完整的埋点统计系统
- 🔐 安全的手机号验证码登录
- 🚀 前后端分离架构，易于扩展

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.2
- **语言**: Java 17
- **数据库**: H2（开发环境）/ MySQL（生产环境）
- **认证**: JWT
- **构建工具**: Maven

### 前端
- **Android**: Kotlin + Jetpack
- **Web**: HTML5 + CSS3 + JavaScript
- **网络请求**: Retrofit2（Android）/ Fetch API（Web）
- **构建工具**: Gradle

## 项目结构

```
Health_AI/
├── backend/              # 后端Spring Boot应用
│   ├── src/              # 后端源代码
│   ├── pom.xml           # Maven配置文件
│   └── Dockerfile        # Docker构建文件
├── frontend/             # 前端应用
│   ├── android/          # Android原生应用
│   └── ios/              # iOS原生应用（预留）
├── web-app/              # Web应用
│   ├── index.html        # Web应用入口
│   └── app.js            # Web应用逻辑
├── mock-server/          # 模拟后端服务器
│   ├── index.js          # 模拟服务器代码
│   └── package.json      # Node.js依赖配置
├── docs/                 # 文档目录
│   ├── deployment_plan.md# 部署计划
│   └── specs.md          # 需求规格
├── resources/            # 资源文件
│   └── animations/       # 动图资源
└── docker-compose.yml    # Docker Compose配置
```

## 快速开始

### 1. 启动模拟服务器（推荐，适合快速测试）

```bash
# 进入模拟服务器目录
cd mock-server

# 安装依赖
npm install

# 启动模拟服务器
npm start
```

模拟服务器将运行在 `http://localhost:8080`

### 2. 启动Web应用

```bash
# 进入Web应用目录
cd web-app

# 启动本地HTTP服务器
python3 -m http.server 8081
```

在浏览器中访问 `http://localhost:8081`

### 3. 完整后端部署（可选）

#### 环境要求
- Java 17+
- Maven 3.8+

```bash
# 进入后端目录
cd backend

# 构建项目
mvn clean package

# 启动应用
java -jar target/health-ai-assistant-1.0.0.jar
```

后端服务将运行在 `http://localhost:8080/api/v1`

### 4. Docker部署（可选）

```bash
# 在项目根目录执行
docker-compose up -d --build
```

## 使用说明

### Web应用使用

1. **访问应用**：在浏览器中打开 `http://localhost:8081`
2. **登录**：
   - 输入手机号（例如：13800138000）
   - 点击"获取验证码"
   - 输入验证码"123456"（模拟环境固定验证码）
   - 点击"一键登录"
3. **选择场景**：选择肩颈拉伸、眼部放松或腰背拉伸
4. **执行方案**：
   - 点击"开始"按钮开始执行
   - 可以暂停/继续计时器
   - 可以开关语音
5. **一键打卡**：方案完成后，点击"一键打卡"完成打卡

### Android应用使用

1. 在Android Studio中打开 `frontend/android` 目录
2. 连接Android设备或启动模拟器
3. 构建并运行应用
4. 按照应用内提示完成登录和使用

## 核心功能

### 1. 登录功能
- 手机号验证码登录
- 登录状态持久化
- 安全的JWT认证

### 2. 场景选择
- 3个核心缓解场景
- 清晰的场景描述和图标
- 平滑的页面切换动画

### 3. 方案执行
- 3分钟智能计时
- 实时进度显示
- 暂停/继续功能
- 语音提示开关

### 4. 一键打卡
- 方案完成后自动解锁打卡
- 打卡数据同步到服务器
- 打卡状态实时更新

### 5. 埋点统计
- 登录页进入/成功
- 场景选择页进入/点击
- 方案完成/打卡成功
- 完整的用户行为分析

## API文档

### 认证相关
- `POST /api/v1/auth/send-code` - 发送验证码
- `POST /api/v1/auth/login` - 登录

### 用户相关
- `POST /api/v1/user/activate` - 用户激活（打卡）

### 埋点相关
- `POST /api/v1/analytics/report` - 上报埋点事件

### 健康检查
- `GET /api/v1/health` - 服务健康检查

## 测试账号

- **手机号**: 13800138000
- **验证码**: 123456（模拟环境固定验证码）

## 开发指南

### 后端开发
1. 确保安装Java 17+和Maven 3.8+
2. 使用IntelliJ IDEA或Eclipse导入后端项目
3. 运行 `HealthAiAssistantApplication.java` 启动应用

### 前端开发
1. Android开发：使用Android Studio打开 `frontend/android` 目录
2. Web开发：直接编辑 `web-app` 目录下的文件

## 部署指南

### 生产环境部署

1. **后端部署**
   - 安装Java 17+环境
   - 配置MySQL数据库
   - 运行 `java -jar` 启动后端服务
   - 配置Nginx反向代理

2. **前端部署**
   - Android: 生成签名APK，通过应用商店或企业分发
   - Web: 部署到Nginx或CDN

### 环境变量配置

| 变量名 | 描述 | 默认值 |
|--------|------|--------|
| `SPRING_DATASOURCE_URL` | 数据库URL | `jdbc:h2:mem:healthaidb` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `password` |
| `JWT_SECRET` | JWT密钥 | `healthaiassistantsecretkey1234567890` |
| `JWT_EXPIRATION` | JWT过期时间 | `86400000`（24小时） |

## 监控与维护

### 日志管理
- 后端日志默认输出到控制台
- 生产环境建议配置日志文件和ELK Stack

### 健康检查
- 访问 `http://localhost:8080/api/v1/health` 检查服务状态

### 常见问题

1. **API连接失败**
   - 检查模拟服务器是否启动
   - 检查API_BASE_URL配置是否正确
   - 检查网络连接

2. **登录失败**
   - 确保使用正确的手机号格式
   - 确保验证码输入正确
   - 检查模拟服务器日志

3. **计时器不工作**
   - 检查浏览器控制台是否有JavaScript错误
   - 确保JavaScript已启用

## 贡献指南

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 许可证

本项目采用Apache 2.0许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目负责人: AI助手
- 技术支持: healthai@example.com
- 问题反馈: 提交GitHub Issue

## 更新日志

### v1.0.0 (2026-01-13)
- 初始版本发布
- 实现3个核心缓解场景
- 支持Android和Web端
- 完整的埋点统计系统
- 模拟后端服务器

## 致谢

感谢所有为项目做出贡献的开发者和测试人员！

---

**健康AI助手** - 缓解疲劳，守护健康 🧘‍♀️✨
