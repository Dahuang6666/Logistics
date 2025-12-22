<template>
  <div class="login-container">
    <el-form
      ref="registerFormRef"
      :model="registerData"
      :rules="registerRules"
      class="login-form"
      label-width="0"
    >
      <h2 class="title">用户注册</h2>

      <!-- 步骤指示器 -->
      <div class="step-indicator">
        <div class="step-item" :class="{ active: step >= 1, completed: step > 1 }">
          <span class="step-number">{{ step >= 1 ? '✓' : '1' }}</span>
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
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="userNo">
            <el-input
              v-model="registerData.userNo"
              placeholder="请输入学号"
              clearable
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerData.password"
              type="password"
              placeholder="请输入密码"
              show-password
              clearable
              class="form-input"
            />
          </el-form-item>
          <el-form-item class="btn-group">
            <el-button
              type="default"
              native-type="button"
              @click="backLogin"
              class="form-btn"
            >
              返回登录
            </el-button>
            <el-button
              type="primary"
              native-type="button"
              @click="toStep2"
              class="form-btn next-btn"
              :loading="btnLoading"
            >
              下一步
              <i class="el-icon-arrow-right btn-icon"></i>
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
              class="form-input"
            />
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="registerData.email"
              placeholder="请输入邮箱"
              clearable
              class="form-input"
            />
          </el-form-item>

          <!-- 优化性别选择方式 - 单选按钮组 -->
          <el-form-item prop="gender" class="gender-radio-group">
            <el-radio-group v-model="registerData.gender" class="radio-group">
              <el-radio label="男" border class="gender-radio" />
              <el-radio label="女" border class="gender-radio" />
            </el-radio-group>
          </el-form-item>

          <el-form-item class="btn-group">
            <el-button type="default" native-type="button" @click="toStep1" class="form-btn prev-btn">
              <i class="el-icon-arrow-left btn-icon"></i>
              上一步
            </el-button>
            <el-button type="primary" native-type="button" @click="handleRegister" class="form-btn submit-btn" :loading="btnLoading">
              提交注册
              <i class="el-icon-check btn-icon"></i>
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
    // 手机号校验规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }

    // 邮箱校验规则
    const validateEmail = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入邮箱'))
      } else if (!/^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }

    // 密码校验规则
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        callback()
      }
    }

    // 学号校验规则
    const validateUserNo = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入学号'))
      } else {
        callback()
      }
    }

    // 姓名校验规则
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入姓名'))
      } else {
        callback()
      }
    }

    // 性别校验规则
    const validateGender = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择性别'))
      } else {
        callback()
      }
    }

    return {
      step: 1,
      btnLoading: false, // 按钮加载状态
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
      // 先校验第一步字段
      try {
        this.btnLoading = true
        // 只校验第一步的字段
        const step1Fields = ['userNo', 'password', 'username']
        const valid = await this.$refs.registerFormRef.validate((prop) => step1Fields.includes(prop))

        if (valid) {
          // 添加延迟让用户看到加载效果
          setTimeout(() => {
            this.step = 2
            this.btnLoading = false
            // 滚动到顶部（平滑滚动）
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
          // 表单校验失败，提示用户完善信息
          ElMessage.warning('请完善并检查基础信息后再继续')
        }
      } catch (error) {
        this.btnLoading = false
        ElMessage.error('表单校验失败，请检查输入内容')
      }
    },
    toStep1() {
      // 上一步动画效果
      this.btnLoading = true
      setTimeout(() => {
        this.step = 1
        this.btnLoading = false
        // 平滑滚动到顶部
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

        // 第一步：校验所有表单字段（仅在校验通过后才会继续）
        const isValid = await new Promise((resolve) => {
          this.$refs.registerFormRef.validate((valid) => {
            resolve(valid)
          })
        })

        // 如果表单校验不通过，直接返回并提示
        if (!isValid) {
          this.btnLoading = false
          ElMessage.warning('请完善所有必填信息并确保格式正确')
          return
        }

        // 第二步：表单校验通过后，才调用注册接口
        ElMessage.info('正在提交注册信息，请稍候...')

        // 调用注册接口（真实接口）
        const response = await register(this.registerData)
        // console.log('注册接口返回:', response.data)
1
        // 第三步：根据接口返回结果提示不同信息
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
        // 捕获接口调用异常
        this.btnLoading = false
        console.error('注册接口调用失败:', error)
        // 区分网络错误和其他错误
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
  background-color: #f5f7fa;
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
  position: relative;
  overflow: hidden;
  /* 固定表单高度，防止切换时拉伸 */
  min-height: 450px;
  display: flex;
  flex-direction: column;
  /* 增加内边距，防止提示被遮挡 */
  padding-bottom: 3rem;
}

/* 标题样式 */
.title {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #222;
  font-weight: 600;
  position: relative;
  flex-shrink: 0;
}

/* 步骤指示器样式 */
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  gap: 8px;
  flex-shrink: 0;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  position: relative;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e4e7ed;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.step-text {
  font-size: 12px;
  color: #909399;
  transition: all 0.3s ease;
}

.step-line {
  width: 40px;
  height: 2px;
  background: #e4e7ed;
  transition: all 0.3s ease;
}

/* 激活状态 */
.step-item.active .step-number {
  background: #4cae60;
  color: white;
  transform: scale(1.05);
}

.step-item.active .step-text {
  color: #4cae60;
  font-weight: 500;
}

.step-item.completed .step-number {
  background: #3da052;
  color: white;
}

.step-line.active {
  background: #4cae60;
}

/* 步骤切换动画 - 优化过渡效果 */
.step-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.step-fade-enter-active {
  transition: all 0.25s ease-out;
}

.step-fade-leave-active {
  transition: all 0.15s ease-in;
  /* 离开时隐藏，防止布局抖动 */
  position: absolute;
  width: calc(100% - 4rem);
}

.step-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.step-content {
  width: 100%;
  /* 让内容区域自适应，防止拉伸 */
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  /* 增加内容间距，防止提示遮挡 */
  gap: 8px;
}

/* 输入框统一样式 */
.form-input {
  width: 100%;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

/* 性别单选组样式 */
.gender-radio-group {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 6px;
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.gender-radio-group:focus-within {
  border-color: #4cae60;
  box-shadow: 0 0 0 2px rgba(76, 174, 96, 0.1);
}

.radio-group {
  display: flex;
  gap: 20px;
  width: 100%;
  justify-content: center;
}

.gender-radio {
  width: auto;
  flex: 1;
  max-width: 80px;
  text-align: center;
}

:deep(.gender-radio .el-radio__label) {
  font-size: 14px;
}

/* 按钮组样式 */
.btn-group {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 20px;
  flex-shrink: 0;
  /* 按钮组固定在底部，增加间距防止遮挡 */
  margin-top: auto;
  padding-top: 16px;
}

.form-btn {
  flex: 1;
  padding: 10px 0;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* 按钮点击波纹效果 */
.form-btn::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.6s ease, height 0.6s ease;
}

.form-btn:active::after {
  width: 200px;
  height: 200px;
  opacity: 0;
}

/* 默认按钮样式 */
.form-btn.el-button--default {
  border: 1px solid #a8d8b9;
  background: #f0fcf5;
  color: #4cae60;
}

.form-btn.el-button--default:hover {
  background: #e0f8e8;
  border-color: #86c997;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(76, 174, 96, 0.1);
}

/* 主要按钮样式 */
.form-btn.el-button--primary {
  background: #4cae60;
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(76, 174, 96, 0.2);
}

.form-btn.el-button--primary:hover {
  background: #3da052;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(76, 174, 96, 0.3);
}

/* 按钮图标样式 */
.btn-icon {
  margin-left: 4px;
  transition: all 0.3s ease;
}

.next-btn:hover .btn-icon {
  transform: translateX(3px);
}

.prev-btn:hover .btn-icon {
  transform: translateX(-3px);
}

.submit-btn:hover .btn-icon {
  transform: scale(1.1);
}

/* 输入框样式优化 */
:deep(.form-input .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  transition: all 0.3s ease;
  height: 40px;
}

:deep(.form-input .el-input__wrapper:focus-within) {
  border-color: #4cae60;
  box-shadow: 0 0 0 2px rgba(76, 174, 96, 0.1);
}

/* 加载状态动画优化 */
:deep(.el-button--loading .el-icon-loading) {
  margin-right: 6px;
}

/* 显示表单内错误提示（仅作为辅助，主要用ElMessage） */
:deep(.el-form-item__error) {
  padding-top: 4px;
  padding-bottom: 8px;
  font-size: 12px;
  color: #f56c6c;
}

/* 调整Message提示位置，防止被遮挡 */
:deep(.el-message) {
  z-index: 9999 !important;
  top: 20px !important;
}
</style>