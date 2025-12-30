<template>
  <div class="dashboard-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="logo">🏠 宿舍管理系统</div>
      <div class="user-info">
        <span class="user-name">👤 {{ userName }} | 学生</span>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </div>
    </div>

    <!-- 左侧边栏 -->
    <div class="sidebar">
      <div class="menu-section">
        <div class="menu-title">学生功能</div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/announcement' }"
          @click="navigateTo('/student/announcement')"
        >
          📢 公告通知
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/repair' }"
          @click="navigateTo('/student/repair')"
        >
          🔧 报修申请
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/repair-progress' }"
          @click="navigateTo('/student/repair-progress')"
        >
          📋 报修进度
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/dorm-change' }"
          @click="navigateTo('/student/dorm-change')"
        >
          🔄 宿舍变更申请
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/change-progress' }"
          @click="navigateTo('/student/change-progress')"
        >
          📋 宿舍变更进度
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/dorm' }"
          @click="navigateTo('/student/dorm')"
        >
          🏢 宿舍信息
        </div>
        <div
          class="menu-item"
          :class="{ active: currentRoute === '/student/feedback' }"
          @click="navigateTo('/student/feedback')"
        >
          💬 投诉与建议
        </div>
      </div>
    </div>

    <!-- 主内容区 - 动态路由组件 -->
    <div class="main-content">
      <div class="content-card">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getUserName } from '@/utils/api.js'

export default {
  name: 'StudentDashboard',
  data() {
    return {
      userName: '学生',
      currentRoute: this.$route.path
    }
  },
  watch: {
    $route(to) {
      this.currentRoute = to.path
    }
  },
  async mounted() {
    await this.loadUserName()
  },
  methods: {
    // 加载用户名
    async loadUserName() {
      const userNo = localStorage.getItem('userName')
      if (userNo) {
        try {
          const response = await getUserName(userNo)
          if (response.data.code === 1) {
            this.userName = response.data.data || userNo
          } else {
            this.userName = userNo
          }
        } catch (error) {
          console.error('获取用户名失败:', error)
          this.userName = userNo
        }
      }
    },

    // 路由导航
    navigateTo(path) {
      if (this.$route.path !== path) {
        this.$router.push(path)
      }
    },

    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.clear()
        this.$router.push('/')
        ElMessage.success('已退出登录')
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