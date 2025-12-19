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

export default {
  name: 'LoginView',
  data() {
    return {
      loginData: {
        userNo: '',
        password: '',
        role: 0
      }
    }
  },
  methods: {
    async handleLogin() {
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
      try {
        const response = await login(this.loginData)
        console.log(response.data)
        ElMessage.success('登录成功！')
        console.log('登录用户:', this.loginData.userNo)
      } catch (error) {
        ElMessage.error('登录失败，请检查账号密码或网络')
        console.error('登录失败:', error)
      }
    },
    async handleRegister(){
      this.$router.push({ name: 'register' })
    },
    handleForgotPassword(){

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
</style>