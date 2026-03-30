<template>
  <div class="announcement-container">
    <!-- 页头 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">📢 公告管理</h1>
        <p class="page-subtitle">管理校园公告内容及公告分类</p>
      </div>
      <button class="add-btn" @click="activeTab === 'announcement' ? openAddAnnouncementDialog() : openAddTypeDialog()">
        <span class="icon">+</span>
        {{ activeTab === 'announcement' ? '发布新公告' : '新增分类' }}
      </button>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <button
        :class="['tab-item', activeTab === 'announcement' ? 'tab-active' : '']"
        @click="activeTab = 'announcement'"
      >
        <span class="tab-icon">📋</span> 公告列表
      </button>
      <button
        :class="['tab-item', activeTab === 'type' ? 'tab-active' : '']"
        @click="activeTab = 'type'"
      >
        <span class="tab-icon">🗂️</span> 公告分类
      </button>
      <div class="tab-indicator" :style="{ left: activeTab === 'announcement' ? '0%' : '50%' }"></div>
    </div>

    <!-- ===== 公告列表 Tab ===== -->
    <div v-show="activeTab === 'announcement'">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="search-wrapper">
          <input
            type="text"
            v-model="announcementSearch"
            placeholder="搜索公告标题或发布人..."
            class="search-box"
          />
        </div>
        <select v-model="filterPriority" class="filter-select">
          <option :value="null">全部优先级</option>
          <option :value="3">🔴 最高优先级</option>
          <option :value="2">🟡 中等优先级</option>
          <option :value="1">🟢 普通优先级</option>
        </select>
      </div>

      <div class="table-card">
        <table class="admin-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>分类</th>
            <th>优先级</th>
            <th>发布人</th>
            <th>发布时间</th>
            <th style="text-align:center">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in paginatedAnnouncements" :key="item.id">
            <td>{{ item.id }}</td>
            <td class="title-cell" @click="openDetailDialog(item)">
              <span class="title-link">{{ item.title }}</span>
            </td>
            <td>
              <span class="type-badge">{{ item.announcementTypeName || '未知类型' }}</span>
            </td>
            <td>
                <span :class="['priority-badge', `priority-${item.priority}`]">
                  {{ priorityLabel(item.priority) }}
                </span>
            </td>
            <td>{{ item.publisher || '-' }}</td>
            <td>{{ formatDate(item.publishTime) }}</td>
            <td class="action-cells">
              <button class="btn-text edit" @click="handleEditAnnouncement(item)">编辑</button>
              <button class="btn-text delete" @click="handleDeleteAnnouncement(item)">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-if="filteredAnnouncements.length === 0" class="empty-placeholder">
          <p>暂无符合条件的公告</p>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="filteredAnnouncements.length > 0">
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">‹</button>
          <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ filteredAnnouncements.length }} 条</span>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">›</button>
        </div>
      </div>
    </div>

    <!-- ===== 公告分类 Tab ===== -->
    <div v-show="activeTab === 'type'">
      <div class="table-card">
        <table class="admin-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>分类名称</th>
            <th>优先级</th>
            <th>创建时间</th>
            <th style="text-align:center">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="type in typeList" :key="type.id">
            <td>{{ type.id }}</td>
            <td>
              <span class="type-name">{{ type.typeName }}</span>
              <span v-if="type.id === 1" class="system-tag">系统内置</span>
            </td>
            <td>
                <span :class="['priority-badge', `priority-${type.priority}`]">
                  {{ priorityLabel(type.priority) }}
                </span>
            </td>
            <td>{{ formatDate(type.createdDate) }}</td>
            <td class="action-cells">
              <button class="btn-text edit" @click="handleEditType(type)">编辑</button>
              <button
                class="btn-text delete"
                :disabled="type.id === 1"
                :style="type.id === 1 ? 'opacity:0.3;cursor:not-allowed' : ''"
                @click="type.id !== 1 && handleDeleteType(type)"
              >删除</button>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-if="typeList.length === 0" class="empty-placeholder">
          <p>暂无分类数据</p>
        </div>
      </div>
    </div>

    <!-- ===== 公告新增/编辑 弹窗 ===== -->
    <Transition name="modal-fade">
      <div v-if="showAnnouncementDialog" class="modal-overlay" @click.self="showAnnouncementDialog = false">
        <div class="modal-box">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag" :class="isEditAnnouncement ? 'tag-edit' : 'tag-add'">
                {{ isEditAnnouncement ? '编辑' : '新增' }}
              </span>
              <h3 class="modal-title-text">{{ isEditAnnouncement ? '编辑公告' : '发布新公告' }}</h3>
            </div>
            <button class="close-btn" @click="showAnnouncementDialog = false">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-grid">
              <div class="form-item full-width">
                <label>公告标题 <span class="required">*</span></label>
                <input v-model="announcementForm.title" placeholder="请输入公告标题" :class="{'input-error': announcementErrors.title}" />
                <span class="error-msg">{{ announcementErrors.title }}</span>
              </div>
              <div class="form-item">
                <label>发布人 <span class="required">*</span></label>
                <input v-model="announcementForm.publisher" placeholder="请输入发布人姓名" :class="{'input-error': announcementErrors.publisher}" />
                <span class="error-msg">{{ announcementErrors.publisher }}</span>
              </div>
              <div class="form-item">
                <label>公告分类 <span class="required">*</span></label>
                <select v-model="announcementForm.announcementTypeId" class="modal-select">
                  <option v-for="t in typeList" :key="t.id" :value="t.id">{{ t.typeName }}</option>
                </select>
              </div>
              <div class="form-item full-width">
                <label>公告内容 <span class="required">*</span></label>
                <textarea v-model="announcementForm.content" placeholder="请输入公告内容..." rows="6" :class="['modal-textarea', {'input-error': announcementErrors.content}]"></textarea>
                <span class="error-msg">{{ announcementErrors.content }}</span>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="showAnnouncementDialog = false">取消</button>
            <button class="confirm-btn" @click="submitAnnouncement">确认发布</button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- ===== 公告详情 弹窗 ===== -->
    <Transition name="modal-fade">
      <div v-if="showDetailDialog" class="modal-overlay" @click.self="showDetailDialog = false">
        <div class="modal-box detail-box">
          <div class="modal-header">
            <div class="title-area">
              <span :class="['priority-badge', `priority-${detailItem.priority}`]">
                {{ priorityLabel(detailItem.priority) }}
              </span>
              <span class="type-badge">{{ detailItem.announcementTypeName }}</span>
            </div>
            <button class="close-btn" @click="showDetailDialog = false">✕</button>
          </div>
          <div class="modal-content">
            <h2 class="detail-title">{{ detailItem.title }}</h2>
            <div class="detail-meta">
              <span>👤 {{ detailItem.publisher }}</span>
              <span>🕐 {{ formatDate(detailItem.publishTime) }}</span>
            </div>
            <div class="detail-divider"></div>
            <p class="detail-content">{{ detailItem.content }}</p>
          </div>
        </div>
      </div>
    </Transition>

    <!-- ===== 分类新增/编辑 弹窗 ===== -->
    <Transition name="modal-fade">
      <div v-if="showTypeDialog" class="modal-overlay" @click.self="showTypeDialog = false">
        <div class="modal-box modal-box-sm">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag" :class="isEditType ? 'tag-edit' : 'tag-add'">
                {{ isEditType ? '编辑' : '新增' }}
              </span>
              <h3 class="modal-title-text">{{ isEditType ? '编辑分类' : '新增公告分类' }}</h3>
            </div>
            <button class="close-btn" @click="showTypeDialog = false">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-grid">
              <div class="form-item full-width">
                <label>分类名称 <span class="required">*</span></label>
                <input
                  v-model="typeForm.typeName"
                  placeholder="请输入分类名称"
                  :disabled="isEditType && typeForm.id === 1"
                  :class="{'input-error': typeErrors.typeName}"
                />
                <span class="error-msg">{{ typeErrors.typeName }}</span>
              </div>
              <div class="form-item full-width">
                <label>优先级</label>
                <div class="priority-radio-group">
                  <label v-for="p in [1,2,3]" :key="p" class="priority-radio-label">
                    <input type="radio" :value="p" v-model="typeForm.priority" />
                    <span :class="['priority-badge', `priority-${p}`]">{{ priorityLabel(p) }}</span>
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="showTypeDialog = false">取消</button>
            <button class="confirm-btn" @click="submitType">确认保存</button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAnnouncementList,
  addAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  getAllTypes,
  addAnnouncementType as addType,
  updateAnnouncementType as updateType,
  deleteAnnouncementType as deleteType
} from '@/utils/api.js'

export default {
  name: 'AnnouncementManagement',
  data() {
    return {
      activeTab: 'announcement',

      // 公告列表
      announcementList: [],
      announcementSearch: '',
      filterPriority: null,
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,

      // 分类列表
      typeList: [],

      // 公告弹窗
      showAnnouncementDialog: false,
      isEditAnnouncement: false,
      announcementForm: { id: null, title: '', content: '', publisher: '', announcementTypeId: null },
      announcementErrors: { title: '', content: '', publisher: '' },

      // 详情弹窗
      showDetailDialog: false,
      detailItem: {},

      // 分类弹窗
      showTypeDialog: false,
      isEditType: false,
      typeForm: { id: null, typeName: '', priority: 1 },
      typeErrors: { typeName: '' },
    }
  },
  computed: {
    filteredAnnouncements() {
      const k = this.announcementSearch.toLowerCase()
      return this.announcementList.filter(a => {
        const matchSearch = !k ||
          (a.title && a.title.toLowerCase().includes(k)) ||
          (a.publisher && a.publisher.toLowerCase().includes(k))
        const matchPriority = this.filterPriority === null || a.priority === this.filterPriority
        return matchSearch && matchPriority
      })
    },
    totalPages() {
      return Math.max(1, Math.ceil(this.filteredAnnouncements.length / this.pageSize))
    },
    paginatedAnnouncements() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.filteredAnnouncements.slice(start, start + this.pageSize)
    }
  },
  watch: {
    announcementSearch() { this.currentPage = 1 },
    filterPriority() { this.currentPage = 1 }
  },
  mounted() {
    this.loadAnnouncements()
    this.loadTypes()
  },
  methods: {
    async loadAnnouncements() {
      try {
        const res = await getAnnouncementList(1, 999, null, null)
        if (res.data.code === 1) {
          this.announcementList = res.data.data.list || []
          this.totalCount = res.data.data.total || 0
        }
      } catch (e) {
        ElMessage.error('加载公告失败，无法连接服务器')
      }
    },
    async loadTypes() {
      try {
        const res = await getAllTypes()
        if (res.data.code === 1) this.typeList = res.data.data || []
      } catch (e) {
        ElMessage.error('加载分类失败')
      }
    },
    priorityLabel(p) {
      return p === 3 ? '最高' : p === 2 ? '中等' : '普通'
    },
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },

    // ---- 公告操作 ----
    openAddAnnouncementDialog() {
      this.isEditAnnouncement = false
      this.announcementErrors = { title: '', content: '', publisher: '' }
      this.announcementForm = { id: null, title: '', content: '', publisher: '', announcementTypeId: this.typeList[0]?.id || null }
      this.showAnnouncementDialog = true
    },
    handleEditAnnouncement(item) {
      this.isEditAnnouncement = true
      this.announcementErrors = { title: '', content: '', publisher: '' }
      this.announcementForm = { ...item }
      this.showAnnouncementDialog = true
    },
    openDetailDialog(item) {
      this.detailItem = item
      this.showDetailDialog = true
    },
    validateAnnouncement() {
      let valid = true
      this.announcementErrors = { title: '', content: '', publisher: '' }
      if (!this.announcementForm.title) { this.announcementErrors.title = '标题不能为空'; valid = false }
      if (!this.announcementForm.content) { this.announcementErrors.content = '内容不能为空'; valid = false }
      if (!this.announcementForm.publisher) { this.announcementErrors.publisher = '发布人不能为空'; valid = false }
      return valid
    },
    async submitAnnouncement() {
      if (!this.validateAnnouncement()) return
      try {
        const res = this.isEditAnnouncement
          ? await updateAnnouncement(this.announcementForm)
          : await addAnnouncement(this.announcementForm)
        if (res.data.code === 1) {
          ElMessage.success(this.isEditAnnouncement ? '公告已更新' : '公告发布成功')
          this.showAnnouncementDialog = false
          this.loadAnnouncements()
        } else {
          ElMessage.error(res.data.msg || '操作失败')
        }
      } catch (e) {
        ElMessage.error('服务器错误')
      }
    },
    handleDeleteAnnouncement(item) {
      ElMessageBox.confirm(`确定要删除公告「${item.title}」吗？`, '删除确认', {
        confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteAnnouncement(item.id)
          if (res.data.code === 1) {
            ElMessage.success('公告已删除')
            this.loadAnnouncements()
          } else {
            ElMessage.error(res.data.msg || '删除失败')
          }
        } catch (e) {
          ElMessage.error('服务器错误')
        }
      }).catch(() => {})
    },

    // ---- 分类操作 ----
    openAddTypeDialog() {
      this.isEditType = false
      this.typeErrors = { typeName: '' }
      this.typeForm = { id: null, typeName: '', priority: 1 }
      this.showTypeDialog = true
    },
    handleEditType(type) {
      this.isEditType = true
      this.typeErrors = { typeName: '' }
      this.typeForm = { ...type }
      this.showTypeDialog = true
    },
    validateType() {
      this.typeErrors = { typeName: '' }
      if (!this.typeForm.typeName) { this.typeErrors.typeName = '分类名称不能为空'; return false }
      return true
    },
    async submitType() {
      if (!this.validateType()) return
      try {
        const res = this.isEditType
          ? await updateType(this.typeForm)
          : await addType(this.typeForm)
        if (res.data.code === 1) {
          ElMessage.success(this.isEditType ? '分类已更新' : '分类新增成功')
          this.showTypeDialog = false
          this.loadTypes()
          this.loadAnnouncements()
        } else {
          ElMessage.error(res.data.msg || '操作失败')
        }
      } catch (e) {
        ElMessage.error('服务器错误')
      }
    },
    handleDeleteType(type) {
      ElMessageBox.confirm(
        `确定删除分类「${type.typeName}」吗？\n该分类下的公告将自动迁移至「未知类型」`,
        '删除确认',
        { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' }
      ).then(async () => {
        try {
          const res = await deleteType(type.id)
          if (res.data.code === 1) {
            ElMessage.success('分类已删除')
            this.loadTypes()
            this.loadAnnouncements()
          } else {
            ElMessage.error(res.data.msg || '删除失败')
          }
        } catch (e) {
          ElMessage.error('服务器错误')
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.announcement-container {
  padding: 20px;
  background: #fcfcfc;
  min-height: 100%;
}

/* 页头 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}
.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}
.page-subtitle {
  color: #888;
  font-size: 14px;
  margin-top: 5px;
}
.add-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  font-size: 14px;
}
.add-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

/* Tab 栏 */
.tab-bar {
  position: relative;
  display: flex;
  background: white;
  border-radius: 10px;
  padding: 5px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  border: 1px solid #eee;
  max-width: 320px;
}
.tab-item {
  flex: 1;
  padding: 10px 0;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #999;
  border-radius: 7px;
  transition: color 0.3s;
  z-index: 1;
  position: relative;
}
.tab-item.tab-active {
  color: #3498db;
  font-weight: 600;
}
.tab-indicator {
  position: absolute;
  top: 5px;
  bottom: 5px;
  width: 50%;
  background: #e8f4fd;
  border-radius: 7px;
  transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}
.tab-icon { margin-right: 4px; }

/* 筛选栏 */
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  align-items: center;
}
.search-wrapper {
  flex: 1;
  max-width: 360px;
}
.search-box {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 13px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}
.search-box:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52,152,219,0.1);
}
.filter-select {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 13px;
  color: #555;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s;
}
.filter-select:focus {
  outline: none;
  border-color: #3498db;
}

/* 表格 */
.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  overflow: hidden;
  border: 1px solid #eee;
}
.admin-table {
  width: 100%;
  border-collapse: collapse;
}
.admin-table th {
  background: #f8f9fb;
  padding: 14px 16px;
  text-align: left;
  color: #666;
  font-size: 13px;
  font-weight: 600;
}
.admin-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #333;
}
.admin-table tbody tr:last-child td {
  border-bottom: none;
}
.admin-table tbody tr:hover {
  background: #fafcff;
}

/* 标题可点击 */
.title-cell { cursor: pointer; max-width: 260px; }
.title-link {
  color: #2c3e50;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
}
.title-link:hover { color: #3498db; text-decoration: underline; }

/* 徽章 */
.type-badge {
  background: #f0f5ff;
  color: #4a6fa5;
  border: 1px solid #d6e4ff;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}
.priority-badge {
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.priority-3 { background: #fff1f0; color: #cf1322; border: 1px solid #ffa39e; }
.priority-2 { background: #fffbe6; color: #d48806; border: 1px solid #ffe58f; }
.priority-1 { background: #f6ffed; color: #389e0d; border: 1px solid #b7eb8f; }

/* 分类名称 */
.type-name { font-weight: 500; color: #2c3e50; margin-right: 6px; }
.system-tag {
  background: #f5f5f5;
  color: #999;
  border: 1px solid #ddd;
  padding: 2px 7px;
  border-radius: 3px;
  font-size: 11px;
}

/* 操作按钮 */
.action-cells {
  display: flex;
  gap: 6px;
  justify-content: center;
}
.btn-text {
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 500;
  padding: 5px 10px;
  border-radius: 5px;
  transition: 0.2s;
  font-size: 13px;
}
.btn-text.edit { color: #1890ff; }
.btn-text.edit:hover { background: #e6f7ff; }
.btn-text.delete { color: #ff4d4f; }
.btn-text.delete:hover { background: #fff1f0; }

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}
.page-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 18px;
  color: #555;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}
.page-btn:hover:not(:disabled) { border-color: #3498db; color: #3498db; }
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.page-info { font-size: 13px; color: #888; }

/* 空占位 */
.empty-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 180px;
}
.empty-placeholder p { color: #bbb; font-size: 14px; }

/* 弹窗公共 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.55);
  backdrop-filter: blur(3px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}
.modal-box {
  background: white;
  border-radius: 14px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.2);
  width: 100%;
  max-width: 620px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modalSlideIn 0.28s ease;
}
.modal-box-sm { max-width: 420px; }
.detail-box { max-width: 600px; }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1px solid #e8e8e8;
}
.title-area { display: flex; align-items: center; gap: 10px; }
.modal-tag {
  padding: 4px 12px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  font-weight: bold;
}
.tag-add { background: #27ae60; }
.tag-edit { background: #3498db; }
.modal-title-text { font-size: 17px; font-weight: bold; color: #2c3e50; margin: 0; }
.close-btn {
  width: 30px; height: 30px;
  border: none;
  background: #f0f0f0;
  color: #666;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
  display: flex; align-items: center; justify-content: center;
}
.close-btn:hover { background: #e0e0e0; transform: rotate(90deg); }

.modal-content { padding: 22px 24px; overflow-y: auto; flex: 1; }

/* 表单 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}
.full-width { grid-column: span 2; }
.form-item { display: flex; flex-direction: column; gap: 7px; }
.form-item label { font-size: 13px; font-weight: 600; color: #555; }
.required { color: #e74c3c; }
.form-item input, .modal-select, .modal-textarea {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 7px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
}
.modal-textarea { resize: vertical; }
.form-item input:focus, .modal-select:focus, .modal-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52,152,219,0.1);
}
.input-error { border-color: #e74c3c !important; }
.error-msg { font-size: 11px; color: #e74c3c; min-height: 13px; }

/* 优先级单选 */
.priority-radio-group { display: flex; gap: 16px; flex-wrap: wrap; }
.priority-radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}
.priority-radio-label input { cursor: pointer; }

/* 弹窗底部 */
.modal-footer {
  padding: 14px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}
.cancel-btn {
  padding: 9px 20px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 10px;
  font-size: 14px;
  transition: 0.2s;
}
.cancel-btn:hover { background: #f5f5f5; }
.confirm-btn {
  padding: 9px 24px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  font-size: 14px;
  transition: 0.3s;
}
.confirm-btn:hover { background: #2980b9; }

/* 详情弹窗 */
.detail-title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 12px 0;
  line-height: 1.4;
}
.detail-meta {
  display: flex;
  gap: 20px;
  color: #888;
  font-size: 13px;
  margin-bottom: 16px;
}
.detail-divider {
  height: 1px;
  background: #f0f0f0;
  margin-bottom: 16px;
}
.detail-content {
  color: #444;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
  margin: 0;
}

/* 动画 */
@keyframes modalSlideIn {
  from { opacity: 0; transform: translateY(-24px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

</style>