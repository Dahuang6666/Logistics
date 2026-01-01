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
          :class="{ active: currentMenu === 'announcement' }"
          @click="switchMenu('announcement')"
        >
          📢 公告通知
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'dorm' }"
          @click="switchMenu('dorm')"
        >
          🏢 宿舍信息
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'repair' }"
          @click="switchMenu('repair')"
        >
          🔧 报修申请
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'repairProgress' }"
          @click="switchMenu('repairProgress')"
        >
          📋 报修进度
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'dormChange' }"
          @click="switchMenu('dormChange')"
        >
          🔄 宿舍变更申请
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'changeProgress' }"
          @click="switchMenu('changeProgress')"
        >
          📊 宿舍变更进度
        </div>
        <div
          class="menu-item"
          :class="{ active: currentMenu === 'feedback' }"
          @click="switchMenu('feedback')"
        >
          💬 投诉与建议
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <div class="content-card">
        <!-- 公告通知页面 -->
        <div v-if="currentMenu === 'announcement'">
          <div class="page-header">
            <h1 class="page-title">公告通知</h1>
          </div>

          <div class="filter-bar">
            <input
              type="text"
              class="search-box"
              placeholder="🔍 搜索公告..."
              v-model="searchKeyword"
              @input="handleSearch"
            >
            <button
              class="filter-btn"
              :class="{ active: filterType === 'all' }"
              @click="handleFilter('all')"
            >
              全部
            </button>
            <button
              class="filter-btn"
              :class="{ active: filterType === '紧急通知' }"
              @click="handleFilter('紧急通知')"
            >
              紧急通知
            </button>
            <button
              class="filter-btn"
              :class="{ active: filterType === '安全提醒' }"
              @click="handleFilter('安全提醒')"
            >
              安全提醒
            </button>
            <button
              class="filter-btn"
              :class="{ active: filterType === '温馨提示' }"
              @click="handleFilter('温馨提示')"
            >
              温馨提示
            </button>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 公告列表 -->
          <div v-else-if="announcementList.length > 0" class="notice-list">
            <div
              v-for="item in announcementList"
              :key="item.id"
              class="notice-item"
            >
            <div class="notice-header">
                <span
                  class="notice-tag"
                  :class="getTagClass(item.announcementTypeName)"
                >
                  {{ item.announcementTypeName }}
                </span>
                <span class="notice-title">{{ item.title }}</span>
              </div>
              <div class="notice-content">{{ item.content }}</div>
              <div class="notice-meta">
                <span>
                  📅 {{ formatDate(item.publishTime) }} |
                  👤 {{ item.publisher }}
                </span>
                <button class="detail-btn" @click="viewDetail(item)">查看详情</button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-icon">📭</div>
            <p>暂无公告信息</p>
          </div>

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination">
            <div class="page-info">共 {{ total }} 条记录</div>
            <div class="page-buttons">
              <button
                class="page-btn"
                :disabled="currentPage === 1"
                @click="changePage(currentPage - 1)"
              >
                &lt;
              </button>
              <button
                v-for="page in totalPages"
                :key="page"
                class="page-btn"
                :class="{ active: currentPage === page }"
                @click="changePage(page)"
              >
                {{ page }}
              </button>
              <button
                class="page-btn"
                :disabled="currentPage === totalPages"
                @click="changePage(currentPage + 1)"
              >
                &gt;
              </button>
            </div>
          </div>
        </div>

        <!-- 其他菜单的占位内容 -->
        <div v-else class="placeholder-content">
          <div class="placeholder-icon">🚧</div>
          <h2>{{ getMenuTitle(currentMenu) }}</h2>
          <p>该功能正在开发中，敬请期待...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getAnnouncementList } from '@/utils/api.js'

export default {
  name: 'StudentDashboard',
  data() {
    return {
      userName: localStorage.getItem('userName') || '学生',
      currentMenu: 'announcement',
      searchKeyword: '',
      filterType: 'all',
      loading: false,
      announcementList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  mounted() {
    this.loadAnnouncementList()
  },
  methods: {
    // 加载公告列表
    async loadAnnouncementList() {
      this.loading = true
      try {
        const response = await getAnnouncementList({
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          priority: this.filterPriority === 'all' ? null : this.filterPriority
        })

        if (response.data.code === 1) {
          this.announcementList = response.data.data.list || []
          this.total = response.data.data.total || 0
        } else {
          ElMessage.error('获取公告列表失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    // 翻页
    changePage(page) {
      if (page < 1 || page > this.totalPages) return
      this.currentPage = page
      this.loadAnnouncementList()
    },

    // 查看详情
    viewDetail(item) {
      ElMessage.info(`查看公告详情: ${item.title}`)
    },

    // 退出登录
    handleLogout() {
      if (confirm('确定要退出登录吗？')) {
        localStorage.clear()
        this.$router.push('/')
        ElMessage.success('已退出登录')
      }
    },

    // 获取标签样式类名
    getTagClass(typeName) {
      const typeMap = {
        '紧急通知': 'tag-urgent',
        '安全提醒': 'tag-safety',
        '制度通知': 'tag-rule',
        '日常通知': 'tag-normal',
        '活动公告': 'tag-activity',
        '温馨提示': 'tag-tip'
      }
      return typeMap[typeName] || 'tag-default'
    },

    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },

    // 获取菜单标题
    getMenuTitle(menu) {
      const titles = {
        'dorm': '宿舍信息',
        'repair': '报修申请',
        'repairProgress': '报修进度',
        'dormChange': '宿舍变更申请',
        'changeProgress': '宿舍变更进度',
        'feedback': '投诉与建议'
      }
      return titles[menu] || ''
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

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

/* 搜索和筛选 */
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.search-box {
  flex: 1;
  min-width: 200px;
  max-width: 300px;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
}

.filter-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  color: #7f8c8d;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.filter-btn.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}

.filter-btn:hover {
  border-color: #3498db;
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 公告列表 */
.notice-list {
  margin-bottom: 20px;
}

.notice-item {
  background: white;
  border: 2px solid #ddd;
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.notice-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.notice-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.notice-tag {
  padding: 4px 12px;
  border-radius: 3px;
  color: white;
  font-size: 11px;
  font-weight: bold;
  white-space: nowrap;
}

.tag-urgent { background: #e74c3c; }
.tag-safety { background: #e67e22; }
.tag-rule { background: #3498db; }
.tag-normal { background: #3498db; }
.tag-activity { background: #f39c12; }
.tag-tip { background: #27ae60; }
.tag-default { background: #95a5a6; }

.notice-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  flex: 1;
}

.notice-content {
  color: #7f8c8d;
  font-size: 13px;
  margin-bottom: 10px;
  line-height: 1.6;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #7f8c8d;
  font-size: 12px;
}

.detail-btn {
  padding: 6px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.3s;
}

.detail-btn:hover {
  background: #2980b9;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.page-info {
  color: #7f8c8d;
  font-size: 13px;
}

.page-buttons {
  display: flex;
  gap: 8px;
}

.page-btn {
  min-width: 35px;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  background: white;
  color: #7f8c8d;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #3498db;
  color: #3498db;
}

.page-btn.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}

.page-btn:disabled {
  background: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

/* 占位内容 */
.placeholder-content {
  text-align: center;
  padding: 100px 20px;
  color: #7f8c8d;
}

.placeholder-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.placeholder-content h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}
</style>