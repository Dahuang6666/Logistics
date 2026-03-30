<template>
  <div class="dormitory-container">
    <!-- 页头 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">🏢 宿舍管理</h1>
        <p class="page-subtitle">管理楼宇信息及宿舍床位分配</p>
      </div>
      <button class="add-btn" @click="activeTab === 'build' ? openBuildDialog() : openDormDialog()">
        <span class="icon">+</span>
        {{ activeTab === 'build' ? '新增楼宇' : '新增宿舍' }}
      </button>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <button :class="['tab-item', activeTab === 'build' ? 'tab-active' : '']" @click="activeTab = 'build'">
        <span class="tab-icon">🏢</span> 楼宇管理
      </button>
      <button :class="['tab-item', activeTab === 'dorm' ? 'tab-active' : '']" @click="activeTab = 'dorm'">
        <span class="tab-icon">🛌</span> 宿舍列表
      </button>
      <div class="tab-indicator" :style="{ left: activeTab === 'build' ? '0%' : '50%' }"></div>
    </div>

    <!-- ===== 楼宇管理 Tab ===== -->
    <div v-show="activeTab === 'build'">
      <div class="filter-bar">
        <div class="search-wrapper">
          <input v-model="queryBuild.buildingNumber" placeholder="搜索楼号..." class="search-box" />
        </div>
        <button class="query-btn" @click="fetchBuilds">查询</button>
      </div>

      <div class="table-card">
        <table class="admin-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>楼号</th>
            <th>分配性别</th>
            <th>描述</th>
            <th style="text-align:center">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in buildList" :key="row.id">
            <td>{{ row.id }}</td>
            <td><span class="build-tag">{{ row.buildingNumber }}</span></td>
            <td>
                <span :class="['gender-badge', row.assignedGender === '男' ? 'gender-male' : 'gender-female']">
                  {{ row.assignedGender === '男' ? '♂ 男生楼' : '♀ 女生楼' }}
                </span>
            </td>
            <td class="desc-cell">{{ row.description || '-' }}</td>
            <td class="action-cells">
              <button class="btn-text delete" @click="handleDeleteBuild(row)">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-if="buildList.length === 0" class="empty-placeholder">
          <p>暂无楼宇数据</p>
        </div>
      </div>
    </div>

    <!-- ===== 宿舍列表 Tab ===== -->
    <div v-show="activeTab === 'dorm'">
      <div class="filter-bar">
        <div class="search-wrapper">
          <input v-model="queryDorm.dormitoryNo" placeholder="搜索宿舍号..." class="search-box" />
        </div>
        <select v-model="queryDorm.buildingId" class="filter-select">
          <option :value="null">全部楼宇</option>
          <option v-for="b in buildList" :key="b.id" :value="b.id">{{ b.buildingNumber }}</option>
        </select>
        <button class="query-btn" @click="fetchDorms">查询</button>
      </div>

      <div class="table-card">
        <table class="admin-table">
          <thead>
          <tr>
            <th>宿舍号</th>
            <th>所属楼宇</th>
            <th>床位占用</th>
            <th>入住率</th>
            <th style="text-align:center">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="row in dormList" :key="row.dormitoryId">
            <td><span class="dorm-no-tag">{{ row.dormitoryNo }}</span></td>
            <td><span class="build-tag">{{ getBuildNumber(row.buildingId) }}</span></td>
            <td>
                <span class="bed-info">
                  <span class="bed-used">{{ row.capacity - row.availableBeds }}</span>
                  <span class="bed-sep">/</span>
                  <span class="bed-total">{{ row.capacity }}</span>
                  <span class="bed-label">床</span>
                </span>
            </td>
            <td>
              <div class="progress-wrap">
                <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :style="{ width: getOccupancyRate(row) + '%' }"
                    :class="getOccupancyClass(row)"
                  ></div>
                </div>
                <span class="progress-text">{{ getOccupancyRate(row) }}%</span>
              </div>
            </td>
            <td class="action-cells">
              <div class="bed-btn-group">
                <button
                  class="bed-btn bed-minus"
                  :disabled="row.availableBeds >= row.capacity"
                  @click="handleBedChange(row, 1)"
                >-1</button>
                <button
                  class="bed-btn bed-plus"
                  :disabled="row.availableBeds <= 0"
                  @click="handleBedChange(row, -1)"
                >+1</button>
              </div>
              <button class="btn-text delete" @click="handleDeleteDorm(row)">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
        <div v-if="dormList.length === 0" class="empty-placeholder">
          <p>暂无宿舍数据</p>
        </div>
      </div>
    </div>

    <!-- ===== 新增楼宇 弹窗 ===== -->
    <Transition name="modal-fade">
      <div v-if="buildDialogVisible" class="modal-overlay" @click.self="buildDialogVisible = false">
        <div class="modal-box modal-box-sm">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag tag-add">新增</span>
              <h3 class="modal-title-text">新增楼宇</h3>
            </div>
            <button class="close-btn" @click="buildDialogVisible = false">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-grid">
              <div class="form-item full-width">
                <label>楼号 <span class="required">*</span></label>
                <input v-model="buildForm.buildingNumber" placeholder="如：10号楼" />
              </div>
              <div class="form-item full-width">
                <label>分配性别</label>
                <div class="radio-group">
                  <label class="radio-label">
                    <input type="radio" value="男" v-model="buildForm.assignedGender" /> 男生楼
                  </label>
                  <label class="radio-label">
                    <input type="radio" value="女" v-model="buildForm.assignedGender" /> 女生楼
                  </label>
                </div>
              </div>
              <div class="form-item full-width">
                <label>描述</label>
                <textarea v-model="buildForm.description" placeholder="楼宇备注信息（可选）" rows="3" class="modal-textarea"></textarea>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="buildDialogVisible = false">取消</button>
            <button class="confirm-btn" @click="submitBuild">确认新增</button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- ===== 新增宿舍 弹窗 ===== -->
    <Transition name="modal-fade">
      <div v-if="dormDialogVisible" class="modal-overlay" @click.self="dormDialogVisible = false">
        <div class="modal-box modal-box-sm">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag tag-add">新增</span>
              <h3 class="modal-title-text">新增宿舍</h3>
            </div>
            <button class="close-btn" @click="dormDialogVisible = false">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-grid">
              <div class="form-item full-width">
                <label>宿舍号 <span class="required">*</span></label>
                <input v-model="dormForm.dormitoryNo" placeholder="如：301" />
              </div>
              <div class="form-item full-width">
                <label>所属楼宇 <span class="required">*</span></label>
                <select v-model="dormForm.buildingId" class="modal-select">
                  <option :value="null" disabled>请选择楼宇</option>
                  <option v-for="b in buildList" :key="b.id" :value="b.id">{{ b.buildingNumber }}</option>
                </select>
              </div>
              <div class="form-item full-width">
                <label>容量（床位数）</label>
                <div class="number-input-wrap">
                  <button class="num-btn" @click="dormForm.capacity = Math.max(1, dormForm.capacity - 1)">−</button>
                  <span class="num-val">{{ dormForm.capacity }}</span>
                  <button class="num-btn" @click="dormForm.capacity = Math.min(12, dormForm.capacity + 1)">+</button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="dormDialogVisible = false">取消</button>
            <button class="confirm-btn" @click="submitDorm">确认新增</button>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script>
import {
  listBuild, deleteBuild, addBuild,
  listDorm, updateBeds, addDorm, deleteDorm
} from '@/utils/api.js'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'DormitoryManagement',
  data() {
    return {
      activeTab: 'build',
      buildList: [],
      dormList: [],
      queryBuild: { buildingNumber: '' },
      queryDorm: { dormitoryNo: '', buildingId: null },
      buildDialogVisible: false,
      dormDialogVisible: false,
      buildForm: { buildingNumber: '', assignedGender: '男', description: '', status: '正常' },
      dormForm: { dormitoryNo: '', buildingId: null, capacity: 4, status: '正常' }
    }
  },
  mounted() {
    this.fetchBuilds()
    this.fetchDorms()
  },
  methods: {
    async fetchBuilds() {
      try {
        const res = await listBuild(this.queryBuild)
        if (res.data && res.data.code === 1) this.buildList = res.data.data
      } catch (e) {
        ElMessage.error('加载楼宇失败')
      }
    },
    async fetchDorms() {
      try {
        const res = await listDorm(this.queryDorm)
        if (res.data && res.data.code === 1) this.dormList = res.data.data
      } catch (e) {
        ElMessage.error('加载宿舍失败')
      }
    },
    getBuildNumber(id) {
      const target = this.buildList.find(b => b.id === id)
      return target ? target.buildingNumber : '未知'
    },
    getOccupancyRate(row) {
      if (!row.capacity) return 0
      return Math.round(((row.capacity - row.availableBeds) / row.capacity) * 100)
    },
    getOccupancyClass(row) {
      const rate = this.getOccupancyRate(row)
      if (rate >= 90) return 'fill-red'
      if (rate >= 60) return 'fill-orange'
      return 'fill-green'
    },

    // 楼宇
    openBuildDialog() {
      this.buildForm = { buildingNumber: '', assignedGender: '男', description: '', status: '正常' }
      this.buildDialogVisible = true
    },
    async submitBuild() {
      if (!this.buildForm.buildingNumber) return ElMessage.warning('请输入楼号')
      try {
        const res = await addBuild(this.buildForm)
        if (res.data && res.data.code === 1) {
          ElMessage.success('楼宇新增成功')
          this.buildDialogVisible = false
          this.fetchBuilds()
        } else {
          ElMessage.error(res.data.msg || '新增失败')
        }
      } catch (e) {
        ElMessage.error('服务器错误')
      }
    },
    handleDeleteBuild(row) {
      ElMessageBox.confirm(
        `确定删除楼宇「${row.buildingNumber}」吗？\n该楼宇下所有宿舍将被级联清除！`,
        '危险操作',
        { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error' }
      ).then(async () => {
        try {
          const res = await deleteBuild(row.id)
          if (res.data && res.data.code === 1) {
            ElMessage.success('已删除')
            this.fetchBuilds()
            this.fetchDorms()
          } else {
            ElMessage.error(res.data.msg || '删除失败')
          }
        } catch (e) {
          ElMessage.error('服务器错误')
        }
      }).catch(() => {})
    },

    // 宿舍
    openDormDialog() {
      this.dormForm = { dormitoryNo: '', buildingId: null, capacity: 4, status: '正常' }
      this.dormDialogVisible = true
    },
    async submitDorm() {
      if (!this.dormForm.dormitoryNo) return ElMessage.warning('请输入宿舍号')
      if (!this.dormForm.buildingId) return ElMessage.warning('请选择所属楼宇')
      try {
        const res = await addDorm(this.dormForm)
        if (res.data && res.data.code === 1) {
          ElMessage.success('宿舍新增成功')
          this.dormDialogVisible = false
          this.fetchDorms()
        } else {
          ElMessage.error(res.data.msg || '新增失败')
        }
      } catch (e) {
        ElMessage.error('服务器错误')
      }
    },
    handleDeleteDorm(row) {
      ElMessageBox.confirm(`确定删除宿舍「${row.dormitoryNo}」吗？`, '删除确认', {
        confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteDorm(row.dormitoryId)
          if (res.data && res.data.code === 1) {
            ElMessage.success('已删除')
            this.fetchDorms()
          } else {
            ElMessage.error(res.data.msg || '删除失败')
          }
        } catch (e) {
          ElMessage.error('服务器错误')
        }
      }).catch(() => {})
    },
    async handleBedChange(row, amount) {
      try {
        const res = await updateBeds(row.dormitoryId, amount)
        if (res.data && res.data.code === 1) {
          ElMessage.success('床位已更新')
          this.fetchDorms()
        } else {
          ElMessage.error(res.data.msg || '更新失败')
        }
      } catch (e) {
        ElMessage.error('服务器错误')
      }
    }
  }
}
</script>

<style scoped>
.dormitory-container {
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
  margin: 0;
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
  font-size: 14px;
  transition: all 0.3s;
}
.add-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

/* Tab */
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
.tab-item.tab-active { color: #3498db; font-weight: 600; }
.tab-indicator {
  position: absolute;
  top: 5px; bottom: 5px;
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
.search-wrapper { max-width: 220px; }
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
}
.filter-select:focus { outline: none; border-color: #3498db; }
.query-btn {
  padding: 10px 18px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: 0.2s;
}
.query-btn:hover { background: #2980b9; }

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
.admin-table tbody tr:last-child td { border-bottom: none; }
.admin-table tbody tr:hover { background: #fafcff; }
.desc-cell { color: #888; font-size: 13px; max-width: 300px; }

/* 徽章 */
.build-tag {
  background: #f0f5ff;
  color: #4a6fa5;
  border: 1px solid #d6e4ff;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
}
.dorm-no-tag {
  background: #f6ffed;
  color: #389e0d;
  border: 1px solid #b7eb8f;
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
}
.gender-badge {
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}
.gender-male { background: #e6f7ff; color: #1890ff; border: 1px solid #91d5ff; }
.gender-female { background: #fff0f6; color: #eb2f96; border: 1px solid #ffadd2; }

/* 床位信息 */
.bed-info { display: flex; align-items: baseline; gap: 2px; }
.bed-used { font-size: 16px; font-weight: 700; color: #2c3e50; }
.bed-sep { color: #bbb; margin: 0 2px; }
.bed-total { font-size: 14px; color: #888; }
.bed-label { font-size: 12px; color: #bbb; margin-left: 2px; }

/* 进度条 */
.progress-wrap { display: flex; align-items: center; gap: 10px; min-width: 140px; }
.progress-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s ease;
}
.fill-green { background: #52c41a; }
.fill-orange { background: #fa8c16; }
.fill-red { background: #ff4d4f; }
.progress-text { font-size: 12px; color: #888; white-space: nowrap; }

/* 操作按钮 */
.action-cells {
  display: flex;
  align-items: center;
  gap: 8px;
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
.btn-text.delete { color: #ff4d4f; }
.btn-text.delete:hover { background: #fff1f0; }

/* 床位±按钮 */
.bed-btn-group { display: flex; gap: 2px; }
.bed-btn {
  width: 32px;
  height: 28px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: #555;
  transition: 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.bed-btn:hover:not(:disabled) { border-color: #3498db; color: #3498db; background: #e8f4fd; }
.bed-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.bed-minus:hover:not(:disabled) { border-color: #ff4d4f; color: #ff4d4f; background: #fff1f0; }

/* 空占位 */
.empty-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 160px;
}
.empty-placeholder p { color: #bbb; font-size: 14px; }

/* 弹窗 */
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
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px; }
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
.radio-group { display: flex; gap: 20px; padding: 4px 0; }
.radio-label { display: flex; align-items: center; gap: 6px; cursor: pointer; font-size: 14px; color: #444; }

/* 数字输入 */
.number-input-wrap {
  display: flex;
  align-items: center;
  gap: 0;
  border: 1px solid #ddd;
  border-radius: 7px;
  overflow: hidden;
  width: fit-content;
}
.num-btn {
  width: 38px;
  height: 38px;
  border: none;
  background: #f5f5f5;
  font-size: 18px;
  cursor: pointer;
  color: #555;
  transition: 0.2s;
}
.num-btn:hover { background: #e8f4fd; color: #3498db; }
.num-val {
  min-width: 48px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
  padding: 0 8px;
  line-height: 38px;
}

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

/* 动画 */
@keyframes modalSlideIn {
  from { opacity: 0; transform: translateY(-24px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}
.modal-fade-enter-active, .modal-fade-leave-active { transition: opacity 0.25s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
</style>