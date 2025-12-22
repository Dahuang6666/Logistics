<template>
  <div class="form-container">
    <!-- 翻转容器：控制3D翻转 -->
    <div class="flip-box" :class="{ 'flipped': formType === 'forget' }">
      <!-- 正面：登录表单 -->
      <div class="form-panel login-panel">
        <h2>系统登录</h2>
        <div class="form-item">
          <label>账号</label>
          <el-input v-model="loginForm.username" placeholder="请输入账号/手机号"></el-input>
        </div>
        <div class="form-item">
          <label>密码</label>
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </div>
        <div class="form-item">
          <label>验证码</label>
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码"
            style="width: 60%;"
          ></el-input>
          <img
            :src="`https://picsum.photos/100/40?random=${loginCaptchaRandom}`"
            @click="refreshLoginCaptcha"
            class="captcha-img"
            alt="登录验证码"
          />
        </div>
        <el-button type="primary" class="submit-btn">登录</el-button>
        <div class="link-btn" @click="switchForm">忘记密码？点击找回</div>
      </div>

      <!-- 反面：找回密码表单 -->
      <div class="form-panel forget-panel">
        <h2>找回密码</h2>
        <div class="form-item">
          <label>手机号</label>
          <el-input v-model="forgetForm.phone" placeholder="请输入绑定的手机号"></el-input>
        </div>
        <div class="form-item">
          <label>验证码</label>
          <el-input
            v-model="forgetForm.captcha"
            placeholder="请输入验证码"
            style="width: 60%;"
          ></el-input>
          <img
            :src="`https://picsum.photos/100/40?random=${forgetCaptchaRandom}`"
            @click="refreshForgetCaptcha"
            class="captcha-img"
            alt="找回验证码"
          />
        </div>
        <div class="form-item">
          <label>新密码</label>
          <el-input v-model="forgetForm.newPwd" type="password" placeholder="请输入新密码"></el-input>
        </div>
        <el-button type="primary" class="submit-btn">重置密码</el-button>
        <div class="link-btn" @click="switchForm">返回登录</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FlipLoginForm',
  data() {
    return {
      formType: 'login', // 'login' 或 'forget'
      loginForm: { username: '', password: '', captcha: '' },
      forgetForm: { phone: '', captcha: '', newPwd: '' },
      loginCaptchaRandom: Math.random(),
      forgetCaptchaRandom: Math.random()
    }
  },
  methods: {
    switchForm() {
      this.formType = this.formType === 'login' ? 'forget' : 'login';
    },
    refreshLoginCaptcha() {
      this.loginCaptchaRandom = Math.random();
    },
    refreshForgetCaptcha() {
      this.forgetCaptchaRandom = Math.random();
    }
  }
}
</script>

<style scoped>
/* 页面整体居中 */
.form-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f5f5;
  margin: 0;
  padding: 0;
}

/* 3D 翻转容器 */
.flip-box {
  width: 400px;
  height: 400px;
  perspective: 1200px; /* 必须设置 perspective 才能有 3D 效果 */
  position: relative;
}

/* 表单面板通用样式 */
.form-panel {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  backface-visibility: hidden; /* 关键：隐藏背面 */
  transform-style: preserve-3d; /* 关键：保留子元素 3D 层级 */
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  padding: 40px 30px;
  box-sizing: border-box;
}

/* 默认状态：登录面板正面朝前 */
.login-panel {
  transform: rotateY(0deg);
  pointer-events: auto;
}
.forget-panel {
  transform: rotateY(180deg);
  pointer-events: none; /* 背面不可交互 */
}

/* 翻转后：找回密码面板正面朝前 */
.flip-box.flipped .login-panel {
  transform: rotateY(180deg);
  pointer-events: none;
}
.flip-box.flipped .forget-panel {
  transform: rotateY(0deg);
  pointer-events: auto;
}

/* 标题 */
h2 {
  text-align: center;
  font-size: 22px;
  color: #333;
  margin: 0 0 30px 0;
  font-weight: 600;
}

/* 表单项布局 */
.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.form-item label {
  width: 80px;
  font-size: 14px;
  color: #666;
  text-align: right;
  margin-right: 12px;
}
.form-item .el-input {
  flex: 1;
}

/* 验证码图片 */
.captcha-img {
  width: 100px;
  height: 40px;
  cursor: pointer;
  margin-left: 10px;
  vertical-align: middle;
  border-radius: 4px;
  user-select: none;
}

/* 按钮样式 */
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}
.link-btn {
  text-align: center;
  color: #1890ff;
  font-size: 14px;
  cursor: pointer;
  margin-top: 16px;
}
</style>