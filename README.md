# 宿舍管理系统

高校宿舍后勤管理平台，支持学生报修、换宿申请、投诉建议，以及管理端的用户/宿舍/公告/数据统计等功能，集成 DeepSeek AI 辅助报修分类。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5 + MyBatis 3 |
| 数据库 | MySQL 8.0 |
| 前端 | Vue 3 + Vite + Element Plus |
| AI 集成 | LangChain4j + DeepSeek |
| 接口文档 | Knife4j (Swagger) |
| 其他 | Spring Security Crypto、Kaptcha 验证码、PageHelper 分页、ECharts 图表 |

## 功能模块

- **学生端**：报修申请、换宿申请、投诉建议、公告查看、个人信息
- **宿管端**：报修处理、换宿审批、反馈回复
- **系统管理员端**：用户管理、宿舍楼/房间管理、公告管理、数据统计
- **AI 辅助**：自动识别报修类型与紧急程度

## 快速开始

### 环境要求

- JDK 19+
- MySQL 8.0+
- Maven 3.6+
- Node.js 18+

### 1. 数据库初始化

```sql
CREATE DATABASE school_backend_manage DEFAULT CHARACTER SET utf8mb4;
```

然后导入表结构：

```bash
mysql -u root -p school_backend_manage < school_backend_manage.sql
```

### 2. 敏感配置

在 `src/main/resources/` 下创建 `application-secret.properties`

```properties
spring.datasource.password=你的数据库密码
spring.mail.username=你的QQ邮箱
spring.mail.password=你的QQ邮箱SMTP授权码
```

### 3. AI 功能配置

在 `src/main/resources/` 下创建 `apiKey.txt`，写入 DeepSeek API Key：

```
sk-xxxxxxxxxxxxxxxx
```

申请地址：https://platform.deepseek.com

如不需要 AI 功能，可注释掉 `OpenAiModelConfig.java` 中的 `@Bean`。

### 4. 启动后端

```bash
./mvnw spring-boot:run        # Windows: mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`，接口文档见 `http://localhost:8080/doc.html`。

### 5. 启动前端

```bash
cd frontend/vue-project
npm install
npm run dev
```

前端默认运行在 `http://localhost:5173`。

## 注意事项

- **敏感文件**：`application-secret.properties` 和 `apiKey.txt` 不上传 GitHub，clone 后需自行创建
- **邮件功能**：QQ 邮箱 SMTP 授权码需在 QQ 邮箱设置中生成（设置 → 账户 → POP3/SMTP 服务）
- **默认头像**：`src/main/resources/static/imgs/default.jpg` 不可删除，否则无头像用户会加载失败
- **数据库脚本**：仓库中的 SQL 仅含表结构，无初始数据，需手动创建管理员账号或通过注册接口注册
