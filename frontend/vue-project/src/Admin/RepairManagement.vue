<template>
  <div class="user-management-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">🔧 宿舍报修管理</h1>
        <p class="page-subtitle">点击"查看详情"处理报修申请及查看现场照片</p>
      </div>
    </div>

    <div class="filter-bar">
      <div class="search-wrapper">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索学号或宿舍号..."
          class="search-box"
        >
      </div>
      <div class="status-filter">
        <select v-model="filterStatus" class="type-select">
          <option value="">全部状态</option>
          <option value="待处理">待处理</option>
          <option value="处理中">处理中</option>
          <option value="已完成">已完成</option>
        </select>
      </div>
    </div>

    <div class="table-card">
      <table class="admin-table">
        <thead>
        <tr>
          <th width="80">ID</th>
          <th width="120">报修人</th>
          <th width="100">宿舍</th>
          <th width="100">紧急度</th>
          <th width="120">当前状态</th>
          <th style="text-align: center" width="200">管理操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in filteredList" :key="item.id">
          <td>{{ item.id }}</td>
          <td><span class="user-no-tag">{{ item.userNo }}</span></td>
          <td><span class="dorm-tag">{{ dormNoMap[item.dormitoryId] || item.dormitoryId }}</span></td>
          <td>
              <span :class="['urgency-tag', getUrgencyClass(item.urgencyLevel)]">
                {{ item.urgencyLevel || '普通' }}
              </span>
          </td>
          <td>
            <div class="status-wrapper">
              <span :class="['status-dot', getStatusClass(item.status)]"></span>
              <span class="status-text">{{ item.status }}</span>
            </div>
          </td>
          <td class="action-cells">
            <button class="btn-text edit" @click="handleOpenDetail(item)">查看详情</button>
            <button class="btn-text delete" @click="confirmDelete(item)">删除记录</button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="filteredList.length === 0" class="empty-placeholder" style="text-align:center; display:flex; flex-direction:column; align-items:center; justify-content:center; padding: 60px 0; width:100%;">
        <div class="empty-icon" style="font-size:48px; margin-bottom:12px;">📭</div>
        <p>暂无符合条件的报修记录</p>
      </div>
    </div>

    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <div class="title-info">
              <span :class="['modal-tag-dot', getStatusClass(currentEdit.status)]"></span>
              <h3 class="modal-title">报修单详情 #{{ currentEdit.id }}</h3>
            </div>
            <button class="close-btn" @click="closeDialog">✕</button>
          </div>

          <div class="modal-body">
            <div class="info-grid">
              <div class="info-item">
                <label>报修用户</label>
                <p>{{ currentEdit.userNo }}</p>
              </div>
              <div class="info-item">
                <label>宿舍号</label>
                <p>
                  <span v-if="dormNoLoading" class="dorm-no-loading">加载中...</span>
                  <span v-else>{{ dormitoryNo || '-' }} 室</span>
                </p>
              </div>
              <div class="info-item">
                <label>报修类型</label>
                <p>{{ currentEdit.repairType }}</p>
              </div>
              <div class="info-item">
                <label>提交时间</label>
                <p>{{ formatDate(currentEdit.createTime) }}</p>
              </div>
            </div>

            <div class="detail-section">
              <label class="section-label">报修描述内容</label>
              <div class="description-content">
                {{ currentEdit.content }}
              </div>
            </div>

            <div class="detail-section">
              <label class="section-label">现场照片</label>
              <div class="image-gallery">
                <div v-if="currentEdit.imageUrl" class="img-wrapper">
                  <el-image
                    :src="currentEdit.imageUrl"
                    :preview-src-list="[currentEdit.imageUrl]"
                    fit="cover"
                    class="repair-img-preview"
                  >
                    <template #error>
                      <div class="img-error">图片加载失败</div>
                    </template>
                  </el-image>
                </div>
                <div v-else class="no-image-tip">该报修未上传相关图片</div>
              </div>
            </div>

            <div class="detail-section status-action-area" v-if="currentEdit.status !== '已完成'">
              <label class="section-label">更新处理进度 <span class="req">*</span></label>
              <div class="status-selector">
                <div
                  class="option-card"
                  :class="{ active: targetStatus === '处理中' }"
                  @click="targetStatus = '处理中'"
                >
                  <span class="dot blue"></span> 设为处理中
                </div>
                <div
                  class="option-card"
                  :class="{ active: targetStatus === '已完成' }"
                  @click="targetStatus = '已完成'"
                >
                  <span class="dot green"></span> 设为已完成
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn-secondary" @click="closeDialog">返回列表</button>
            <button
              v-if="currentEdit.status !== '已完成'"
              class="btn-primary"
              @click="submitStatusUpdate"
            >确认提交
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { getAllRepairs, updateRepairStatus, deleteRepair, getDormitoryNoById } from '@/utils/api.js'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'RepairManagement',
  data() {
    return {
      repairList: [],
      searchKeyword: '',
      filterStatus: '',
      showDialog: false,
      targetStatus: '',
      currentEdit: {},
      dormitoryNo: '',       // 当前弹窗展示的宿舍号
      dormNoLoading: false,  // 宿舍号加载状态
      dormNoMap: {}          // { dormitoryId: dormitoryNo } 缓存映射
    }
  },
  computed: {
    filteredList() {
      return this.repairList.filter(item => {
        const matchesSearch = !this.searchKeyword ||
          (item.userNo && item.userNo.includes(this.searchKeyword)) ||
          (this.dormNoMap[item.dormitoryId] && this.dormNoMap[item.dormitoryId].includes(this.searchKeyword))
        const matchesStatus = this.filterStatus === '' || item.status === this.filterStatus
        return matchesSearch && matchesStatus
      }).sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await getAllRepairs()
        if (res.data.code === 1) {
          this.repairList = res.data.data
          // 批量获取宿舍号，去重后并发请求
          const ids = [...new Set(this.repairList.map(r => r.dormitoryId).filter(Boolean))]
          const results = await Promise.allSettled(ids.map(id => getDormitoryNoById(id)))
          const map = {}
          results.forEach((result, index) => {
            if (result.status === 'fulfilled' && result.value.data.code === 1) {
              map[ids[index]] = result.value.data.data
            }
          })
          this.dormNoMap = map
        }
      } catch (e) {
        ElMessage.error('无法连接到服务器')
      }
    },
    getStatusClass(status) {
      if (status === '待处理') return 'dot-red'
      if (status === '处理中') return 'dot-blue'
      return 'dot-green'
    },
    getUrgencyClass(level) {
      if (level === '紧急' || level === '高') return 'u-high'
      if (level === '普通' || level === '中') return 'u-mid'
      return 'u-low'
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    },
    async handleOpenDetail(item) {
      this.currentEdit = { ...item }
      this.targetStatus = item.status === '待处理' ? '处理中' : item.status
      this.dormitoryNo = ''
      this.showDialog = true

      // 获取宿舍号
      if (item.dormitoryId) {
        this.dormNoLoading = true
        try {
          const res = await getDormitoryNoById(item.dormitoryId)
          if (res.data.code === 1) {
            this.dormitoryNo = res.data.data
          } else {
            this.dormitoryNo = item.dormitoryId // 兜底显示ID
          }
        } catch (e) {
          this.dormitoryNo = item.dormitoryId // 兜底显示ID
        } finally {
          this.dormNoLoading = false
        }
      }
    },
    closeDialog() {
      this.showDialog = false
    },
    async submitStatusUpdate() {
      try {
        const res = await updateRepairStatus(this.currentEdit.id, this.targetStatus)
        if (res.data.code === 1) {
          ElMessage.success('报修状态已更新')
          this.closeDialog()
          this.loadData()
        }
      } catch (e) {
        ElMessage.error('更新失败')
      }
    },
    confirmDelete(item) {
      ElMessageBox.confirm('确定要删除这条报修记录吗？', '操作警告', {
        type: 'warning',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }).then(async () => {
        const res = await deleteRepair(item.id)
        if (res.data.code === 1) {
          ElMessage.success('删除成功')
          this.loadData()
        }
      }).catch(() => {
      })
    }
  }
}
</script>

<style scoped>
/* 1. 基础容器与头部 */
.user-management-container {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.page-title {
  font-size: 24px;
  color: #1e293b;
  margin: 0;
}

.page-subtitle {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}

/* 2. 工具栏 */
.filter-bar {
  display: flex;
  gap: 20px;
  margin: 24px 0;
  align-items: center;
}

.search-box {
  flex: 0 1 350px;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  transition: all 0.3s;
}

.search-box:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  outline: none;
}

.type-select {
  padding: 11px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  color: #475569;
  cursor: pointer;
}

/* 3. 表格样式 */
.table-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background: #f1f5f9;
  padding: 16px;
  font-size: 13px;
  color: #475569;
  text-align: left;
}

.admin-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #334155;
}

.user-no-tag {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 4px 8px;
  border-radius: 6px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
}

.dorm-tag {
  font-weight: 700;
  color: #1e293b;
}

/* 状态点样式 */
.status-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot-red {
  background: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
}

.dot-blue {
  background: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.dot-green {
  background: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.15);
}

/* 紧急度标签 */
.urgency-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.u-high {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fee2e2;
}

.u-mid {
  background: #fffbeb;
  color: #d97706;
  border: 1px solid #fef3c7;
}

.u-low {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #dcfce7;
}

/* 按钮操作 */
.action-cells {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-text {
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 6px;
  transition: 0.2s;
}

.btn-text.edit {
  color: #2563eb;
}

.btn-text.edit:hover {
  background: #eff6ff;
}

.btn-text.delete {
  color: #dc2626;
}

.btn-text.delete:hover {
  background: #fef2f2;
}

/* 4. 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(6px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-box {
  background: white;
  width: 90%;
  max-width: 650px;
  border-radius: 24px;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.title-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-tag-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.modal-body {
  padding: 24px;
}

/* 栅格信息 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.info-item label {
  font-size: 12px;
  color: #64748b;
  display: block;
  margin-bottom: 4px;
}

.info-item p {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

/* 宿舍号加载状态 */
.dorm-no-loading {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 400;
  font-style: italic;
}

/* 详情描述 */
.section-label {
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  margin-bottom: 10px;
  display: block;
}

.description-content {
  background: #f8fafc;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  color: #334155;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
  margin-bottom: 24px;
}

/* 图片画廊 */
.image-gallery {
  margin-bottom: 24px;
}

.img-wrapper {
  width: 100%;
  max-width: 240px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.repair-img-preview {
  width: 100%;
  height: 160px;
  display: block;
}

.no-image-tip {
  color: #94a3b8;
  font-style: italic;
  font-size: 13px;
  padding: 10px 0;
}

/* 状态选择器 */
.status-selector {
  display: flex;
  gap: 12px;
}

.option-card {
  flex: 1;
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
  transition: 0.2s;
}

.option-card:hover {
  border-color: #3b82f6;
  background: #f0f7ff;
}

.option-card.active {
  border-color: #3b82f6;
  background: #3b82f6;
  color: white;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.blue {
  background: #3b82f6;
}

.dot.green {
  background: #10b981;
}

.option-card.active .dot {
  background: white;
}

/* 弹窗页脚 */
.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: #f8fafc;
}

.btn-secondary {
  padding: 10px 20px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: white;
  cursor: pointer;
  font-weight: 600;
  color: #64748b;
}

.btn-primary {
  padding: 10px 24px;
  border-radius: 10px;
  border: none;
  background: #3b82f6;
  color: white;
  cursor: pointer;
  font-weight: 700;
  transition: 0.3s;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* 动画 */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-box {
  animation: scaleUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes scaleUp {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

@media (max-width: 600px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .modal-box {
    width: 95%;
    margin: 10px;
  }

  .status-selector {
    flex-direction: column;
  }
}
</style>