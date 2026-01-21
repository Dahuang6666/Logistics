<template>
  <div class="repair-apply-container">
    <div class="page-header">
      <h1 class="page-title">🔧 报修申请</h1>
      <p class="subtitle">请详细描述您遇到的问题,我们会尽快处理</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 报修表单 -->
    <div v-else class="repair-form-card">
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
            <span class="label">宿舍:</span>
            <span class="value">{{ dormInfo }}</span>
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3 class="section-title">📝 报修内容</h3>

        <div class="form-group">
          <label class="form-label">
            <span class="required">*</span>
            问题描述
          </label>
          <textarea
            v-model="repairForm.content"
            placeholder="请详细描述遇到的问题(如:水龙头漏水、灯泡不亮等)"
            class="form-textarea"
            rows="6"
            maxlength="500"
          ></textarea>
          <div class="char-count">{{ repairForm.content.length }}/500</div>
        </div>

        <div class="form-group">
          <label class="form-label">
            <span class="optional">(可选)</span>
            上传图片
          </label>
          <div class="image-upload-area">
            <!-- 已上传的图片 -->
            <div v-if="imageUrl" class="image-preview">
              <img :src="imageUrl" alt="报修图片" class="preview-img" />
              <button class="remove-btn" @click="removeImage">
                <span>✕</span>
              </button>
            </div>

            <!-- 上传按钮 -->
            <div v-else class="upload-box" @click="triggerFileInput">
              <div class="upload-icon">📷</div>
              <div class="upload-text">点击上传图片</div>
              <div class="upload-hint">支持 JPG、PNG 格式,不超过 2MB</div>
            </div>

            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleImageUpload"
            />
          </div>
        </div>
      </div>

      <div class="form-footer">
        <button class="btn btn-cancel" @click="handleCancel">取消</button>
        <button
          class="btn btn-submit"
          @click="handleSubmit"
          :disabled="submitting || !repairForm.content.trim() || !dormitoryId"
        >
          {{ submitting ? '提交中...' : '提交报修' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getDormitoryId, uploadRepairImage, submitRepair } from '@/utils/api.js'

export default {
  name: 'RepairApply',
  data() {
    return {
      loading: false,
      submitting: false,
      userNo: '',
      userName: '',
      dormInfo: '',
      dormitoryId: null,  // 宿舍ID
      repairForm: {
        content: ''
      },
      imageUrl: '',
      uploadingImage: false
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

        // 获取宿舍ID
        const response = await getDormitoryId(this.userNo)

        if (response.data.code === 1) {
          this.dormitoryId = response.data.data
          console.log('获取到的宿舍ID:', this.dormitoryId)

          // 可以额外显示宿舍信息(可选)
          this.dormInfo = '已绑定宿舍'  // 简化显示,或者可以再调用接口获取详细信息
        } else {
          ElMessage.warning('未找到您的宿舍信息,请先完善宿舍信息')
          this.dormInfo = '未绑定宿舍'
        }
      } catch (error) {
        console.error('获取宿舍信息失败:', error)
        ElMessage.error('获取宿舍信息失败,请稍后重试')
        this.dormInfo = '获取失败'
      } finally {
        this.loading = false
      }
    },

    // 触发文件选择
    triggerFileInput() {
      this.$refs.fileInput.click()
    },

    // 处理图片上传
    async handleImageUpload(event) {
      const file = event.target.files[0]
      if (!file) return

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }

      // 验证文件大小 (2MB)
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('图片大小不能超过 2MB')
        return
      }

      // 上传图片
      this.uploadingImage = true
      const loadingMsg = ElMessage({
        message: '正在上传图片...',
        type: 'info',
        duration: 0
      })

      try {
        const response = await uploadRepairImage(file)

        if (response.data.code === 1) {
          this.imageUrl = response.data.data
          loadingMsg.close()
          ElMessage.success('图片上传成功')
        } else {
          loadingMsg.close()
          ElMessage.error(response.data.msg || '图片上传失败')
        }
      } catch (error) {
        console.error('上传图片失败:', error)
        loadingMsg.close()
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.uploadingImage = false
        event.target.value = ''
      }
    },

    // 移除图片
    removeImage() {
      this.imageUrl = ''
      ElMessage.info('已移除图片')
    },

    // 提交报修
    async handleSubmit() {
      // 验证
      if (!this.repairForm.content.trim()) {
        ElMessage.warning('请输入问题描述')
        return
      }

      if (!this.dormitoryId) {
        ElMessage.error('未找到宿舍信息,无法提交报修')
        return
      }

      this.submitting = true
      try {
        const requestData = {
          userNo: this.userNo,
          dormitoryId: this.dormitoryId,
          content: this.repairForm.content.trim(),
          imageUrl: this.imageUrl || ''
        }

        console.log('提交报修数据:', requestData)

        const response = await submitRepair(requestData)

        if (response.data.code === 1) {
          ElMessage.success('报修申请提交成功!')
          // 重置表单
          this.repairForm.content = ''
          this.imageUrl = ''
          // 跳转到报修进度页面
          setTimeout(() => {
            this.$router.push('/student/repair-progress')
          }, 1500)
        } else {
          ElMessage.error(response.data.msg || '提交失败')
        }
      } catch (error) {
        console.error('提交报修失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.submitting = false
      }
    },

    // 取消
    handleCancel() {
      if (this.repairForm.content || this.imageUrl) {
        if (confirm('确定要取消吗?已填写的内容将丢失')) {
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
.repair-apply-container {
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
.repair-form-card {
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
  margin-bottom: 20px;
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
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 10px;
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

/* 图片上传 */
.image-upload-area {
  margin-top: 10px;
}

.upload-box {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafbfc;
}

.upload-box:hover {
  border-color: #3498db;
  background: #f0f7ff;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.upload-text {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 5px;
}

.upload-hint {
  font-size: 12px;
  color: #95a5a6;
}

.image-preview {
  position: relative;
  display: inline-block;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

.preview-img {
  max-width: 100%;
  max-height: 300px;
  display: block;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s;
}

.remove-btn:hover {
  background: rgba(231, 76, 60, 0.9);
  transform: scale(1.1);
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
  .repair-form-card {
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