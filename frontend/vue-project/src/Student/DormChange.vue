<template>
  <div class="dorm-change-container">
    <div class="page-header">
      <h1 class="page-title">🔄 宿舍变更申请</h1>
      <p class="subtitle">请详细说明您的换宿原因，我们会尽快为您处理</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 申请表单 -->
    <div v-else class="form-card">
      <div class="form-section">
        <h3 class="section-title">📋 基本信息</h3>
        <div class="info-display">
          <div class="info-item">
            <span class="label">学号:</span>
            <span class="value">{{ userNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">姓名:</span>
            <span class="value">{{ userName }}</span>
          </div>
          <div class="info-item">
            <span class="label">当前宿舍:</span>
            <span class="value">{{ currentDormInfo }}</span>
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3 class="section-title">🏢 目标宿舍（可选）</h3>
        <p class="section-hint">您可以选择具体的目标宿舍，也可以不选择由管理员分配</p>

        <!-- 选择宿舍楼 -->
        <div class="form-group">
          <label class="form-label">
            <span class="label-icon">🏢</span>
            选择宿舍楼
            <span class="optional">(可选)</span>
          </label>
          <select
            v-model="selectedBuilding"
            @change="handleBuildingChange"
            :disabled="loadingBuildings"
            class="form-select"
          >
            <option value="">不指定目标宿舍（由管理员分配）</option>
            <option
              v-for="building in buildingOptions"
              :key="building.id"
              :value="building.id"
            >
              {{ building.buildingNumber }}号楼 ({{ building.assignedGender }}生楼)
            </option>
          </select>
        </div>

        <!-- 选择宿舍号 -->
        <div class="form-group" v-if="selectedBuilding">
          <label class="form-label">
            <span class="label-icon">🚪</span>
            选择宿舍号
          </label>
          <select
            v-model="selectedDorm"
            :disabled="!selectedBuilding || loadingDorms"
            class="form-select"
          >
            <option value="">请选择宿舍号</option>
            <option
              v-for="dorm in dormOptions"
              :key="dorm.dormitoryId"
              :value="dorm.dormitoryId"
            >
              {{ dorm.dormitoryNo }} (剩余 {{ dorm.availableBeds }}/{{ dorm.capacity }} 床位)
            </option>
          </select>
        </div>

        <!-- 提示信息 -->
        <div class="info-box" v-if="selectedBuilding && dormOptions.length === 0 && !loadingDorms">
          <span class="info-icon">💡</span>
          <span>该宿舍楼暂无可用宿舍，建议选择其他宿舍楼或由管理员分配</span>
        </div>

        <!-- 选中结果展示 -->
        <div class="selected-info" v-if="selectedBuilding && selectedDorm">
          <div class="info-item-inline">
            <span class="info-label">已选目标宿舍:</span>
            <span class="info-value">
              {{ getSelectedBuildingLabel() }}号楼 - {{ getSelectedDormLabel() }}
            </span>
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3 class="section-title">📝 申请原因</h3>

        <div class="form-group">
          <label class="form-label">
            <span class="required">*</span>
            请详细说明换宿原因
          </label>
          <textarea
            v-model="applicationForm.reason"
            placeholder="请详细描述您的换宿原因（如：与室友作息不合、身体原因需要特殊环境等）"
            class="form-textarea"
            rows="8"
            maxlength="500"
          ></textarea>
          <div class="char-count">{{ applicationForm.reason.length }}/500</div>
        </div>
      </div>

      <div class="form-footer">
        <button class="btn btn-cancel" @click="handleCancel">取消</button>
        <button
          class="btn btn-submit"
          @click="handleSubmit"
          :disabled="submitting || !applicationForm.reason.trim() || !currentDormitoryId"
        >
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import {
  getDormitoryId,
  getAvailableBuildings,
  getAvailableDorms,
  submitDormChangeApplication
} from '@/utils/api.js'

export default {
  name: 'DormChange',
  data() {
    return {
      loading: false,
      loadingBuildings: false,
      loadingDorms: false,
      submitting: false,
      userNo: '',
      userName: '',
      currentDormInfo: '',
      currentDormitoryId: null,

      // 表单数据
      applicationForm: {
        reason: ''
      },

      // 目标宿舍选择
      buildingOptions: [],
      dormOptions: [],
      selectedBuilding: '',
      selectedDorm: ''
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      this.loading = true
      try {
        // 从 sessionStorage 获取基本信息
        this.userNo = sessionStorage.getItem('userNo') || ''
        this.userName = sessionStorage.getItem('userName') || '未设置'

        // 获取当前宿舍ID
        const response = await getDormitoryId(this.userNo)

        if (response.data.code === 1) {
          this.currentDormitoryId = response.data.data
          console.log('获取到的当前宿舍ID:', this.currentDormitoryId)
          this.currentDormInfo = '已绑定宿舍'
        } else {
          ElMessage.warning('未找到您的宿舍信息，请先完善宿舍信息')
          this.currentDormInfo = '未绑定宿舍'
        }

        // 加载可选宿舍楼列表
        await this.loadBuildings()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败，请稍后重试')
        this.currentDormInfo = '获取失败'
      } finally {
        this.loading = false
      }
    },

    // 加载宿舍楼列表
    async loadBuildings() {
      this.loadingBuildings = true
      try {
        const response = await getAvailableBuildings(this.userNo)

        if (response.data.code === 1) {
          this.buildingOptions = response.data.data || []
          console.log('可选宿舍楼:', this.buildingOptions)
        } else {
          ElMessage.warning(response.data.msg || '获取宿舍楼列表失败')
        }
      } catch (error) {
        console.error('获取宿舍楼失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loadingBuildings = false
      }
    },

    // 宿舍楼变化时加载宿舍列表
    async handleBuildingChange(event) {
      const buildingId = event.target ? event.target.value : this.selectedBuilding

      this.selectedDorm = ''
      this.dormOptions = []

      if (!buildingId) return

      this.loadingDorms = true
      try {
        const response = await getAvailableDorms(buildingId)

        if (response.data.code === 1) {
          this.dormOptions = response.data.data || []
          console.log('可选宿舍:', this.dormOptions)

          if (this.dormOptions.length === 0) {
            ElMessage.warning('该宿舍楼暂无可用宿舍')
          }
        } else {
          ElMessage.error(response.data.msg || '获取宿舍列表失败')
        }
      } catch (error) {
        console.error('获取宿舍列表失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.loadingDorms = false
      }
    },

    // 获取选中宿舍楼的标签
    getSelectedBuildingLabel() {
      const building = this.buildingOptions.find(b => b.id == this.selectedBuilding)
      return building ? building.buildingNumber : ''
    },

    // 获取选中宿舍的标签
    getSelectedDormLabel() {
      const dorm = this.dormOptions.find(d => d.dormitoryId == this.selectedDorm)
      return dorm ? dorm.dormitoryNo : ''
    },

    // 提交申请
    async handleSubmit() {
      // 验证
      if (!this.applicationForm.reason.trim()) {
        ElMessage.warning('请填写申请原因')
        return
      }

      if (!this.currentDormitoryId) {
        ElMessage.error('未找到当前宿舍信息，无法提交申请')
        return
      }

      this.submitting = true
      try {
        const requestData = {
          studentNo: this.userNo,
          currentDormitoryId: this.currentDormitoryId,
          targetDormitoryId: this.selectedDorm ? parseInt(this.selectedDorm) : 0,
          reason: this.applicationForm.reason.trim()
        }

        console.log('提交换宿申请数据:', requestData)

        const response = await submitDormChangeApplication(requestData)

        if (response.data.code === 1) {
          ElMessage.success('换宿申请提交成功！')
          // 重置表单
          this.resetForm()
        } else {
          ElMessage.error(response.data.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交换宿申请失败:', error)
        ElMessage.error('网络错误，请稍后重试')
      } finally {
        this.submitting = false
      }
    },

    // 重置表单
    resetForm() {
      this.applicationForm.reason = ''
      this.selectedBuilding = ''
      this.selectedDorm = ''
      this.dormOptions = []
    },

    // 取消
    handleCancel() {
      if (this.applicationForm.reason || this.selectedBuilding) {
        if (confirm('确定要取消吗？已填写的内容将丢失')) {
          this.$router.go(-1)
        }
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style scoped>
.dorm-change-container {
  width: 100%;
  max-width: 800px;
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

/* 表单卡片 */
.form-card {
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  padding: 30px;
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e0e0e0;
}

.form-section:last-of-type {
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.section-hint {
  font-size: 13px;
  color: #7f8c8d;
  margin-bottom: 20px;
  padding: 10px 15px;
  background: #f8f9fa;
  border-left: 3px solid #3498db;
  border-radius: 4px;
}

/* 信息展示 */
.info-display {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.info-item .label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
  margin-right: 10px;
}

.info-item .value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 600;
}

/* 表单元素 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 10px;
}

.label-icon {
  font-size: 16px;
}

.required {
  color: #e74c3c;
  margin-right: 4px;
}

.optional {
  color: #95a5a6;
  font-size: 12px;
  margin-left: 4px;
}

/* 下拉选择框 */
.form-select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  color: #2c3e50;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.form-select:hover {
  border-color: #c0c4cc;
}

.form-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.form-select:disabled {
  background: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 文本域 */
.form-textarea {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s;
}

.form-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #95a5a6;
  margin-top: 5px;
}

/* 提示框 */
.info-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  background: #fff3cd;
  border-left: 4px solid #ffc107;
  border-radius: 4px;
  color: #856404;
  font-size: 13px;
  margin-top: 15px;
}

.info-icon {
  font-size: 18px;
}

/* 选中信息展示 */
.selected-info {
  margin-top: 20px;
  padding: 15px;
  background: #e8f4fd;
  border-radius: 6px;
  border: 1px solid #b3d9f2;
}

.info-item-inline {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  font-size: 13px;
  color: #2980b9;
  font-weight: 600;
}

.info-value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: bold;
}

/* 底部按钮 */
.form-footer {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
}

.btn {
  padding: 12px 30px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #ecf0f1;
  color: #2c3e50;
}

.btn-cancel:hover {
  background: #bdc3c7;
}

.btn-submit {
  background: #3498db;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.btn-submit:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
}

/* 响应式 */
@media (max-width: 768px) {
  .form-card {
    padding: 20px;
  }

  .info-display {
    grid-template-columns: 1fr;
  }

  .form-footer {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>