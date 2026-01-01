<template>
  <div class="login-container">
    <!-- 装饰圆圈 -->
    <div class="circle"></div>
    <div class="circle"></div>
    <div class="circle"></div>
    <div class="circle"></div>

    <!-- 波浪元素 -->
    <div class="wave"></div>
    <div class="wave"></div>

    <!-- 自定义邮箱确认弹窗 -->
    <transition name="modal-fade">
      <div v-if="showEmailModal" class="modal-overlay" @click="closeEmailModal">
        <div class="modal-box" @click.stop>
          <div class="modal-header">
            <div class="modal-icon">📧</div>
            <h3>确认邮箱</h3>
          </div>
          <div class="modal-content">
            <p>系统将向以下邮箱发送验证码：</p>
            <div class="email-display">{{ currentEmail }}</div>
          </div>
          <div class="modal-footer">
            <button class="modal-btn cancel-btn" @click="closeEmailModal">
              不是这个邮箱
            </button>
            <button class="modal-btn confirm-btn" @click="confirmSendEmail">
              确定发送
            </button>
          </div>
        </div>
      </div>
    </transition>

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
          <div class="form-header">
            <div class="logo-icon">🏠</div>
            <h2>宿舍管理系统</h2>
            <p class="subtitle">Dormitory Management System</p>
          </div>

          <el-form-item>
            <el-input
              v-model="loginData.userNo"
              placeholder="请输入学号或工号"
              clearable
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <el-input
              v-model="loginData.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
              size="large"
            />
          </el-form-item>

          <el-form-item>
            <div class="captcha-container">
              <el-input
                v-model="loginData.captchaInput"
                placeholder="请输入验证码"
                clearable
                class="captcha-input"
                size="large"
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
            <el-radio v-model="loginData.role" :label="1">👤 用户</el-radio>
            <el-radio v-model="loginData.role" :label="4">👨‍💼 管理员</el-radio>
          </div>

          <el-form-item class="btn-group">
            <el-button type="default" @click="handleRegister" size="large">注册</el-button>
            <el-button type="primary" native-type="submit" size="large">登录</el-button>
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
          <div class="form-header">
            <div class="logo-icon">🔑</div>
            <h2>找回密码</h2>
            <p class="subtitle">Reset Password</p>
          </div>

          <el-form-item>
            <el-input
              v-model="forgetData.userNo"
              placeholder="请输入学号或工号"
              clearable
              size="large"
            />
          </el-form-item>

          <!-- 邮箱验证码行 -->
          <el-form-item>
            <div class="email-captcha-row">
              <el-input
                v-model="forgetData.emailCode"
                placeholder="邮箱验证码"
                clearable
                class="email-code-input"
                size="large"
              />
              <el-button
                type="primary"
                @click="handleGetEmailCode"
                :loading="sending"
                :disabled="countdown > 0"
                class="send-code-btn"
                size="large"
              >
                {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
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
              size="large"
            />
          </el-form-item>

          <el-form-item class="btn-group">
            <el-button type="default" @click="flipToLogin" size="large">返回登录</el-button>
            <el-button type="primary" native-type="submit" size="large">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { login, getCaptcha, verifyCaptcha, updatePassword, verifyEmail, getUserName } from '@/utils/api.js'
import { getEmail, sendEmail } from '@/utils/api.js'

export default {
  name: 'LoginView',
  data() {
    return {
      isFlipped: false,
      loginData: {
        userNo: '',
        password: '',
        role: 0,
        captchaInput: '',
        captchaId: ''
      },
      captchaImage: '',
      forgetData: {
        userNo: '',
        emailCode: '',
        newPassword: ''
      },
      sending: false,
      countdown: 0,
      countdownTimer: null,
      showEmailModal: false,
      currentEmail: ''
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

            const userName = await getUserName(this.loginData.userNo)
            // 保存用户信息到 localStorage
            localStorage.setItem('token',this.loginData.userNo)
            localStorage.setItem('userName', userName.data.data)
            localStorage.setItem('userRole', this.loginData.role)

            // 根据角色跳转
            if (this.loginData.role === 1) {
              // 学生用户跳转到学生端
              this.$router.push('/student')
            } else if (this.loginData.role === 4) {
              // 管理员跳转到管理端
              this.$router.push('/admin')
            }
          }
          else{
            ElMessage.error(response.data.msg)
            await this.refreshCaptcha()
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
    flipToForget() {
      this.isFlipped = true
    },
    flipToLogin() {
      this.isFlipped = false
    },
    async handleGetEmailCode() {
      if (this.countdown > 0) return;

      const { userNo } = this.forgetData;
      if (!userNo?.trim()) {
        ElMessage.warning('请先输入学号或工号');
        return;
      }

      try {
        const emailRes = await getEmail(userNo);
        const email = emailRes.data.data;
        if(emailRes.data.code===0)
        {
          ElMessage.error(emailRes.data.msg)
          return
        }

        // 显示自定义弹窗
        this.currentEmail = email;
        this.showEmailModal = true;

      } catch (err) {
        ElMessage.error('操作失败，请重试');
        console.error(err);
      }
    },
    closeEmailModal() {
      this.showEmailModal = false;
      ElMessage.info('请联系管理员更新您的邮箱信息');
    },
    async confirmSendEmail() {
      this.showEmailModal = false;
      this.startCountdown();
      ElMessage.success('验证码已发送，请查收邮箱');

      try {
        await sendEmail(this.forgetData.userNo);
      } catch (err) {
        console.warn('邮件发送异常（已忽略）:', err);
      }
    },
    startCountdown() {
      this.countdown = 30;
      if (this.countdownTimer) clearInterval(this.countdownTimer);
      this.countdownTimer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer);
          this.countdownTimer = null;
        }
      }, 1000);
    },
    async handleConfirmReset() {
      const { userNo, emailCode, newPassword } = this.forgetData;

      if (!userNo?.trim()) {
        ElMessage.warning('请输入学号或工号');
        return;
      }
      if (!emailCode?.trim()) {
        ElMessage.warning('请输入邮箱验证码');
        return;
      }
      if (!newPassword?.trim()) {
        ElMessage.warning('请输入新密码');
        return;
      }

      try {
        const verifyRes = await verifyEmail(userNo, emailCode);
        if (verifyRes.data.code !== 1) {
          ElMessage.error(verifyRes.data.msg);
          return;
        }

        const updateRes = await updatePassword(userNo, newPassword);
        if (updateRes.data.code === 1) {
          ElMessage.success('密码重置成功！');
          this.forgetData = { userNo: '', emailCode: '', newPassword: '' };
          this.flipToLogin();
        } else {
          ElMessage.error(updateRes.data.msg );
        }
      } catch (error) {
        console.error('重置密码出错:', error);
        ElMessage.error('网络错误，请稍后重试');
      }
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

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 25%, #90caf9 50%, #64b5f6 75%, #42a5f5 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  overflow: hidden;
  position: relative;
  padding: 20px;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 漂浮装饰圆 */
.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  animation: float 8s infinite ease-in-out;
}

.circle:nth-child(1) {
  width: 100px;
  height: 100px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 60%;
  left: 75%;
  animation-delay: 2s;
}

.circle:nth-child(3) {
  width: 80px;
  height: 80px;
  top: 30%;
  left: 80%;
  animation-delay: 4s;
}

.circle:nth-child(4) {
  width: 120px;
  height: 120px;
  top: 75%;
  left: 15%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) translateX(0) scale(1);
    opacity: 0.3;
  }
  25% {
    transform: translateY(-40px) translateX(30px) scale(1.1);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-60px) translateX(-20px) scale(0.9);
    opacity: 0.4;
  }
  75% {
    transform: translateY(-30px) translateX(-40px) scale(1.05);
    opacity: 0.6;
  }
}

/* 波浪动画 */
.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 200px;
  background: linear-gradient(to right,
  rgba(255, 255, 255, 0.1) 0%,
  rgba(255, 255, 255, 0.2) 50%,
  rgba(255, 255, 255, 0.1) 100%);
  animation: wave 10s linear infinite;
}

.wave:nth-child(5) {
  bottom: -50px;
  opacity: 0.5;
  animation-delay: 0s;
  animation-duration: 8s;
}

.wave:nth-child(6) {
  bottom: -30px;
  opacity: 0.3;
  animation-delay: -3s;
  animation-duration: 12s;
}

@keyframes wave {
  0% { transform: translateX(0) translateY(0); }
  50% { transform: translateX(-25%) translateY(-20px); }
  100% { transform: translateX(-50%) translateY(0); }
}

/* 3D 翻转*/
.flip-box {
  width: 100%;
  max-width: 450px;
  perspective: 1200px;
  position: relative;
  min-height: 580px;
  transform-style: preserve-3d;
  z-index: 10;
}

.form-panel {
  position: absolute;
  width: 100%;
  top: 0;
  left: 0;
  backface-visibility: hidden;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(66, 165, 245, 0.3);
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
  transform: rotateY(-180deg);
  pointer-events: none;
}
.flip-box.flipped .forget-panel {
  transform: rotateY(0deg);
  pointer-events: auto;
}

/* ========== 表单通用 ========== */
.login-form {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 40px 35px;
  border-radius: 20px;
  width: 100%;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 50px;
  margin-bottom: 10px;
}

.login-form h2 {
  color: #1976d2;
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 5px;
}

.subtitle {
  color: #64b5f6;
  font-size: 13px;
  margin-top: 5px;
}

.btn-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 1.5rem;
}

.btn-group :deep(.el-button) {
  flex: 1;
  border-radius: 10px;
  font-weight: 600;
}

.btn-group :deep(.el-button--primary) {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  border: none;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

.btn-group :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
}

.btn-group :deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(25, 118, 210, 0.2);
  color: #1976d2;
}

.btn-group :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.9);
  border-color: #42a5f5;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(25, 118, 210, 0.2);
  box-shadow: none;
  border-radius: 10px;
  padding: 8px 15px;
}

:deep(.el-input__wrapper:hover) {
  border-color: #42a5f5;
}

:deep(.el-input__wrapper.is-focus) {
  background: rgba(255, 255, 255, 0.7);
  border-color: #42a5f5;
  box-shadow: 0 0 15px rgba(66, 165, 245, 0.3);
}

:deep(.el-input__inner) {
  color: #1976d2;
}

:deep(.el-input__inner::placeholder) {
  color: rgba(25, 118, 210, 0.4);
}

.forgot-password {
  text-align: right;
  margin-top: 15px;
}

.forgot-password a {
  font-size: 13px;
  color: #1976d2;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.3s;
}

.forgot-password a:hover {
  color: #42a5f5;
  text-decoration: underline;
}

.role-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 60px;
  margin: 20px 0;
}

:deep(.el-radio) {
  color: #1976d2;
  font-weight: 500;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #42a5f5;
  border-color: #42a5f5;
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #1976d2;
}

.captcha-container {
  display: flex;
  width: 100%;
  gap: 10px;
}

.captcha-input {
  flex: 2;
}

.captcha-img-box {
  flex: 1;
  height: 50px;
  border: 1px solid rgba(25, 118, 210, 0.2);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.captcha-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-loading {
  font-size: 12px;
  color: #1976d2;
}

.captcha-img-box:hover {
  border-color: #42a5f5;
  box-shadow: 0 0 10px rgba(66, 165, 245, 0.3);
}

/* 邮箱验证码 */
.email-captcha-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.email-code-input {
  flex: 1;
}

.send-code-btn {
  min-width: 120px;
  white-space: nowrap;
  border-radius: 10px;
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  border: none;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

.send-code-btn:hover {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
}

.send-code-btn:disabled {
  background: #95a5a6;
  box-shadow: none;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-form {
    padding: 30px 25px;
  }

  .logo-icon {
    font-size: 40px;
  }

  .login-form h2 {
    font-size: 22px;
  }
}

/* 邮箱确认弹窗  */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(66, 165, 245, 0.3);
  box-shadow: 0 8px 32px rgba(66, 165, 245, 0.4);
  width: 90%;
  max-width: 420px;
  padding: 30px;
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  text-align: center;
  margin-bottom: 20px;
}

.modal-icon {
  font-size: 50px;
  margin-bottom: 10px;
}

.modal-header h3 {
  color: #1976d2;
  font-size: 22px;
  font-weight: 600;
  margin: 0;
}

.modal-content {
  margin-bottom: 25px;
}

.modal-content p {
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.email-display {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  border: 2px solid #42a5f5;
  border-radius: 12px;
  padding: 15px 20px;
  color: #1976d2;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  word-break: break-all;
}

.modal-footer {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: rgba(149, 165, 166, 0.2);
  color: #2c3e50;
  border: 1px solid rgba(149, 165, 166, 0.3);
}

.cancel-btn:hover {
  background: rgba(149, 165, 166, 0.3);
  border-color: #95a5a6;
}

.confirm-btn {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  color: white;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
  transform: translateY(-2px);
}

/* 弹窗动画 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-box {
  animation: modalSlideIn 0.3s ease;
}

.modal-fade-leave-active .modal-box {
  animation: modalSlideOut 0.3s ease;
}

@keyframes modalSlideOut {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-30px) scale(0.95);
  }
}
</style>