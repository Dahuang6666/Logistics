<template>
  <div class="dorm-change-progress-container">
    <div class="page-header">
      <h1 class="page-title">📋 换宿进度</h1>
      <p class="subtitle">查看您的换宿申请处理进度</p>
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
            :class="{ active: statusFilter === 'PENDING' }"
            @click="changeStatusFilter('PENDING')"
          >
            待审批
          </button>
          <button
            class="filter-btn"
            :class="{ active: statusFilter === 'APPROVED' }"
            @click="changeStatusFilter('APPROVED')"
          >
            已同意
          </button>
          <button
            class="filter-btn"
            :class="{ active: statusFilter === 'REJECTED' }"
            @click="changeStatusFilter('REJECTED')"
          >
            已拒绝
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
    <div v-else-if="filteredList.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <p class="empty-text">
        {{ statusFilter ? `该状态下暂无申请记录` : '暂无换宿申请记录' }}
      </p>
      <button v-if="!statusFilter" class="btn-primary" @click="goToDormChange">
        去申请换宿
      </button>
      <button v-else class="btn-secondary" @click="changeStatusFilter('')">
        查看全部
      </button>
    </div>

    <!-- 申请列表 -->
    <div v-else class="application-list">
      <div
        v-for="item in filteredList"
        :key="item.applicationId"
        class="application-card"
      >
        <!-- 卡片头部 -->
        <div class="card-header">
          <div class="application-info">
            <span class="application-id">申请编号: #{{ item.applicationId }}</span>
          </div>
          <span class="status-badge" :class="'status-' + getStatusClass(item.status)">
            {{ getStatusText(item.status) }}
          </span>
        </div>

        <!-- 卡片内容 -->
        <div class="card-body">
          <!-- 宿舍信息 -->
          <div class="dorm-section">
            <div class="dorm-item">
              <div class="dorm-label">
                <span class="icon">🏠</span>
                当前宿舍
              </div>
              <div class="dorm-value">
                {{ item.currentDormInfo || currentDormInfo || '未知' }}
              </div>
            </div>
            <div class="arrow-icon">→</div>
            <div class="dorm-item">
              <div class="dorm-label">
                <span class="icon">🎯</span>
                目标宿舍
              </div>
              <div class="dorm-value">
                {{ item.targetDormInfo || '由管理员分配' }}
              </div>
            </div>
          </div>

          <!-- 申请原因 -->
          <div class="content-section">
            <div class="content-label">申请原因:</div>
            <div class="content-text">{{ item.reason }}</div>
          </div>

          <!-- 审批意见 (已同意/已拒绝时显示) -->
          <div
            v-if="(item.status === 'APPROVED' || item.status === 'REJECTED') && item.comment"
            class="approval-section"
            :class="item.status === 'APPROVED' ? 'approval-approved' : 'approval-rejected'"
          >
            <div class="approval-header">
              <span class="approval-icon">
                {{ item.status === 'APPROVED' ? '✅' : '❌' }}
              </span>
              <span class="approval-title">审批意见</span>
            </div>
            <div class="approval-content">{{ item.comment }}</div>
            <div class="approval-footer">
              <span class="approver">审批人: {{ item.approverNo || '未知' }}</span>
              <span class="approval-time">{{ formatTime(item.approvalTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 卡片底部 -->
        <div class="card-footer">
          <div class="time-info">
            <span class="time-icon">🕐</span>
            <span class="time-text">申请时间: {{ formatTime(item.applicationTime) }}</span>
          </div>

          <!-- 撤销按钮(仅待审批状态显示) -->
          <button
            v-if="item.status === 'PENDING'"
            class="btn-cancel-application"
            @click="handleCancelApplication(item.applicationId)"
          >
            撤销申请
          </button>
        </div>
      </div>
    </div>

    <!-- 撤销确认弹窗 -->
    <transition name="modal-fade">
      <div v-if="showCancelModal" class="confirm-modal-overlay" @click="closeCancelModal">
        <div class="confirm-modal" @click.stop>
          <div class="modal-icon">⚠️</div>
          <h3 class="modal-title">确认撤销</h3>
          <p class="modal-message">确定要撤销这条换宿申请吗？</p>
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
import { getMyDormApplications, cancelDormApplication, getCurrentDormInfo, getDormNameById } from '@/utils/api.js'

export default {
  name: 'DormChangeProgress',
  data() {
    return {
      loading: false,
      userNo: '',
      applicationList: [],
      statusFilter: '',
      showCancelModal: false,
      cancelApplicationId: null,
      currentDormInfo: ''
    }
  },
  computed: {
    filteredList() {
      if (!this.statusFilter) {
        return this.applicationList
      }
      return this.applicationList.filter(item => item.status === this.statusFilter)
    }
  },
  mounted() {
    this.userNo = sessionStorage.getItem('userNo') || ''
    this.loadCurrentDormInfo()
    this.loadApplicationList()
  },
  methods: {
    // 加载当前宿舍信息
    async loadCurrentDormInfo() {
      try {
        const response = await getCurrentDormInfo(this.userNo)
        if (response.data.code === 1) {
          this.currentDormInfo = response.data.data || '未知'
          console.log('当前宿舍信息:', this.currentDormInfo)
        }
      } catch (error) {
        console.error('获取当前宿舍信息失败:', error)
      }
    },

    // 加载申请列表
    async loadApplicationList() {
      this.loading = true
      try {
        const response = await getMyDormApplications(this.userNo)

        if (response.data.code === 1) {
          const list = response.data.data || []

          for (const item of list) {
            // 目标宿舍为 0：管理员分配
            if (item.targetDormitoryId === 0) {
              item.targetDormInfo = '由管理员分配'
            } else {
              try {
                const dormRes = await getDormNameById(item.targetDormitoryId)
                if (dormRes.data.code === 1) {
                  item.targetDormInfo = dormRes.data.data
                } else {
                  item.targetDormInfo = '未知'
                }
              } catch (e) {
                console.error('获取目标宿舍失败:', e)
                item.targetDormInfo = '未知'
              }
            }
          }

          this.applicationList = list
          console.log('申请列表(已补全宿舍信息):', this.applicationList)
        } else {
          ElMessage.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取申请列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },


    // 切换状态筛选
    changeStatusFilter(status) {
      this.statusFilter = status
    },

    // 获取状态样式类
    getStatusClass(status) {
      const map = {
        'PENDING': 'pending',
        'APPROVED': 'approved',
        'REJECTED': 'rejected'
      }
      return map[status] || 'pending'
    },

    // 获取状态文本
    getStatusText(status) {
      const map = {
        'PENDING': '待审批',
        'APPROVED': '已同意',
        'REJECTED': '已拒绝'
      }
      return map[status] || '未知'
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

    // 撤销申请
    handleCancelApplication(applicationId) {
      this.cancelApplicationId = applicationId
      this.showCancelModal = true
      document.body.style.overflow = 'hidden'
    },

    // 关闭撤销弹窗
    closeCancelModal() {
      this.showCancelModal = false
      this.cancelApplicationId = null
      document.body.style.overflow = ''
    },

    // 确认撤销
    async confirmCancel() {
      if (!this.cancelApplicationId) return

      try {
        const response = await cancelDormApplication(this.cancelApplicationId, this.userNo)

        if (response.data.code === 1) {
          ElMessage.success('已撤销申请')
          this.closeCancelModal()
          this.loadApplicationList()
        } else {
          ElMessage.error(response.data.msg || '撤销失败')
        }
      } catch (error) {
        console.error('撤销申请失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      }
    },

    // 跳转到换宿申请
    goToDormChange() {
      this.$router.push('/student/dorm-change')
    }
  }
}
</script>

<style scoped>
.dorm-change-progress-container {
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

/* 申请列表 */
.application-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  overflow: hidden;
  transition: all 0.3s;
}

.application-card:hover {
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

.application-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.application-id {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
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

.status-approved {
  background: #e8f5e9;
  color: #27ae60;
}

.status-rejected {
  background: #fee;
  color: #e74c3c;
}

/* 卡片内容 */
.card-body {
  padding: 20px;
}

/* 宿舍信息 */
.dorm-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.dorm-item {
  flex: 1;
}

.dorm-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #7f8c8d;
  font-weight: 500;
  margin-bottom: 8px;
}

.dorm-label .icon {
  font-size: 16px;
}

.dorm-value {
  font-size: 15px;
  color: #2c3e50;
  font-weight: 600;
}

.arrow-icon {
  font-size: 24px;
  color: #3498db;
  font-weight: bold;
}

/* 申请原因 */
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
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #3498db;
}

/* 审批意见 */
.approval-section {
  margin-top: 20px;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid;
}

.approval-approved {
  background: #f0f9f4;
  border-color: #a8e6c1;
}

.approval-rejected {
  background: #fef5f5;
  border-color: #f5c2c7;
}

.approval-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.approval-icon {
  font-size: 18px;
}

.approval-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.approval-content {
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.6;
  margin-bottom: 10px;
}

.approval-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #7f8c8d;
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

.btn-cancel-application {
  padding: 6px 16px;
  background: white;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel-application:hover {
  background: #e74c3c;
  color: white;
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

  .dorm-section {
    flex-direction: column;
    gap: 10px;
  }

  .arrow-icon {
    transform: rotate(90deg);
  }

  .card-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .btn-cancel-application {
    width: 100%;
  }

  .approval-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
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