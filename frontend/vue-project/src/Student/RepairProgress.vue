<template>
  <div class="repair-progress-container">
    <div class="page-header">
      <h1 class="page-title">📋 报修进度</h1>
      <p class="subtitle">查看您的报修申请处理进度</p>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label class="filter-label">状态筛选:</label>
        <div class="filter-buttons">
          <button
            class="filter-btn"
            :class="{ active: statusFilter === '' }"
            @click="changeStatusFilter('')"
          >
            全部
          </button>
          <button
            class="filter-btn"
            :class="{ active: statusFilter === '待处理' }"
            @click="changeStatusFilter('待处理')"
          >
            待处理
          </button>
          <button
            class="filter-btn"
            :class="{ active: statusFilter === '处理中' }"
            @click="changeStatusFilter('处理中')"
          >
            处理中
          </button>
          <button
            class="filter-btn"
            :class="{ active: statusFilter === '已完成' }"
            @click="changeStatusFilter('已完成')"
          >
            已完成
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 空状态 -->
    <div v-else-if="repairList.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <p class="empty-text">
        {{ statusFilter ? `该状态下暂无报修记录` : '暂无报修记录' }}
      </p>
      <button v-if="!statusFilter" class="btn-primary" @click="goToRepairApply">
        去报修
      </button>
      <button v-else class="btn-secondary" @click="changeStatusFilter('')">
        查看全部
      </button>
    </div>

    <!-- 报修列表 -->
    <div v-else class="repair-list">
      <div
        v-for="item in repairList"
        :key="item.id"
        class="repair-card"
      >
        <!-- 卡片头部 -->
        <div class="card-header">
          <div class="repair-info">
            <span class="repair-type">{{ item.repairType || '未分类' }}</span>
            <span class="urgency-badge" :class="'urgency-' + item.urgencyLevel">
              {{ item.urgencyLevel }}
            </span>
          </div>
          <span class="status-badge" :class="'status-' + getStatusClass(item.status)">
            {{ item.status }}
          </span>
        </div>

        <!-- 卡片内容 -->
        <div class="card-body">
          <div class="content-section">
            <div class="content-label">报修内容:</div>
            <div class="content-text">{{ item.content }}</div>
          </div>

          <!-- 图片展示 -->
          <div v-if="item.imageUrl" class="image-section">
            <div class="image-wrapper" @click="previewImage(item.imageUrl)">
              <img :src="item.imageUrl" alt="报修图片" class="repair-image" />
              <div class="image-overlay">
                <span class="view-icon">🔍 点击查看大图</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 卡片底部 -->
        <div class="card-footer">
          <div class="time-info">
            <span class="time-icon">🕐</span>
            <span class="time-text">{{ formatTime(item.createTime) }}</span>
          </div>

          <!-- 撤销按钮(仅待处理状态显示) -->
          <button
            v-if="item.status === '待处理'"
            class="btn-cancel-repair"
            @click="handleCancelRepair(item.id)"
          >
            撤销报修
          </button>
        </div>
      </div>
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

    <!-- 图片预览弹窗 -->
    <div v-if="showImagePreview" class="image-preview-modal" @click="closeImagePreview">
      <div class="preview-content">
        <img :src="previewImageUrl" alt="预览图片" class="preview-image" />
        <button class="close-btn" @click="closeImagePreview">✕</button>
      </div>
    </div>

    <!-- 撤销确认弹窗 -->
    <transition name="modal-fade">
      <div v-if="showCancelModal" class="confirm-modal-overlay" @click="closeCancelModal">
        <div class="confirm-modal" @click.stop>
          <div class="modal-icon">⚠️</div>
          <h3 class="modal-title">确认撤销</h3>
          <p class="modal-message">确定要撤销这条报修申请吗？</p>
          <p class="modal-hint">撤销后将无法恢复</p>
          <div class="modal-actions">
            <button class="modal-btn btn-cancel" @click="closeCancelModal">
              取消
            </button>
            <button class="modal-btn btn-confirm" @click="confirmCancel">
              确认撤销
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getMyRepairs, cancelRepair } from '@/utils/api.js'

export default {
  name: 'RepairProgress',
  data() {
    return {
      loading: false,
      userNo: '',
      repairList: [],
      statusFilter: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      showImagePreview: false,
      previewImageUrl: '',
      showCancelModal: false,
      cancelRepairId: null
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
    this.userNo = sessionStorage.getItem('userNo') || ''
    this.loadRepairList()
  },
  methods: {
    // 加载报修列表
    async loadRepairList() {
      this.loading = true
      try {
        const params = {
          userNo: this.userNo,
          pageNum: this.currentPage,
          pageSize: this.pageSize
        }

        if (this.statusFilter) {
          params.status = this.statusFilter
        }

        const response = await getMyRepairs(params)

        if (response.data.code === 1) {
          const pageData = response.data.data
          this.repairList = pageData.records || []
          this.total = pageData.total || 0
        } else {
          ElMessage.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取报修列表失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 切换状态筛选
    changeStatusFilter(status) {
      this.statusFilter = status
      this.currentPage = 1
      this.loadRepairList()
    },

    // 切换页码
    changePage(page) {
      if (page < 1 || page > this.totalPages) return
      this.currentPage = page
      this.loadRepairList()
    },

    // 切换每页显示条数
    handlePageSizeChange() {
      this.currentPage = 1  // 重置到第一页
      this.loadRepairList()
    },

    // 获取状态样式类
    getStatusClass(status) {
      const map = {
        '待处理': 'pending',
        '处理中': 'processing',
        '已完成': 'completed'
      }
      return map[status] || 'pending'
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    },

    // 预览图片
    previewImage(url) {
      this.previewImageUrl = url
      this.showImagePreview = true
    },

    // 关闭图片预览
    closeImagePreview() {
      this.showImagePreview = false
      this.previewImageUrl = ''
    },

    // 撤销报修
    async handleCancelRepair(repairId) {
      this.cancelRepairId = repairId
      this.showCancelModal = true
      document.body.style.overflow = 'hidden'
    },

    // 关闭撤销弹窗
    closeCancelModal() {
      this.showCancelModal = false
      this.cancelRepairId = null
      document.body.style.overflow = ''
    },

    // 确认撤销
    async confirmCancel() {
      if (!this.cancelRepairId) return

      try {
        const response = await cancelRepair(this.cancelRepairId, this.userNo)

        if (response.data.code === 1) {
          ElMessage.success('已撤销报修')
          this.closeCancelModal()
          this.loadRepairList()
        } else {
          ElMessage.error(response.data.msg || '撤销失败')
        }
      } catch (error) {
        console.error('撤销报修失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      }
    },

    // 跳转到报修申请
    goToRepairApply() {
      this.$router.push('/student/repair')
    }
  }
}
</script>

<style scoped>
.repair-progress-container {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
}

/* 筛选工具栏 */
.filter-bar {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #e0e0e0;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 8px 20px;
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.filter-btn:hover {
  border-color: #3498db;
  color: #3498db;
}

.filter-btn.active {
  background: #3498db;
  color: white;
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

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
  color: #7f8c8d;
  margin-bottom: 30px;
}

.btn-primary {
  padding: 12px 30px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.btn-secondary {
  padding: 12px 30px;
  background: white;
  color: #3498db;
  border: 2px solid #3498db;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #3498db;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

/* 报修卡片列表 */
.repair-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.repair-card {
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  overflow: hidden;
  transition: all 0.3s;
}

.repair-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.repair-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.repair-type {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.urgency-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.urgency-高 {
  background: #fee;
  color: #e74c3c;
}

.urgency-中 {
  background: #fff4e5;
  color: #f39c12;
}

.urgency-低 {
  background: #e8f5e9;
  color: #27ae60;
}

.status-badge {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
}

.status-pending {
  background: #fff4e5;
  color: #f39c12;
}

.status-processing {
  background: #e3f2fd;
  color: #2196f3;
}

.status-completed {
  background: #e8f5e9;
  color: #27ae60;
}

/* 卡片内容 */
.card-body {
  padding: 20px;
}

.content-section {
  margin-bottom: 15px;
}

.content-label {
  font-size: 13px;
  color: #7f8c8d;
  font-weight: 500;
  margin-bottom: 8px;
}

.content-text {
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.6;
}

/* 图片展示 */
.image-section {
  margin-top: 15px;
}

.image-wrapper {
  position: relative;
  display: inline-block;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  max-width: 300px;
}

.repair-image {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.3s;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-wrapper:hover .image-overlay {
  opacity: 1;
}

.image-wrapper:hover .repair-image {
  transform: scale(1.05);
}

.view-icon {
  color: white;
  font-size: 14px;
  font-weight: 600;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8c8d;
  font-size: 13px;
}

.time-icon {
  font-size: 16px;
}

.btn-cancel-repair {
  padding: 6px 16px;
  background: white;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel-repair:hover {
  background: #e74c3c;
  color: white;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.page-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-info {
  color: #7f8c8d;
  font-size: 14px;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8c8d;
  font-size: 14px;
}

.size-select {
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: white;
  color: #2c3e50;
  font-size: 14px;
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
  min-width: 36px;
  height: 36px;
  padding: 0 10px;
  border: 1px solid #dcdfe6;
  background: white;
  color: #606266;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
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
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 图片预览弹窗 */
.image-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  cursor: pointer;
}

.preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.preview-image {
  max-width: 100%;
  max-height: 90vh;
  border-radius: 8px;
}

.close-btn {
  position: absolute;
  top: -40px;
  right: 0;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  font-size: 24px;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s;
}

.close-btn:hover {
  background: white;
  transform: scale(1.1);
}

/* 撤销确认弹窗 */
.confirm-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
}

.confirm-modal {
  background: white;
  border-radius: 16px;
  padding: 40px 30px 30px;
  width: 100%;
  max-width: 420px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: iconBounce 0.6s ease;
}

@keyframes iconBounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.modal-title {
  font-size: 22px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 15px;
}

.modal-message {
  font-size: 15px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.6;
}

.modal-hint {
  font-size: 13px;
  color: #95a5a6;
  margin-bottom: 30px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.modal-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f5f5f5;
  color: #606266;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-confirm {
  background: #e74c3c;
  color: white;
}

.btn-confirm:hover {
  background: #c0392b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
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

.modal-fade-enter-active .confirm-modal {
  animation: modalSlideIn 0.3s ease;
}

.modal-fade-leave-active .confirm-modal {
  animation: modalSlideOut 0.3s ease;
}

@keyframes modalSlideOut {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .filter-group {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-buttons {
    flex-wrap: wrap;
    width: 100%;
  }

  .filter-btn {
    flex: 1;
    min-width: calc(50% - 5px);
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .btn-cancel-repair {
    width: 100%;
  }

  .pagination {
    flex-direction: column;
    gap: 15px;
  }

  .page-left {
    width: 100%;
    justify-content: space-between;
  }

  .page-buttons {
    width: 100%;
    justify-content: center;
  }

  .confirm-modal {
    max-width: 90%;
    padding: 30px 20px 20px;
  }

  .modal-actions {
    flex-direction: column;
  }

  .modal-btn {
    width: 100%;
  }
}
</style>