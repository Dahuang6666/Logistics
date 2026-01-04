<template>
  <div class="feedback-view">
    <div class="page-header">
      <h1 class="page-title">投诉与建议</h1>
      <button class="submit-btn" @click="showSubmitModal = true">
        ✍️ 提交反馈
      </button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <button
        class="filter-btn"
        :class="{ active: filterStatus === 'all' }"
        @click="filterStatus = 'all'"
      >
        全部
      </button>
      <button
        class="filter-btn"
        :class="{ active: filterStatus === '未处理' }"
        @click="filterStatus = '未处理'"
      >
        未处理
      </button>
      <button
        class="filter-btn"
        :class="{ active: filterStatus === '已处理' }"
        @click="filterStatus = '已处理'"
      >
        已处理
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 反馈列表 -->
    <div v-else-if="filteredList.length > 0" class="feedback-list">
      <div
        v-for="item in filteredList"
        :key="item.id"
        class="feedback-item"
        :class="{ processed: item.status === '已处理' }"
      >
        <div class="feedback-header">
          <span class="feedback-id">#{{ item.id }}</span>
          <span
            class="status-tag"
            :class="{
              'status-pending': item.status === '未处理',
              'status-done': item.status === '已处理'
            }"
          >
            {{ item.status }}
          </span>
        </div>

        <div class="feedback-content">{{ item.content }}</div>

        <div class="feedback-meta">
          <span>📅 提交时间：{{ formatDate(item.createTime) }}</span>
          <span v-if="item.status === '已处理'">
            ✅ 处理时间：{{ formatDate(item.updateTime) }}
          </span>
        </div>

        <!-- 管理员回复 -->
        <div v-if="item.reply" class="feedback-reply">
          <div class="reply-header">💬 管理员回复：</div>
          <div class="reply-content">{{ item.reply }}</div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">📝</div>
      <p>暂无投诉建议记录</p>
      <button class="empty-btn" @click="showSubmitModal = true">
        提交第一条反馈
      </button>
    </div>

    <!-- 提交反馈弹窗 -->
    <transition name="modal-fade">
      <div v-if="showSubmitModal" class="modal-overlay" @click="closeSubmitModal">
        <div class="modal-box" @click.stop>
          <div class="modal-header">
            <h3>提交投诉与建议</h3>
            <button class="close-btn" @click="closeSubmitModal">✕</button>
          </div>
          <div class="modal-content">
            <textarea
              v-model="submitContent"
              class="feedback-textarea"
              placeholder="请详细描述您的投诉或建议内容...&#10;&#10;例如：&#10;1. 宿舍设施问题&#10;2. 管理服务建议&#10;3. 其他意见反馈"
              maxlength="500"
            ></textarea>
            <div class="char-count">{{ submitContent.length }} / 500</div>
          </div>
          <div class="modal-footer">
            <button class="modal-btn cancel-btn" @click="closeSubmitModal">
              取消
            </button>
            <button
              class="modal-btn submit-btn-modal"
              @click="handleSubmit"
              :disabled="!submitContent.trim() || submitting"
            >
              {{ submitting ? '提交中...' : '提交' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { submitFeedback, getMyFeedbackList } from '@/utils/api.js'

export default {
  name: 'FeedbackView',
  data() {
    return {
      loading: false,
      feedbackList: [],
      filterStatus: 'all',
      showSubmitModal: false,
      submitContent: '',
      submitting: false
    }
  },
  computed: {
    filteredList() {
      if (this.filterStatus === 'all') {
        return this.feedbackList
      }
      return this.feedbackList.filter(item => item.status === this.filterStatus)
    }
  },
  mounted() {
    this.loadFeedbackList()
  },
  methods: {
    // 加载投诉建议列表
    async loadFeedbackList() {
      this.loading = true
      const userNo = localStorage.getItem('userNo')

      if (!userNo) {
        ElMessage.error('用户信息获取失败，请重新登录')
        this.loading = false
        return
      }

      try {
        const response = await getMyFeedbackList(userNo)
        if (response.data.code === 1) {
          this.feedbackList = response.data.data || []
          // 按时间倒序排列（最新的在前）
          this.feedbackList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        } else {
          ElMessage.error(response.data.msg )
        }
      } catch (error) {
        console.error('获取投诉建议列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 提交反馈
    async handleSubmit() {
      if (!this.submitContent.trim()) {
        ElMessage.warning('请输入内容')
        return
      }

      const userNo = localStorage.getItem('userNo')
      if (!userNo) {
        ElMessage.error('用户信息获取失败，请重新登录')
        return
      }

      this.submitting = true

      try {
        const response = await submitFeedback({
          userNo: userNo,
          content: this.submitContent.trim()
        })

        if (response.data.code === 1) {
          ElMessage.success('提交成功！我们会尽快处理')
          this.closeSubmitModal()
          this.loadFeedbackList() // 刷新列表
        } else {
          ElMessage.error(response.data.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交投诉建议失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.submitting = false
      }
    },

    // 关闭提交弹窗
    closeSubmitModal() {
      this.showSubmitModal = false
      this.submitContent = ''
      document.body.style.overflow = ''
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  },
  watch: {
    showSubmitModal(val) {
      if (val) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
      }
    }
  }
}
</script>

<style scoped>
.feedback-view {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

.submit-btn {
  padding: 10px 25px;
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

.submit-btn:hover {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
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
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 反馈列表 */
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.feedback-item {
  background: white;
  border: 2px solid #ddd;
  border-left: 4px solid #f39c12;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s;
}

.feedback-item.processed {
  border-left-color: #27ae60;
  opacity: 0.9;
}

.feedback-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.feedback-id {
  font-size: 14px;
  font-weight: bold;
  color: #7f8c8d;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-done {
  background: #d4edda;
  color: #155724;
}

.feedback-content {
  color: #2c3e50;
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 15px;
  white-space: pre-wrap;
  word-break: break-word;
}

.feedback-meta {
  display: flex;
  gap: 20px;
  color: #7f8c8d;
  font-size: 12px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.feedback-reply {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #3498db;
}

.reply-header {
  font-size: 13px;
  font-weight: 600;
  color: #3498db;
  margin-bottom: 8px;
}

.reply-content {
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.6;
  white-space: pre-wrap;
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

.empty-btn {
  margin-top: 20px;
  padding: 10px 30px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.empty-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
}

/* ========== 提交弹窗 ========== */
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
  max-width: 600px;
  margin: 0 auto; /* 添加这一行！ */
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
}

.modal-header h3 {
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
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
  transition: all 0.3s;
}

.close-btn:hover {
  background: #e0e0e0;
  color: #333;
  transform: rotate(90deg);
}

.modal-content {
  padding: 25px;
}

.feedback-textarea {
  width: 100%;
  min-height: 200px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.3s;
  box-sizing: border-box; /* 👈 添加这一行 */
}

.feedback-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.char-count {
  text-align: right;
  color: #7f8c8d;
  font-size: 12px;
  margin-top: 8px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 25px;
  border-top: 1px solid #e0e0e0;
}

.modal-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.submit-btn-modal {
  background: linear-gradient(135deg, #42a5f5, #1976d2);
  color: white;
  box-shadow: 0 4px 15px rgba(25, 118, 210, 0.3);
}

.submit-btn-modal:hover:not(:disabled) {
  background: linear-gradient(135deg, #64b5f6, #42a5f5);
  transform: translateY(-2px);
}

.submit-btn-modal:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
</style>