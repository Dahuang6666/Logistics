<template>
  <transition name="modal-fade">
    <div v-if="visible" class="modal-overlay" @click.self="handleClose">
      <div class="modal-container">
        <div class="modal-header">
          <div class="header-icon">🏠</div>
          <h2>完善宿舍信息</h2>
          <p class="subtitle">首次登录需要选择您的宿舍</p>
        </div>

        <div class="modal-body">
          <!-- 选择宿舍楼 - 改用原生 select -->
          <div class="form-group">
            <label class="form-label">
              <span class="label-icon">🏢</span>
              选择宿舍楼
            </label>
            <select
              v-model="selectedBuilding"
              @change="handleBuildingChange"
              :disabled="loading"
              class="native-select"
            >
              <option value="">请选择宿舍楼</option>
              <option
                v-for="building in buildingOptions"
                :key="building.id"
                :value="building.id"
              >
                {{ building.buildingNumber }}号楼 ({{ building.assignedGender }}生楼)
              </option>
            </select>
            <!-- 调试信息 -->
            <div style="margin-top: 10px; font-size: 12px; color: #999;">
              共 {{ buildingOptions.length }} 个宿舍楼可选
            </div>
          </div>

          <!-- 选择宿舍号 - 改用原生 select -->
          <div class="form-group" v-if="dormOptions.length > 0">
            <label class="form-label">
              <span class="label-icon">🚪</span>
              选择宿舍号
            </label>
            <select
              v-model="selectedDorm"
              :disabled="!selectedBuilding || loading"
              class="native-select"
            >
              <option value="">请选择宿舍号</option>
              <option
                v-for="dorm in dormOptions"
                :key="dorm.dormitoryId"
                :value="dorm.dormitoryNo"
              >
                {{ dorm.dormitoryNo }} (剩余 {{ dorm.availableBeds }}/{{ dorm.capacity }} 床位)
              </option>
            </select>
            <!-- 调试信息 -->
            <div style="margin-top: 10px; font-size: 12px; color: #999;">
              共 {{ dormOptions.length }} 个宿舍可选
            </div>
          </div>

          <!-- 提示信息 -->
          <div class="info-box" v-if="selectedBuilding && dormOptions.length === 0 && !loading">
            <span class="info-icon">💡</span>
            <span>该宿舍楼暂无可用宿舍</span>
          </div>

          <!-- 选中结果展示 -->
          <div class="selected-info" v-if="selectedBuilding && selectedDorm">
            <div class="info-item">
              <span class="info-label">已选宿舍:</span>
              <span class="info-value">
                {{ getSelectedBuildingLabel() }}号楼 - {{ selectedDorm }}
              </span>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button
            @click="handleSubmit"
            :disabled="!selectedBuilding || !selectedDorm || submitting"
            class="submit-btn-native"
          >
            {{ submitting ? '提交中...' : '确认提交' }}
          </button>
        </div>
        <!-- 关闭按钮 -->
        <!--  <button class="close-btn" @click="handleClose">✕</button>-->
      </div>
    </div>
  </transition>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getAvailableBuildings, getAvailableDorms, submitFirstDormInfo } from '@/utils/api.js'

export default {
  name: 'FirstLoginModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userNo: {
      type: String,
      required: true
    }
  },
  emits: ['update:visible', 'submit-success'],
  data() {
    return {
      loading: false,
      submitting: false,
      buildingOptions: [],
      dormOptions: [],
      selectedBuilding: '',
      selectedDorm: '',
      isSubmitting: false  // 添加提交状态标记
    }
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        console.log('弹窗打开,开始加载宿舍楼')
        this.loadBuildings()
      }
    }
  },
  methods: {
    // 加载宿舍楼列表
    async loadBuildings() {
      this.loading = true
      try {
        const response = await getAvailableBuildings(this.userNo)

        if (response.data.code === 1) {
          this.buildingOptions = response.data.data || []

          if (this.buildingOptions.length === 0) {
            ElMessage.warning('暂无可选宿舍楼')
          }
        } else {
          ElMessage.error(response.data.msg || '获取宿舍楼失败')
        }
      } catch (error) {
        console.error('获取宿舍楼失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 宿舍楼变化时加载宿舍列表
    async handleBuildingChange(event) {
      const buildingId = event.target ? event.target.value : this.selectedBuilding

      this.selectedDorm = ''
      this.dormOptions = []

      if (!buildingId) return

      this.loading = true
      try {
        const response = await getAvailableDorms(buildingId)

        if (response.data.code === 1) {
          this.dormOptions = response.data.data || []

          if (this.dormOptions.length === 0) {
            ElMessage.warning('该宿舍楼暂无可用宿舍')
          }
        } else {
          ElMessage.error(response.data.msg || '获取宿舍列表失败')
        }
      } catch (error) {
        console.error('获取宿舍列表失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 获取选中宿舍楼的标签
    getSelectedBuildingLabel() {
      const building = this.buildingOptions.find(b => b.id == this.selectedBuilding)
      return building ? building.buildingNumber : ''
    },

    // 提交宿舍信息
    async handleSubmit() {
      if (!this.selectedBuilding || !this.selectedDorm) {
        ElMessage.warning('请选择完整的宿舍信息')
        return
      }

      this.submitting = true
      this.isSubmitting = true  // 标记为正在提交

      try {
        const response = await submitFirstDormInfo({
          userNo: this.userNo,
          buildingId: parseInt(this.selectedBuilding),
          dormitoryNo: this.selectedDorm
        })

        if (response.data.code === 1) {
          ElMessage.success('宿舍信息提交成功!')
          this.$emit('submit-success')

          // 延迟关闭,确保提示显示
          setTimeout(() => {
            this.forceClose()
          }, 500)
        } else {
          ElMessage.error(response.data.msg || '提交失败')
          this.isSubmitting = false
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('网络错误,请稍后重试')
        this.isSubmitting = false
      } finally {
        this.submitting = false
      }
    },

    // 强制关闭(提交成功后调用)
    forceClose() {
      this.$emit('update:visible', false)
      // 清空选择
      this.selectedBuilding = ''
      this.selectedDorm = ''
      this.dormOptions = []
      this.isSubmitting = false
    },

    // 用户手动关闭(首次登录必须完成)
    handleClose() {
      // 如果正在提交,不阻止关闭
      if (this.isSubmitting) {
        return
      }

      // 否则阻止关闭并提示
      ElMessage({
        message: '请先完成宿舍信息填写',
        type: 'warning',
        duration: 2000,
        customClass: 'custom-message'
      })
    }
  }
}
</script>
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modalSlideIn 0.3s ease;
  position: relative;
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
  text-align: center;
  padding: 30px 30px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-icon {
  font-size: 50px;
  margin-bottom: 15px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.modal-body {
  padding: 30px;
  overflow-y: auto;
  flex: 1;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

.label-icon {
  font-size: 18px;
}

/* 原生 select 样式 */
.native-select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  color: #2c3e50;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.native-select:hover {
  border-color: #c0c4cc;
}

.native-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.native-select:disabled {
  background: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
}

.info-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  background: #e8f4fd;
  border-left: 4px solid #3498db;
  border-radius: 4px;
  color: #2c3e50;
  font-size: 13px;
  margin-top: 15px;
}

.info-icon {
  font-size: 18px;
}

.selected-info {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  font-size: 13px;
  color: #7f8c8d;
  font-weight: 600;
}

.info-value {
  font-size: 14px;
  color: #2c3e50;
  font-weight: bold;
}

.modal-footer {
  padding: 20px 30px;
  border-top: 1px solid #e0e0e0;
  background: #f8f9fa;
}

/* 原生按钮样式 */
.submit-btn-native {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn-native:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn-native:disabled {
  background: #dcdfe6;
  cursor: not-allowed;
  transform: none;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  line-height: 1;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
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

.modal-fade-enter-active .modal-container {
  animation: modalSlideIn 0.3s ease;
}

.modal-fade-leave-active .modal-container {
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
@media (max-width: 576px) {
  .modal-container {
    max-width: 95%;
  }

  .modal-header {
    padding: 25px 20px 15px;
  }

  .header-icon {
    font-size: 40px;
  }

  .modal-header h2 {
    font-size: 20px;
  }

  .modal-body {
    padding: 20px;
  }
}
</style>