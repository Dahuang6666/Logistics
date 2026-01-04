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

    <el-form
      ref="registerFormRef"
      :model="registerData"
      :rules="registerRules"
      class="login-form"
      label-width="0"
    >
      <div class="form-header">
        <div class="logo-icon">📝</div>
        <h2>学生注册</h2>
        <p class="subtitle">Student Registration</p>
      </div>

      <!-- 步骤指示器 -->
      <div class="step-indicator">
        <div class="step-item" :class="{ active: step >= 1, completed: step > 1 }">
          <span class="step-number">{{ step > 1 ? '✓' : '1' }}</span>
          <span class="step-text">基础信息</span>
        </div>
        <div class="step-line" :class="{ active: step >= 2 }"></div>
        <div class="step-item" :class="{ active: step >= 2 }">
          <span class="step-number">2</span>
          <span class="step-text">补充信息</span>
        </div>
      </div>

      <!-- 第一步：基础信息 -->
      <transition name="step-fade" mode="out-in">
        <div v-if="step === 1" key="step1" class="step-content">
          <el-form-item prop="username">
            <el-input
              v-model="registerData.username"
              placeholder="请输入姓名"
              clearable
              size="large"
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="userNo">
            <el-input
              v-model="registerData.userNo"
              placeholder="请输入学号"
              clearable
              size="large"
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerData.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              show-password
              clearable
              size="large"
              class="form-input"
            />
          </el-form-item>
          <el-form-item class="btn-group">
            <el-button
              type="default"
              native-type="button"
              @click="backLogin"
              class="form-btn"
              size="large"
            >
              返回登录
            </el-button>
            <el-button
              type="primary"
              native-type="button"
              @click="toStep2"
              class="form-btn next-btn"
              size="large"
              :loading="btnLoading"
            >
              下一步 →
            </el-button>
          </el-form-item>
        </div>
      </transition>

      <!-- 第二步：补充信息 -->
      <transition name="step-fade" mode="out-in">
        <div v-if="step === 2" key="step2" class="step-content">
          <el-form-item prop="phone">
            <el-input
              v-model="registerData.phone"
              placeholder="请输入手机号"
              clearable
              size="large"
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="registerData.email"
              placeholder="请输入邮箱"
              clearable
              size="large"
              class="form-input"
            />
          </el-form-item>

          <!-- 性别选择 -->
          <el-form-item prop="gender">
            <el-radio-group v-model="registerData.gender" class="radio-group">
              <el-radio label="男" class="gender-radio">👨 男</el-radio>
              <el-radio label="女" class="gender-radio">👩 女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item class="btn-group">
            <el-button
              type="default"
              native-type="button"
              @click="toStep1"
              class="form-btn prev-btn"
              size="large"
            >
              ← 上一步
            </el-button>
            <el-button
              type="primary"
              native-type="button"
              @click="handleRegister"
              class="form-btn submit-btn"
              size="large"
              :loading="btnLoading"
            >
              提交注册 ✓
            </el-button>
          </el-form-item>
        </div>
      </transition>
    </el-form>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { register } from '@/utils/api.js'

export default {
  name: 'RegisterView',
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }

    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!/^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }

    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    const validateUserNo = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入学号'))
      } else {
        callback()
      }
    }

    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入姓名'))
      } else {
        callback()
      }
    }

    const validateGender = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择性别'))
      } else {
        callback()
      }
    }

    return {
      step: 1,
      btnLoading: false,
      registerData: {
        userNo: '',
        password: '',
        username: '',
        role: 1,
        phone: '',
        email: '',
        gender: ''
      },
      registerRules: {
        userNo: [{ validator: validateUserNo, trigger: 'blur' }],
        password: [{ validator: validatePassword, trigger: 'blur' }],
        username: [{ validator: validateUsername, trigger: 'blur' }],
        phone: [{ validator: validatePhone, trigger: 'blur' }],
        email: [{ validator: validateEmail, trigger: 'blur' }],
        gender: [{ validator: validateGender, trigger: 'change' }]
      }
    }
  },
  methods: {
    async toStep2() {
      try {
        this.btnLoading = true
        const step1Fields = ['userNo', 'password', 'username']
        const valid = await this.$refs.registerFormRef.validate((prop) => step1Fields.includes(prop))

        if (valid) {
          setTimeout(() => {
            this.step = 2
            this.btnLoading = false
            const loginForm = document.querySelector('.login-form')
            if (loginForm) {
              loginForm.scrollTo({
                top: 0,
                behavior: 'smooth'
              })
            }
          }, 200)
        } else {
          this.btnLoading = false
          ElMessage.warning('请完善并检查基础信息后再继续')
        }
      } catch (error) {
        this.btnLoading = false
        ElMessage.error('表单校验失败，请检查输入内容')
      }
    },
    toStep1() {
      this.btnLoading = true
      setTimeout(() => {
        this.step = 1
        this.btnLoading = false
        const loginForm = document.querySelector('.login-form')
        if (loginForm) {
          loginForm.scrollTo({
            top: 0,
            behavior: 'smooth'
          })
        }
      }, 200)
    },
    backLogin() {
      this.$router.push('/')
    },
    async handleRegister() {
      try {
        this.btnLoading = true

        const isValid = await new Promise((resolve) => {
          this.$refs.registerFormRef.validate((valid) => {
            resolve(valid)
          })
        })

        if (!isValid) {
          this.btnLoading = false
          ElMessage.warning('请完善所有必填信息并确保格式正确')
          return
        }

        ElMessage.info('正在提交注册信息，请稍候...')

        const response = await register(this.registerData)

        if (response.data.code === 1) {
          ElMessage.success('注册成功！即将跳转到登录页')
          setTimeout(() => {
            this.$router.push('/')
            this.btnLoading = false
          }, 1000)
        } else if (response.data.msg === "账号已注册") {
          ElMessage.error('注册失败！该学号已被注册，请更换学号或直接登录')
          this.btnLoading = false
        } else {
          ElMessage.error(`注册失败：${response.data.msg || '未知错误'}`)
          this.btnLoading = false
        }

      } catch (error) {
        this.btnLoading = false
        console.error('注册接口调用失败:', error)
        if (error.message.includes('Network Error')) {
          ElMessage.error('注册失败：网络连接异常，请检查网络后重试')
        } else {
          ElMessage.error(`注册失败：${error.message || '服务器异常，请稍后再试'}`)
        }
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

.login-form {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 40px 35px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(66, 165, 245, 0.3);
  width: 100%;
  max-width: 460px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  position: relative;
  min-height: 580px;
  display: flex;
  flex-direction: column;
  z-index: 10;
}

.form-header {
  text-align: center;
  margin-bottom: 25px;
  flex-shrink: 0;
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

/* 步骤指示器 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 25px;
  gap: 8px;
  flex-shrink: 0;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.step-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  color: #64b5f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 2px solid rgba(100, 181, 246, 0.3);
}

.step-text {
  font-size: 12px;
  color: #64b5f6;
  transition: all 0.3s ease;
  font-weight: 500;
}

.step-line {
  width: 50px;
  height: 2px;
  background: rgba(100, 181, 246, 0.3);
  transition: all 0.3s ease;
}

.step-item.active .step-number {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  color: white;
  transform: scale(1.05);
  border-color: #1976d2;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.step-item.active .step-text {
  color: #1976d2;
  font-weight: 600;
}

.step-item.completed .step-number {
  background: #1976d2;
  color: white;
  border-color: #1976d2;
}

.step-line.active {
  background: linear-gradient(to right, #42a5f5, #1976d2);
}

/* 步骤切换动画 */
.step-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.step-fade-enter-active {
  transition: all 0.3s ease-out;
}

.step-fade-leave-active {
  transition: all 0.2s ease-in;
  position: absolute;
  width: calc(100% - 70px);
}

.step-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.step-content {
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(25, 118, 210, 0.2);
  box-shadow: none;
  border-radius: 10px;
  padding: 8px 15px;
  transition: all 0.3s;
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

/* 性别选择 */
.radio-group {
  display: flex;
  gap: 15px;
  width: 100%;
  justify-content: center;
}

.gender-radio {
  flex: 1;
}

:deep(.gender-radio.el-radio) {
  margin: 0;
  border: none !important;
  background: none !important;
  padding: 0 !important;
  box-shadow: none !important;
}

:deep(.gender-radio.el-radio.is-bordered) {
  border: none !important;
}

:deep(.gender-radio .el-radio__input) {
  display: none;
}

:deep(.gender-radio .el-radio__label) {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.5);
  border: 2px solid rgba(25, 118, 210, 0.2);
  border-radius: 10px;
  color: #1976d2;
  font-weight: 500;
  transition: all 0.3s;
  cursor: pointer;
  display: block;
  text-align: center;
}

:deep(.gender-radio .el-radio__label:hover) {
  background: rgba(255, 255, 255, 0.7);
  border-color: #42a5f5;
}

:deep(.gender-radio.is-checked .el-radio__label) {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  border-color: #1976d2;
  color: white;
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

/* 按钮组 */
.btn-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: auto;
  padding-top: 20px;
  flex-shrink: 0;
}

.form-btn {
  flex: 1;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s;
}

:deep(.form-btn.el-button--default) {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(25, 118, 210, 0.2);
  color: #1976d2;
}

:deep(.form-btn.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.9);
  border-color: #42a5f5;
  transform: translateY(-2px);
}

:deep(.form-btn.el-button--primary) {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  border: none;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

:deep(.form-btn.el-button--primary:hover) {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
  transform: translateY(-2px);
}

:deep(.el-form-item__error) {
  padding-top: 4px;
  font-size: 12px;
  color: #f56c6c;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 4px;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-form {
    padding: 30px 25px;
    max-width: 100%;
  }

  .logo-icon {
    font-size: 40px;
  }

  .login-form h2 {
    font-size: 22px;
  }
}
</style>