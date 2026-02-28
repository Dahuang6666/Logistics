<template>
  <div class="user-management-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">📩 建议反馈管理</h1>
        <p class="page-subtitle">查看并回复学生提交的投诉与建议</p>
      </div>
    </div>

    <div class="filter-bar">
      <div class="search-wrapper">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索用户编号或反馈内容..."
          class="search-box"
        >
      </div>
      <div class="status-filter">
        <select v-model="filterStatus" class="type-select">
          <option value="">全部状态</option>
          <option value="未处理">待处理</option>
          <option value="已处理">已处理</option>
        </select>
      </div>
    </div>

    <div class="table-card">
      <table class="admin-table">
        <thead>
        <tr>
          <th width="80">ID</th>
          <th width="150">反馈用户</th>
          <th>内容摘要</th>
          <th width="180">提交时间</th>
          <th width="120">状态</th>
          <th style="text-align: center" width="180">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in filteredList" :key="item.id">
          <td>{{ item.id }}</td>
          <td><span class="user-no-tag">{{ item.userNo }}</span></td>
          <td class="content-cell">{{ truncateText(item.content) }}</td>
          <td>{{ item.createTime }}</td>
          <td>
            <div class="status-wrapper">
              <span :class="['status-dot', item.status === '已处理' ? 'status-active' : 'status-pending']"></span>
              {{ item.status }}
            </div>
          </td>
          <td class="action-cells">
            <button class="btn-text edit" @click="handleOpenDetail(item)">
              {{ item.status === '已处理' ? '查看详情' : '处理回复' }}
            </button>
            <button class="btn-text delete" @click="confirmDelete(item)">删除</button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="filteredList.length === 0" class="empty-placeholder">
        <div class="empty-icon">📂</div>
        <p>暂无符合条件的反馈数据</p>
      </div>
    </div>

    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag" :class="currentEdit.status === '已处理' ? 'tag-activity' : 'tag-rule'">
                {{ currentEdit.status }}
              </span>
              <h3 class="modal-title-text">建议反馈详情</h3>
            </div>
            <button class="close-btn" @click="closeDialog">✕</button>
          </div>

          <div class="modal-content">
            <!-- 基础信息区域 - 一行两列布局 -->
            <div class="detail-section basic-info">
              <div class="detail-item">
                <label>反馈用户</label>
                <span>{{ currentEdit.userNo }}</span>
              </div>
              <div class="detail-item">
                <label>提交时间</label>
                <span>{{ currentEdit.createTime }}</span>
              </div>
            </div>

            <!-- 反馈内容区域 -->
            <div class="detail-section content-section">
              <label>反馈内容</label>
              <div class="content-box">{{ currentEdit.content }}</div>
            </div>

            <!-- 回复结果区域 -->
            <div class="detail-section reply-section">
              <label>回复结果 <span class="required" v-if="currentEdit.status !== '已处理'">*</span></label>
              <textarea
                v-model="currentEdit.reply"
                :disabled="currentEdit.status === '已处理'"
                placeholder="请输入处理意见或回复内容..."
                class="modal-textarea"
                rows="5"
              ></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button class="cancel-btn" @click="closeDialog">返回</button>
            <!-- 关键修正：只有未处理的才显示提交按钮 -->
            <button
              v-if="currentEdit.status === '未处理'"
              class="confirm-btn"
              @click="submitHandle"
            >确认提交处理</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { getListSuggest, handleSuggest, deleteSuggest } from '@/utils/api.js'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'FeedbackProcessing',
  data() {
    return {
      feedbackList: [],
      searchKeyword: '',
      filterStatus: '',
      showDialog: false,
      currentEdit: {
        id: null,
        userNo: '',
        content: '',
        status: '',
        reply: ''
      }
    }
  },
  computed: {
    filteredList() {
      return this.feedbackList.filter(item => {
        const matchesSearch = (item.userNo && item.userNo.includes(this.searchKeyword)) ||
          (item.content && item.content.includes(this.searchKeyword));
        const matchesStatus = this.filterStatus === '' || item.status === this.filterStatus;
        return matchesSearch && matchesStatus;
      }).sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      try {
        const res = await getListSuggest();
        if (res.data.code === 1) {
          this.feedbackList = res.data.data;
        }
      } catch (e) {
        ElMessage.error('获取列表失败，请检查网络');
      }
    },
    truncateText(text) {
      if (!text) return '';
      return text.length > 40 ? text.substring(0, 40) + '...' : text;
    },
    handleOpenDetail(item) {
      this.currentEdit = { ...item };
      this.showDialog = true;
    },
    closeDialog() {
      this.showDialog = false;
    },
    async submitHandle() {
      if (!this.currentEdit.reply || this.currentEdit.reply.trim() === '') {
        return ElMessage.warning('回复内容不能为空');
      }
      try {
        const submitData = { ...this.currentEdit, status: '已处理' };
        const res = await handleSuggest(submitData);
        if (res.data.code === 1) {
          ElMessage.success('处理成功并已通知用户');
          this.closeDialog();
          this.loadData();
        } else {
          ElMessage.error(res.data.msg || '提交失败');
        }
      } catch (e) {
        ElMessage.error('服务器响应异常');
      }
    },
    confirmDelete(item) {
      ElMessageBox.confirm('确定要永久删除这条反馈记录吗？删除后不可恢复。', '警告', {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        confirmButtonClass: 'btn-danger-confirm'
      }).then(async () => {
        try {
          const res = await deleteSuggest(item.id);
          if (res.data.code === 1) {
            ElMessage.success('删除成功');
            this.loadData();
          }
        } catch (e) {
          ElMessage.error('删除操作失败');
        }
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
/* ========================================================================
   1. 基础布局
   ======================================================================== */
.user-management-container {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.page-subtitle {
  color: #64748b;
  font-size: 14px;
  margin-top: 6px;
}

/* ========================================================================
   2. 过滤栏 - 更大间距 + 更短下拉框
   ======================================================================== */
.filter-bar {
  display: flex;
  gap: 50px; /* 增大间距 */
  margin-bottom: 24px;
  align-items: center;
  flex-wrap: wrap;
  padding: 8px 0;
}

.search-wrapper {
  flex: 0 1 380px;
  min-width: 200px;
}

.search-box {
  width: 100%;
  padding: 11px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}

.search-box:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.status-filter {
  min-width: 140px; /* 缩短下拉框 */
  width: 140px;
  display: flex;
  align-items: center;
}

.type-select {
  width: 100%;
  padding: 11px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background-color: white;
  color: #475569;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.3s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath fill='%2364748b' d='M10.84 1.84L6 6.69 1.16 1.84A1 1 0 00-.16 3.16l6 6a1 1 0 001.32 0l6-6a1 1 0 10-1.32-1.32z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  padding-right: 40px;
}

.type-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

/* ========================================================================
   3. 表格与卡片
   ======================================================================== */
.table-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background: #f1f5f9;
  padding: 16px 20px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.admin-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #334155;
}

.content-cell {
  color: #64748b;
  line-height: 1.5;
}

.user-no-tag {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 4px 10px;
  border-radius: 6px;
  font-family: 'JetBrains Mono', 'Monaco', monospace;
  font-size: 13px;
  font-weight: 500;
}

.status-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-active { background: #10b981; box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1); }
.status-pending { background: #f59e0b; box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1); }

.action-cells {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.btn-text {
  background: transparent;
  border: none;
  padding: 6px 12px;
  cursor: pointer;
  font-weight: 600;
  font-size: 13px;
  border-radius: 8px;
  transition: all 0.2s;
}

.btn-text.edit { color: #2563eb; }
.btn-text.edit:hover { background: #eff6ff; }
.btn-text.delete { color: #dc2626; }
.btn-text.delete:hover { background: #fef2f2; }

.empty-placeholder {
  padding: 80px 0;
  text-align: center;
  color: #94a3b8;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

/* ========================================================================
   4. 弹窗样式 - 全新优化布局
   ======================================================================== */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-box {
  background: white;
  width: 95%;
  max-width: 700px; /* 适度加宽弹窗 */
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-tag {
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  color: white;
  text-transform: uppercase;
  margin-right: 12px;
}

.tag-rule { background: #f59e0b; }
.tag-activity { background: #10b981; }

.modal-title-text {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  display: inline-block;
}

.close-btn {
  background: #f1f5f9;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  color: #64748b;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #e2e8f0;
}

.modal-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px; /* 统一区块间距 */
}

/* 基础信息区域 - 一行两列布局 */
.detail-section.basic-info {
  display: flex;
  gap: 24px;
}

.detail-section.basic-info .detail-item {
  flex: 1;
}

/* 通用标签样式 */
.detail-section label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 8px;
}

/* 基础信息文本样式 */
.detail-item span {
  font-weight: 600;
  color: #334155;
  font-size: 15px;
}

/* 反馈内容区域 */
.content-box {
  background: #f8fafc;
  padding: 18px;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  color: #475569;
  line-height: 1.6;
  white-space: pre-wrap;
  min-height: 100px;
}

/* 回复结果文本框 */
.modal-textarea {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 14px;
  font-family: inherit;
  font-size: 15px;
  transition: all 0.3s;
  box-sizing: border-box;
  background: #fff;
  color: #334155; /* 加深字体颜色 */
  font-weight: 500;
  min-height: 120px;
  resize: vertical; /* 允许垂直调整大小 */
}

.modal-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.modal-textarea:disabled {
  background: #f1f5f9;
  color: #475569;
  opacity: 1;
}

.modal-footer {
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 12px 24px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  color: #64748b;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #f8fafc;
}

.confirm-btn {
  padding: 12px 28px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.3s;
}

.confirm-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.required { color: #ef4444; margin-left: 4px; }

/* ========================================================================
   5. 动画
   ======================================================================== */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.4s ease;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-box {
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { transform: translateY(30px) scale(0.95); opacity: 0; }
  to { transform: translateY(0) scale(1); opacity: 1; }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .detail-section.basic-info {
    flex-direction: column;
    gap: 16px;
  }

  .filter-bar {
    gap: 20px;
  }

  .modal-box {
    max-width: 95%;
  }
}
</style>