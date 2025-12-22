<template>
  <div class="login-container">
    <!-- 3D 翻转容器 -->
    <div class="flip-box" :class="{ flipped: isFlipped }">
      <!-- 正面：登录表单 -->
      <div class="form-panel login-panel">
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
            <el-input
              v-model="loginData.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item>
            <div class="captcha-container">
              <el-input
                v-model="loginData.captchaInput"
                placeholder="请输入验证码"
                clearable
                class="captcha-input"
              />
              <div class="captcha-img-box" @click="refreshCaptcha">
                <img
                  v-if="captchaImage"
                  :src="captchaImage"
                  alt="验证码"
                  class="captcha-img"
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
            <el-button type="default" @click="handleRegister">注册</el-button>
            <el-button type="primary" native-type="submit">登录</el-button>
          </el-form-item>
          <div class="forgot-password">
            <a @click="flipToForget">忘记密码？</a>
          </div>
        </el-form>
      </div>

      <!-- 反面：找回密码表单 -->
      <div class="form-panel forget-panel">
        <el-form
          ref="forgetFormRef"
          :model="forgetData"
          class="login-form"
          @submit.prevent="handleConfirmReset"
        >
          <h2>找回密码</h2>
          <el-form-item>
            <el-input
              v-model="forgetData.userNo"
              placeholder="请输入学号或工号"
              clearable
            />
          </el-form-item>
          <!-- 邮箱验证码行 -->
          <el-form-item>
            <div class="email-captcha-row">
              <el-input
                v-model="forgetData.emailCode"
                placeholder="请输入邮箱验证码"
                clearable
                class="email-code-input"
              />
              <el-button
                type="primary"
                @click="handleGetEmailCode"
                :loading="sending"
                :disabled="countdown > 0"
                class="send-code-btn"
              >
                {{ countdown > 0 ? `${countdown}s 后重发` : '获取邮箱验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <!-- 新密码输入 -->
          <el-form-item>
            <el-input
              v-model="forgetData.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item class="btn-group">
            <el-button type="default" @click="flipToLogin">返回登录</el-button>
            <el-button type="primary" native-type="submit">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import { login, getCaptcha, verifyCaptcha } from '@/utils/api.js'
// 新增导入
import { getEmail, sendEmail } from '@/utils/api.js'

export default {
  name: 'LoginView',
  data() {
    return {
      isFlipped: false,
      // 登录数据
      loginData: {
        userNo: '',
        password: '',
        role: 0,
        captchaInput: '',
        captchaId: ''
      },
      captchaImage: '',
      // 找回密码数据
      forgetData: {
        userNo: '',
        emailCode: '',
        newPassword: ''
      },
      sending: false,// 发送中状态
      countdown: 0, // 倒计时秒数，0 表示未激活
      countdownTimer: null // 定时器引用
    }
  },
  mounted() {
    this.refreshCaptcha()
  },
  methods: {
    async refreshCaptcha() {
      try {
        const response = await getCaptcha()
        this.captchaImage = `data:image/png;base64,${response.data.captcha}`
        this.loginData.captchaId = response.data.captchaId
        this.loginData.captchaInput = ''
      } catch (error) {
        ElMessage.error('验证码加载失败，请刷新重试')
      }
    },
    async checkCaptcha() {
      if (!this.loginData.captchaInput.trim()) {
        ElMessage.error('请输入验证码')
        return false
      }
        const response = await verifyCaptcha({
          captchaId: this.loginData.captchaId,
          captchaInput: this.loginData.captchaInput
        })
        if (response.data.code === 0) {
          ElMessage.error('验证码错误，请重新输入')
          await this.refreshCaptcha()
          return false
        }
        return true
    },
    async handleLogin() {
      if (!this.loginData.userNo.trim() || !this.loginData.password.trim()) {
        ElMessage.error('用户名和密码不能为空')
        return
      }
      if (this.loginData.role === 0) {
        ElMessage.error('请选择角色')
        return
      }
      const a = await this.checkCaptcha()
      if(a){
        try {
          const response = await login(this.loginData)
          if (response.data.code === 1) {
            ElMessage.success('登录成功！')
            console.log('登录用户:', this.loginData.userNo)
          }
          else{
            ElMessage.error(response.data.msg)
          }
        } catch (error) {
          ElMessage.error('登录失败，请检查账号密码或网络')
          await this.refreshCaptcha()
        }
      }
    },
    handleRegister() {
      this.$router.push({ name: 'register' })
    },

    // ===== 翻转控制 =====
    flipToForget() {
      this.isFlipped = true
    },
    flipToLogin() {
      this.isFlipped = false
    },

    // ===== 新增：获取邮箱验证码流程 =====
    async handleGetEmailCode() {
      // 防止倒计时期间重复点击
      if (this.countdown > 0) return;

      const { userNo } = this.forgetData;
      if (!userNo?.trim()) {
        ElMessage.warning('请先输入学号或工号');
        return;
      }

      try {
        // 1. 获取邮箱
        const emailRes = await getEmail(userNo);
        const email = emailRes.data.data;
        if(emailRes.data.code===0)
        {
          ElMessage.error(emailRes.data.msg)
          return
        }
        // 2. 弹出确认框
        await ElMessageBox.confirm(
          `系统将向以下邮箱发送验证码：<br/><strong>${email}</strong>`,
          '确认邮箱',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定',
            cancelButtonText: '不是这个邮箱',
            type: 'info'
          }
        );
        this.startCountdown();
        ElMessage.success('验证码已发送，请查收邮箱（含垃圾邮件箱）');
        sendEmail(userNo).catch(err => {
          console.warn('邮件发送异常（已忽略）:', err);
        });

      } catch (err) {
        if (err === 'cancel') {
          ElMessage.info('请联系管理员更新您的邮箱信息');
        } else {
          ElMessage.error('操作失败，请重试');
          console.error(err);
        }
      }
    },
    startCountdown() {
      this.countdown = 30; // 30秒倒计时

      // 清除可能存在的旧定时器
      if (this.countdownTimer) clearInterval(this.countdownTimer);

      this.countdownTimer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer);
          this.countdownTimer = null;
        }
      }, 1000);
    },

    // 暂时不做真实重置，只提示
    handleConfirmReset() {
      ElMessage.info('密码重置功能待开发')

    }
  }
}
</script>

<style scoped>
/* ========== 全局样式保持不变 ========== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
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

/* ========== 3D 翻转核心 ========== */
.flip-box {
  width: 100%;
  max-width: 400px;
  perspective: 1200px;
  position: relative;
  height: 520px;
  transform-style: preserve-3d;
}

.form-panel {
  position: absolute;
  width: 100%;
  top: 0;
  left: 0;
  backface-visibility: hidden; /* 确保这个属性存在 */
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.login-panel {
  transform: rotateY(0deg);
  pointer-events: auto;
}
.forget-panel {
  transform: rotateY(180deg);
  pointer-events: none;
}
.flip-box.flipped .login-panel {
  transform: rotateY(-180deg); /* 修改这里 */
  pointer-events: none;
}
.flip-box.flipped .forget-panel {
  transform: rotateY(0deg); /* 修改这里 */
  pointer-events: auto;
}
/* ========== 表单通用 ========== */
.login-form {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  padding: 2rem;
  border-radius: 12px;
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.9);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #222;
  font-weight: 600;
}

.btn-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 1rem;
}
.btn-group .el-button {
  flex: 1;
  padding: 10px 0;
  border-radius: 20px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e4e7ed;
}

.forgot-password {
  position: absolute;
  bottom: 20px;    /* 距离底部 20px */
  right: 20px;     /* 距离右侧 20px */
  margin: 0;       /* 清除可能的默认 margin */
}

.forgot-password a {
  font-size: 12px;
  color: #409eff;
  text-decoration: none;
  cursor: pointer;
}

.forgot-password a:hover {
  color: #66b1ff;
  text-decoration: underline;
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

.captcha-container {
  display: flex;
  width: 100%;
  gap: 8px;
}
.captcha-input {
  flex: 2;
}
.captcha-img-box {
  flex: 1;
  height: 50px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
}
.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.captcha-loading {
  font-size: 12px;
  color: #999;
}
.captcha-img-box:hover {
  border-color: #409eff;
}

/* ========== 新增：邮箱验证码行 ========== */
.email-captcha-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
.email-code-input {
  flex: 1;
}
.send-code-btn {
  width: auto;
  min-width: 120px;
  white-space: nowrap;
  padding: 0 12px;
}
</style>