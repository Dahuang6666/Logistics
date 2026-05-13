<template>
  <div class="user-management-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">🔁 宿舍调换审批</h1>
        <p class="page-subtitle">管理学生换宿申请，已自动匹配物理房间号</p>
      </div>
    </div>

    <div class="filter-bar">
      <div class="search-wrapper">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索学号、房号或原因..."
          class="search-box"
        />
      </div>
      <div class="status-filter">
        <select v-model="filterStatus" class="type-select" @change="loadData">
          <option value="全部">全部记录</option>
          <option value="待审批">待审批</option>
          <option value="已同意">已同意</option>
          <option value="已拒绝">已拒绝</option>
        </select>
      </div>
    </div>

    <div class="table-card">
      <table class="admin-table" v-if="filteredList.length > 0">
        <thead>
        <tr>
          <th width="120">申请人学号</th>
          <th width="220">调换路径 (原宿舍 -> 目标)</th>
          <th>申请原因摘要</th>
          <th width="120">当前状态</th>
          <th style="text-align: center" width="180">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in filteredList" :key="item.applicationId">
          <td><span class="user-no-tag">{{ item.studentNo }}</span></td>
          <td>
            <div class="path-inline">
              <span class="dorm-badge">{{ getDormNo(item.currentDormitoryId) }}</span>
              <span class="arrow-icon">→</span>
              <span class="dorm-badge target">{{ getDormNo(item.targetDormitoryId) }}</span>
            </div>
          </td>
          <td class="reason-cell">{{ truncate(item.reason) }}</td>
          <td>
            <div class="status-wrapper">
              <span :class="['status-dot', getStatusClass(item.status)]"></span>
              <span class="status-text">{{ formatStatus(item.status) }}</span>
            </div>
          </td>
          <td class="action-cells">
            <button class="btn-text edit" @click="handleOpenDetail(item)">
              {{ item.status === '待审批' ? '去审批' : '查看详情' }}
            </button>
          </td>
        </tr>
        </tbody>
      </table>
      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <div class="empty-text">暂无数据</div>
      </div>
    </div>

    

    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <div class="header-title-wrapper">
              <span :class="['header-dot', getStatusClass(currentEdit.status)]"></span>
              <h3 class="modal-title">调换申请单详情</h3>
            </div>
            <button class="close-icon-btn" @click="closeDialog">✕</button>
          </div>

          <div class="modal-body">
            <div class="info-grid">
              <div class="info-group">
                <label>申请学生</label>
                <p>{{ currentEdit.studentNo }}</p>
              </div>
              <div class="info-group">
                <label>申请时间</label>
                <p>{{ formatDate(currentEdit.applicationTime) }}</p>
              </div>
            </div>

            <div class="visual-path-card">
              <div class="node">
                <div class="node-label">当前所在房间</div>
                <div class="node-box">{{ getDormNo(currentEdit.currentDormitoryId) }}</div>
              </div>
              <div class="path-connector">
                <span class="connector-emoji">🚚</span>
              </div>
              <div class="node">
                <div class="node-label">目标调换房间</div>
                <div class="node-box target">{{ getDormNo(currentEdit.targetDormitoryId) }}</div>
              </div>
            </div>

            <!-- 新增：宿舍分配区域 -->
            <div v-if="currentEdit.status === '待审批'" class="dorm-assign-area">
              <div class="dorm-assign-header">
                <label class="block-title">分配目标宿舍</label>
                <button class="btn-edit-dorm" @click="startEditDorm" v-if="!isEditingDorm">
                  修改目标宿舍
                </button>
              </div>

              <div v-if="isEditingDorm" class="dorm-select-area">
                <div class="select-group">
                  <label>选择宿舍楼</label>
                  <select v-model="selectedBuildingId" class="type-select" @change="onBuildingChange(selectedBuildingId)">
                    <option :value="null">请选择宿舍楼</option>
                    <option v-for="build in availableBuildings" :key="build.id" :value="build.id">
                      {{ build.buildingNumber }} ({{ build.assignedGender }})
                    </option>
                  </select>
                </div>

                <div class="select-group" v-if="selectedBuildingId">
                  <label>选择目标宿舍</label>
                  <select v-model="selectedDormId" class="type-select">
                    <option :value="null">请选择宿舍</option>
                    <option v-for="dorm in availableDorms" :key="dorm.dormitoryId" :value="dorm.dormitoryId">
                      {{ dorm.dormitoryNo }} (剩余床位: {{ dorm.availableBeds }})
                    </option>
                  </select>
                </div>

                <div class="dorm-select-buttons">
                  <button class="btn-cancel" @click="cancelEditDorm">取消</button>
                  <button class="btn-approve" @click="confirmUpdateDorm">确认</button>
                </div>
              </div>
            </div>

            <div class="detail-block">
              <label class="block-title">详细申请理由</label>
              <div class="text-content">{{ currentEdit.reason }}</div>
            </div>

            <!-- 审批结果区域 -->
            <div class="detail-block" v-if="currentEdit.status !== '待审批' && currentEdit.comment">
              <label class="block-title">审批意见</label>
              <div class="text-content">{{ currentEdit.comment }}</div>
            </div>

            <div class="detail-block" v-if="currentEdit.status !== '待审批' && currentEdit.approvalTime">
              <label class="block-title">审批信息</label>
              <div class="text-content">
                审批人：{{ currentEdit.approverNo }} | 
                审批时间：{{ formatDate(currentEdit.approvalTime) }}
              </div>
            </div>

            <div class="approve-action-area" v-if="currentEdit.status === '待审批'">
              <label class="block-title">审批处理意见 <span class="required">*</span></label>
              <textarea v-model="approveComment" class="comment-input" placeholder="请填写意见..." rows="3"></textarea>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn-cancel" @click="closeDialog">返回</button>
            <template v-if="currentEdit.status === '待审批'">
              <button class="btn-reject" @click="submitStatus('已拒绝')">拒绝申请</button>
              <button class="btn-approve" @click="submitStatus('已同意')">同意换宿</button>
            </template>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
// 1. 引入 listDorm 接口
import {
    getApplicationList,
    updateApplicationStatus,
    listDorm,
    getAdminAvailableDorms,
    getAdminAvailableBuildings,
    updateApplicationTargetDorm,
    getApplicationById
} from '@/utils/api.js'
import { ElMessage } from 'element-plus'

export default {
  name: 'DormChangeManagement',
  data() {
    return {
      applicationList: [],
      dormMap: {},       // 用于存放 ID -> No 的映射关系 {1: "323", 2: "332"...}
      searchKeyword: '',
      filterStatus: '全部',
      showDialog: false,
      currentEdit: {},
      approveComment: '',
      availableBuildings: [],
      availableDorms: [],
      selectedBuildingId: null,
      selectedDormId: null,
      isEditingDorm: false
    }
  },
  computed: {
    filteredList() {
      return this.applicationList.filter(item => {
        const keyword = this.searchKeyword.toLowerCase()
        // 扩展搜索逻辑：支持按转换后的房号搜索
        const currentNo = this.getDormNo(item.currentDormitoryId)
        const targetNo = this.getDormNo(item.targetDormitoryId)

        return (item.studentNo && item.studentNo.toLowerCase().includes(keyword)) ||
          (item.reason && item.reason.toLowerCase().includes(keyword)) ||
          (currentNo.includes(keyword)) ||
          (targetNo.includes(keyword))
      })
    }
  },
  async mounted() {
    // 页面加载时，先获取宿舍列表建立映射，再获取申请数据
    await this.fetchDormMap()
    this.loadData()
  },
  methods: {
    // 2. 获取宿舍列表并建立映射
    async fetchDormMap() {
      try {
        const res = await listDorm()
        if (res.data.code === 1) {
          const map = {}
          res.data.data.forEach(dorm => {
            map[dorm.dormitoryId] = dorm.dormitoryNo
          })
          this.dormMap = map
        }
      } catch (e) {
        console.error('映射宿舍列表失败', e)
      }
    },
    getDormNo(id) {
      // 如果 ID 是 0 或者不存在，显示“等待分配”
      if (id === 0 || id === '0' || !id) {
        return '待分配'
      }
      // 否则从映射表中取房号，取不到则返回原 ID
      return this.dormMap[id] || `ID:${id}`
    },
    
    // 获取可用宿舍楼
    async fetchAvailableBuildings() {
      try {
        if (!this.currentEdit.studentNo) {
          console.error('学生学号为空:', this.currentEdit)
          return
        }
        console.log('开始获取宿舍楼,学生学号:', this.currentEdit.studentNo)
        const res = await getAdminAvailableBuildings(this.currentEdit.studentNo)
        console.log('获取宿舍楼结果:', res)
        if (res.data.code === 1) {
          this.availableBuildings = res.data.data
          console.log('可用宿舍楼:', this.availableBuildings)
        } else {
          console.error('获取宿舍楼失败:', res.data.msg)
        }
      } catch (e) {
        console.error('获取宿舍楼失败', e)
      }
    },

    // 获取可用宿舍
    async fetchAvailableDorms(buildingId) {
      try {
        console.log('开始获取宿舍,楼栋ID:', buildingId)
        const res = await getAdminAvailableDorms(buildingId)
        console.log('获取宿舍结果:', res)
        if (res.data.code === 1) {
          this.availableDorms = res.data.data
          console.log('可用宿舍:', this.availableDorms)
        } else {
          console.error('获取宿舍失败:', res.data.msg)
        }
      } catch (e) {
        console.error('获取可用宿舍失败', e)
      }
    },

    // 宿舍楼变化
    onBuildingChange(buildingId) {
      this.selectedDormId = null
      this.fetchAvailableDorms(buildingId)
    },

    // 开始编辑宿舍
    startEditDorm() {
      this.isEditingDorm = true
      this.fetchAvailableBuildings()
    },

    // 确认修改宿舍
    async confirmUpdateDorm() {
      if (!this.selectedDormId) {
        ElMessage.warning('请选择目标宿舍')
        return
      }
      try {
        const res = await updateApplicationTargetDorm(
            this.currentEdit.applicationId,
            this.selectedDormId
        )
        if (res.data.code === 1) {
          ElMessage.success('目标宿舍更新成功')
          this.currentEdit.targetDormitoryId = this.selectedDormId
          this.isEditingDorm = false
          this.loadData()
        }
      } catch (e) {
        ElMessage.error('更新目标宿舍失败')
      }
    },

    // 取消编辑宿舍
    cancelEditDorm() {
      this.isEditingDorm = false
      this.selectedBuildingId = null
      this.selectedDormId = null
    },
    // 在 methods 中添加或修改
    formatStatus(status) {
      const statusMap = {
        'PENDING': '待审批',
        'APPROVED': '已同意',
        'REJECTED': '已拒绝'
      }
      // 如果匹配不到（比如后端直接传了中文），则原样返回
      return statusMap[status] || status
    },

    async loadData() {
      try {
        const res = await getApplicationList(this.filterStatus)
        if (res.data.code === 1) {
          this.applicationList = res.data.data
        }
      } catch (e) {
        ElMessage.error('获取申请数据失败')
      }
    },

    getStatusClass(status) {
      const classes = { '待审批': 'bg-blue', '已同意': 'bg-green', '已拒绝': 'bg-red' }
      return classes[status] || ''
    },

    handleOpenDetail(item) {
      this.currentEdit = { ...item }
      this.approveComment = ''
      this.showDialog = true
      this.fetchApplicationDetail(item.applicationId)
    },

    async fetchApplicationDetail(applicationId) {
      try {
        const res = await getApplicationById(applicationId)
        if (res.data.code === 1 && res.data.data) {
          this.currentEdit = { ...this.currentEdit, ...res.data.data }
        }
      } catch (e) {
        console.error('获取申请详情失败', e)
      }
    },

    async submitStatus(targetStatus) {
      const approverNo = sessionStorage.getItem('userNo')
      if (!approverNo) return ElMessage.error('登录失效')
      if (targetStatus === '已拒绝' && !this.approveComment) return ElMessage.warning('请填写原因')

      try {
        const res = await updateApplicationStatus({
          applicationId: this.currentEdit.applicationId,
          status: targetStatus,
          approverNo,
          comment: this.approveComment
        })
        if (res.data.code === 1) {
          ElMessage.success(`已操作：${targetStatus}`)
          this.closeDialog()
          this.loadData()
        }
      } catch (e) {
        ElMessage.error('提交失败')
      }
    },

    closeDialog() {
      this.showDialog = false
    },
    formatDate(d) {
      return d ? new Date(d).toLocaleString() : '-'
    },
    truncate(t) {
      return t && t.length > 25 ? t.substring(0, 25) + '...' : t
    }
  }
}
</script>

<style scoped>
/* 核心容器 */
.user-management-container {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
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
  margin-top: 4px;
}

/* 工具栏 */
.filter-bar {
  display: flex;
  gap: 20px;
  margin: 24px 0;
}

.search-box {
  flex: 0 1 350px;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.type-select {
  padding: 11px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  cursor: pointer;
}

/* 表格样式 */
.table-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  color: #94a3b8;
  font-size: 16px;
}

.admin-table th {
  background: #f8fafc;
  padding: 16px;
  font-size: 13px;
  color: #64748b;
  text-align: left;
  border-bottom: 2px solid #f1f5f9;
}

.admin-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #334155;
}

/* 路径内联显示 */
.path-inline {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dorm-badge {
  background: #ffffff;
  padding: 4px 12px;
  border-radius: 8px;
  font-weight: 800;
  color: #1e293b;
  border: 1px solid #e2e8f0;
  min-width: 60px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.dorm-badge.target {
  background: #10b981;
  color: white;
  border-color: #059669;
}

.arrow-icon {
  color: #94a3b8;
  font-weight: bold;
}

/* 状态样式 */
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

.bg-blue {
  background: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.bg-green {
  background: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.bg-red {
  background: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.user-no-tag {
  background: #eff6ff;
  color: #1d4ed8;
  padding: 4px 8px;
  border-radius: 6px;
  font-family: monospace;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-box {
  background: white;
  width: 90%;
  max-width: 600px;
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.modal-title {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}

.close-icon-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #94a3b8;
  cursor: pointer;
}

.modal-body {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.info-grid.mini {
  margin-top: 15px;
  background: #f8fafc;
  padding: 12px;
  border-radius: 12px;
}

.info-group label {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

.info-group p {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

/* 可视化路径卡片 */
.visual-path-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f8fafc;
  padding: 24px;
  border-radius: 20px;
  margin-bottom: 24px;
  border: 1px dashed #cbd5e1;
}

.node {
  text-align: center;
}

.node-label {
  font-size: 11px;
  color: #94a3b8;
  margin-bottom: 8px;
  text-transform: uppercase;
}

.node-box {
  background: white;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 20px;
  font-weight: 800;
  color: #1e293b;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.node-box.target {
  background: #10b981;
  color: white;
}

.connector-line {
  flex: 1;
  height: 2px;
  background: #e2e8f0;
  margin: 0 15px;
  position: relative;
}

.connector-emoji {
  font-size: 24px;
  margin: 0 10px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(10px);
  }
}

.block-title {
  font-size: 14px;
  font-weight: 700;
  color: #475569;
  margin-bottom: 10px;
  display: block;
}

.text-content {
  background: #f1f5f9;
  padding: 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
}

.text-content.feedback {
  border-left: 4px solid #10b981;
  background: #f0fdf4;
}

.comment-input {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
  font-family: inherit;
  resize: none;
  margin-top: 8px;
  outline: none;
}

.comment-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 页脚 */
.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: #f8fafc;
}

.btn-cancel {
  padding: 10px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  font-weight: 600;
}

.btn-reject {
  padding: 10px 20px;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
}

.btn-approve {
  padding: 10px 24px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;
}

.action-cells .btn-text {
  background: none;
  border: none;
  color: #2563eb;
  font-weight: 700;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
}

.action-cells .btn-text:hover {
  background: #eff6ff;
}

/* 版本弹窗样式 */
.version-modal {
  max-width: 450px;
}

.version-content {
  text-align: center;
}

.version-content h4 {
  font-size: 18px;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.version-date {
  color: #94a3b8;
  font-size: 14px;
  margin: 0 0 20px 0;
}

.version-changes {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;
}

.version-changes li {
  padding: 8px 12px;
  background: #f8fafc;
  margin-bottom: 8px;
  border-radius: 8px;
  color: #475569;
  font-size: 14px;
}

.version-changes li:last-child {
  margin-bottom: 0;
}

/* 宿舍分配区域样式 */
.dorm-assign-area {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.dorm-assign-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.btn-edit-dorm {
  background: white;
  border: 1px solid #3b82f6;
  color: #3b82f6;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.btn-edit-dorm:hover {
  background: #eff6ff;
}

.dorm-select-area {
  border-top: 1px solid #e2e8f0;
  padding-top: 12px;
}

.select-group {
  margin-bottom: 12px;
}

.select-group label {
  display: block;
  font-size: 14px;
  color: #475569;
  margin-bottom: 6px;
}

.dorm-select-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

/* 动画 */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}
</style>