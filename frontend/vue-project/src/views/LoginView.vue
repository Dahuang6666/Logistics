<template>
  <div class="login-container">
    <el-form
      ref="loginFormRef"
      :model="loginData"
      class="login-form"
      @submit.prevent="handleLogin"
    >
      <h2>用户登录</h2>
      <el-form-item>
        <el-input v-model="loginData.userNo" placeholder="请输入学号或工号" clearable />
      </el-form-item>
      <el-form-item>
        <el-input v-model="loginData.password" type="password" placeholder="请输入密码" show-password clearable />
      </el-form-item>
      <!-- 新增：验证码模块 -->
      <el-form-item>
        <div class="captcha-container">
          <!-- 验证码输入框 -->
          <el-input
            v-model="loginData.captchaInput"
            placeholder="请输入验证码"
            clearable
            class="captcha-input"
          />
          <!-- 其他代码不变，只改验证码图片区域 -->
          <div class="captcha-img-box" @click="refreshCaptcha">
            <!-- 修复：确保前缀拼接正确，去掉多余空格 -->
            <img
              v-if="captchaImage" :src="this.captchaImage"  alt="验证码" class="captcha-img"
            />
            <div v-else class="captcha-loading">加载中...</div>
          </div>
        </div>
      </el-form-item>
      <div class="role-container">
        <el-radio v-model="loginData.role" :label="1" class="role-left">用户</el-radio>
        <el-radio v-model="loginData.role" :label="4" class="role-right">管理员</el-radio>
      </div>
      <el-form-item class="btn-group">
        <el-button type="default" native-type="button" @click="handleRegister">注册</el-button>
        <el-button type="primary" native-type="submit">登录</el-button>
      </el-form-item>
      <div class="forgot-password">
        <a @click="handleForgotPassword">忘记密码？</a>
      </div>
    </el-form>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { login } from '@/utils/api.js'
// 导入验证码接口
import { getCaptcha, verifyCaptcha } from '@/utils/api.js' // 确保接口路径正确

export default {
  name: 'LoginView',
  data() {
    return {
      loginData: {
        userNo: '',
        password: '',
        role: 0,
        captchaInput: '',
        captchaId: ''
      },
      captchaImage: ''
    }
  },
  mounted() {
    // 页面加载时自动获取验证码
    this.refreshCaptcha()
  },
  methods: {
    // 获取/刷新验证码
    async refreshCaptcha() {
      try {
        const response = await getCaptcha()
        console.log("response", response)
        this.captchaImage = `data:image/png;base64,${response.data.captcha}`;
        this.loginData.captchaId = response.data.captchaId // 保存验证码ID
        this.loginData.captchaInput = ''
      } catch (error) {
        ElMessage.error('验证码加载失败，请刷新重试')
        console.error('验证码获取失败:', error)
      }
    },
    // 验证验证码
    async checkCaptcha() {
      if (!this.loginData.captchaInput.trim()) {
        ElMessage.error('请输入验证码')
        return false
      }
      try {
        // 调用验证接口
        const response = await verifyCaptcha({
          captchaId: this.loginData.captchaId,
          captchaInput: this.loginData.captchaInput
        })
        // 验证成功返回true
        return response.code === 200
      } catch (error) {
        ElMessage.error('验证码错误，请重新输入')
        this.refreshCaptcha()
        return false
      }
    },
    // 登录逻辑（整合验证码验证）
    async handleLogin() {
      // 1. 基础信息校验
      if (!this.loginData.userNo.trim() || !this.loginData.password.trim()) {
        ElMessage({
          message: '用户名和密码不能为空',
          type: 'error',
          duration: 3000
        })
        return
      }
      if (this.loginData.role === 0) {
        ElMessage({
          message: '请选择角色',
          type: 'error',
          duration: 3000
        })
        return
      }
      // 2. 验证码验证
      const captchaValid = await this.checkCaptcha()
      if (!captchaValid) {
        return
      }
      // 3. 执行登录
      try {
        const response = await login(this.loginData)
        console.log(response.data)
        ElMessage.success('登录成功！')
        console.log('登录用户:', this.loginData.userNo)
      } catch (error) {
        ElMessage.error('登录失败，请检查账号密码或网络')
        console.error('登录失败:', error)
        // 登录失败刷新验证码
        await this.refreshCaptcha()
      }
    },
    async handleRegister() {
      this.$router.push({ name: 'register' })
    },
    handleForgotPassword() {

    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background-image: url('@/assets/background1.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  padding: 20px;
}

.login-form {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  width: 100%;
  max-width: 400px;
  border: 1px solid rgba(255, 255, 255, 0.9);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #222;
  font-weight: 600;
}

.role-container {
  position: relative;
  height: 40px;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 1rem 0;
}

.role-left {
  position: absolute;
  left: 50%;
  transform: translateX(-120px);
}

.role-right {
  position: absolute;
  left: 50%;
  transform: translateX(60px);
}

.btn-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 1rem;
}

.btn-group .el-button--default {
  flex: 1;
  padding: 10px 0;
  border-radius: 20px;
  border: 1px solid #a8d8b9;
  background: #f0fcf5;
  color: #4cae60;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-group .el-button--default:hover {
  background: #e0f8e8;
  border-color: #86c997;
}

.btn-group .el-button--primary {
  flex: 1;
  padding: 10px 0;
  border-radius: 20px;
  background: #4cae60;
  border: none;
  color: white;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(76, 174, 96, 0.2);
  transition: all 0.3s;
}

.btn-group .el-button--primary:hover {
  background: #3da052;
  box-shadow: 0 6px 16px rgba(76, 174, 96, 0.3);
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e4e7ed;
}

/* 忘记密码样式 */
.forgot-password {
  text-align: right;
  margin-top: 10px;
}

.forgot-password a {
  font-size: 12px;
  color: #409eff;
  text-decoration: none;
}

.forgot-password a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* 核心：验证码容器样式（总长度和密码框一致） */
.captcha-container {
  display: flex;
  width: 100%;
  gap: 8px; /* 输入框和图片的间距 */
}

/* 验证码输入框：占2/3宽度 */
.captcha-input {
  flex: 2;
}

/* 验证码图片区域：占1/3宽度 */
.captcha-img-box {
  flex: 1;
  height: 50px; /* 和输入框高度一致 */
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer; /* 点击刷新 */
  overflow: hidden;
}

/* 验证码图片样式 */
.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 验证码加载中样式 */
.captcha-loading {
  font-size: 12px;
  color: #999;
}

/* 鼠标悬停验证码图片时的提示 */
.captcha-img-box:hover {
  border-color: #409eff;
}
</style>