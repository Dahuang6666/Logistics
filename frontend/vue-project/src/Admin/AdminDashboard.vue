<template>
  <div class="dashboard-container">
    <div class="header">
      <div class="logo">🏠 宿舍管理系统</div>
      <div class="user-info">
        <span class="user-name">👤 {{ userName }} | 系统管理员</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </div>

    <div class="sidebar">
      <div class="menu-section">
        <div class="menu-title">管理主菜单</div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/user' }"
          @click="navigateTo('/admin/user')"
        >
          👥 用户管理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/announcement' }"
          @click="navigateTo('/admin/announcement')"
        >
          📢 公告管理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/dormitory' }"
          @click="navigateTo('/admin/dormitory')"
        >
          🏢 宿舍管理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/repair' }"
          @click="navigateTo('/admin/repair')"
        >
          🔧 报修管理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/dorm-change' }"
          @click="navigateTo('/admin/dorm-change')"
        >
          🔄 宿舍变更管理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/feedback' }"
          @click="navigateTo('/admin/feedback')"
        >
          💬 投诉建议处理
        </div>

        <div
          class="menu-item"
          :class="{ active: currentRoute === '/admin/statistics' }"
          @click="navigateTo('/admin/statistics')"
        >
          📊 数据统计与分析
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="content-card">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>

        <div v-if="!$route.name" class="placeholder-box">
          <h2>欢迎进入管理员后台</h2>
          <p>请从左侧菜单选择要管理的模块</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      userName: sessionStorage.getItem('userName') || 'Admin',
      currentRoute: this.$route.path
    }
  },
  watch: {
    $route(to) {
      this.currentRoute = to.path
    }
  },
  methods: {
    // 路由导航逻辑
    navigateTo(path) {
      if (this.$route.path !== path) {
        this.$router.push(path)
      }
    },

    // 退出登录
    handleLogout() {
      if (confirm('确定要退出管理员系统吗？')) {
        sessionStorage.clear()
        this.$router.push('/')
        ElMessage.success('已退出登录')
      }
    }
  }
}
</script>

<style scoped>
/* 继承你提供的样式，确保风格一致 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.dashboard-container {
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
  background: #f5f5f5;
  height: 100vh;
  overflow: hidden;
}

/* 顶部导航栏 */
.header {
  height: 60px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-name {
  color: #2c3e50;
  font-size: 14px;
}

.logout-btn {
  padding: 6px 20px;
  background: #95a5a6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: #7f8c8d;
}

/* 左侧边栏 */
.sidebar {
  width: 240px;
  background: white;
  border-right: 1px solid #e0e0e0;
  position: fixed;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  padding: 20px 10px;
}

.menu-section {
  margin-bottom: 20px;
}

.menu-title {
  color: #7f8c8d;
  font-size: 12px;
  font-weight: bold;
  padding: 0 10px;
  margin-bottom: 10px;
}

.menu-item {
  padding: 12px 15px;
  color: #2c3e50;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.3s;
  font-size: 14px;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.active {
  background: #3498db;
  color: white;
}

/* 主内容区 */
.main-content {
  margin-left: 240px;
  margin-top: 60px;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.content-card {
  background: white;
  border-radius: 8px;
  padding: 30px;
  min-height: 100%;
}

.placeholder-box {
  text-align: center;
  padding-top: 100px;
  color: #bdc3c7;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-10px);
}
</style>