<template>
  <div class="announcement-view">
    <div class="page-header">
      <h1 class="page-title">公告通知</h1>
    </div>

    <div class="filter-bar">
      <div class="search-wrapper">
        <input
          type="text"
          class="search-box"
          placeholder="搜索公告..."
          v-model="searchKeyword"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">
          🔍
        </button>
      </div>

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
    <div v-else-if="filteredList.length > 0" class="notice-list">
      <div
        v-for="item in filteredList"
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
        <div class="notice-content">{{ getContentPreview(item.content) }}</div>
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
      <div class="page-left">
        <div class="page-info">共 {{ total }} 条记录</div>
        <div class="page-size-selector">
          <span>每页</span>
          <select v-model="pageSize" @change="handlePageSizeChange" class="size-select">
            <option :value="5">5 条</option>
            <option :value="10">10 条</option>
            <option :value="20">20 条</option>
            <option :value="50">50 条</option>
          </select>
        </div>
      </div>
      <div class="page-buttons">
        <button
          class="page-btn"
          :disabled="currentPage === 1"
          @click="changePage(currentPage - 1)"
        >
          &lt;
        </button>
        <button
          v-for="page in displayPages"
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

    <!-- 公告详情弹窗 -->
    <transition name="modal-fade">
      <div v-if="showDetailModal" class="modal-overlay" @click="closeDetail">
        <div class="modal-box" @click.stop>
          <div class="modal-header">
            <span
              class="modal-tag"
              :class="getTagClass(currentDetail.announcementTypeName)"
            >
              {{ currentDetail.announcementTypeName }}
            </span>
            <button class="close-btn" @click="closeDetail">✕</button>
          </div>
          <div class="modal-content">
            <h2 class="modal-title">{{ currentDetail.title }}</h2>
            <div class="modal-meta">
              <span>📅 {{ formatDate(currentDetail.publishTime) }}</span>
              <span>👤 {{ currentDetail.publisher }}</span>
            </div>
            <div class="modal-divider"></div>
            <div class="modal-text">{{ currentDetail.content }}</div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getAnnouncementList } from '@/utils/api.js'

export default {
  name: 'AnnouncementView',
  data() {
    return {
      searchKeyword: '',
      filterType: 'all',
      loading: false,
      announcementList: [],
      filteredList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      showDetailModal: false,
      currentDetail: {}
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    },
    displayPages() {
      const pages = []
      const total = this.totalPages
      const current = this.currentPage

      if (total <= 5) {
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        if (current <= 3) {
          for (let i = 1; i <= 5; i++) {
            pages.push(i)
          }
        } else if (current >= total - 2) {
          for (let i = total - 4; i <= total; i++) {
            pages.push(i)
          }
        } else {
          for (let i = current - 2; i <= current + 2; i++) {
            pages.push(i)
          }
        }
      }

      return pages
    }
  },
  mounted() {
    this.loadAnnouncementList()
  },
  methods: {
    async loadAnnouncementList() {
      this.loading = true
      try {
        const response = await getAnnouncementList(this.currentPage, this.pageSize)
        if (response.data.code === 1) {
          this.announcementList = response.data.data.list || []
          this.total = response.data.data.total || 0
          this.applyFilters()
        } else {
          ElMessage.error(response.data.msg || '获取公告列表失败')
        }
      } catch (error) {
        console.error('获取公告列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    applyFilters() {
      let list = [...this.announcementList]

      if (this.filterType !== 'all') {
        list = list.filter(item => item.announcementTypeName === this.filterType)
      }

      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.toLowerCase()
        list = list.filter(item =>
          item.title.toLowerCase().includes(keyword) ||
          item.content.toLowerCase().includes(keyword)
        )
      }

      this.filteredList = list
    },

    handleSearch() {
      this.currentPage = 1  // 重置到第一页
      this.applyFilters()
    },

    handleFilter(type) {
      this.filterType = type
      this.applyFilters()
    },

    changePage(page) {
      if (page < 1 || page > this.totalPages) return
      this.currentPage = page
      this.loadAnnouncementList()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    // 切换每页显示条数
    handlePageSizeChange() {
      this.currentPage = 1  // 重置到第一页
      this.loadAnnouncementList()
    },

    // 获取内容摘要（前50个字）
    getContentPreview(content) {
      if (!content) return ''
      return content.length > 50 ? content.substring(0, 50) + '...' : content
    },

    // 查看详情
    viewDetail(item) {
      this.currentDetail = item
      this.showDetailModal = true
      // 禁止背景滚动
      document.body.style.overflow = 'hidden'
    },

    // 关闭详情弹窗
    closeDetail() {
      this.showDetailModal = false
      this.currentDetail = {}
      // 恢复背景滚动
      document.body.style.overflow = ''
    },

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

    formatDate(dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.announcement-view {
  width: 100%;
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

.search-wrapper {
  flex: 1;
  min-width: 200px;
  max-width: 300px;
  display: flex;
  gap: 0;
}

.search-box {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-right: none;
  border-radius: 4px 0 0 4px;
  font-size: 13px;
  transition: border-color 0.3s;
}

.search-box:focus {
  outline: none;
  border-color: #3498db;
}

.search-btn {
  width: 45px;
  padding: 0;
  border: 1px solid #ddd;
  border-left: none;
  background: white;
  color: #7f8c8d;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-btn:hover {
  background: #3498db;
  border-color: #3498db;
}

.search-box:focus + .search-btn {
  border-color: #3498db;
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
  transform: translateY(-2px);
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
  transition: all 0.3s;
}

.detail-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
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

.page-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-info {
  color: #7f8c8d;
  font-size: 13px;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8c8d;
  font-size: 13px;
}

.size-select {
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  color: #2c3e50;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.size-select:hover {
  border-color: #3498db;
}

.size-select:focus {
  outline: none;
  border-color: #3498db;
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

/* ========== 详情弹窗 ========== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(3px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}

.modal-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 700px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e0e0e0;
  flex-shrink: 0;
}

.modal-tag {
  padding: 5px 15px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  color: #666;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  line-height: 1;
  transition: all 0.3s;
}

.close-btn:hover {
  background: #e0e0e0;
  color: #333;
  transform: rotate(90deg);
}

.modal-content {
  padding: 25px;
  overflow-y: auto;
  flex: 1;
}

.modal-title {
  font-size: 22px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 15px;
  line-height: 1.4;
}

.modal-meta {
  display: flex;
  gap: 20px;
  color: #7f8c8d;
  font-size: 13px;
  margin-bottom: 20px;
}

.modal-divider {
  height: 1px;
  background: #e0e0e0;
  margin-bottom: 20px;
}

.modal-text {
  color: #2c3e50;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
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

/* 响应式 */
@media (max-width: 768px) {
  .modal-box {
    max-width: 95%;
    max-height: 90vh;
  }

  .modal-title {
    font-size: 18px;
  }

  .modal-text {
    font-size: 14px;
  }
}
</style>