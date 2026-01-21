<template>
  <div class="profile-container">

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
    <!-- 个人资料内容 -->
    <div v-else class="profile-content">
      <!-- 第一行:个人信息 + 宿舍信息 -->
      <div class="info-row">
        <!-- 个人信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <span class="card-icon">👤</span>
            <h3>个人信息</h3>
          </div>
          <div class="card-body">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <div class="avatar-wrapper">
                <img :src="avatarUrl" alt="头像" class="avatar-img" />
                <div class="avatar-overlay" @click="triggerFileInput">
                  <span class="upload-icon">📷</span>
                  <span class="upload-text">更换头像</span>
                </div>
              </div>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleAvatarChange"
              />
            </div>

            <!-- 个人信息列表 -->
            <div class="info-item">
              <span class="label">学号:</span>
              <span class="value">{{ userInfo.userNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名:</span>
              <span class="value">{{ userInfo.username || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别:</span>
              <span class="value">{{ userInfo.gender || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机:</span>
              <span class="value">{{ formatPhone(userInfo.phone) }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱:</span>
              <span class="value">{{ userInfo.email || '未绑定' }}</span>
            </div>
          </div>
        </div>

        <!-- 宿舍信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <span class="card-icon">🏠</span>
            <h3>宿舍信息</h3>
          </div>
          <div class="card-body">
            <div v-if="dormInfo">
              <div class="info-item">
                <span class="label">楼栋:</span>
                <span class="value">
                  {{ dormInfo.buildingNumber }}号楼
                  ({{ dormInfo.assignedGender }}生楼)
                </span>
              </div>
              <div class="info-item">
                <span class="label">宿舍:</span>
                <span class="value">{{ dormInfo.dormitoryNumber }}室</span>
              </div>
              <div class="info-item">
                <span class="label">床位:</span>
                <span class="value">
                  {{ dormInfo.capacity - dormInfo.availableBeds }}/{{ dormInfo.capacity }}
                  (已入住/总数)
                </span>
              </div>
              <div class="info-item">
                <span class="label">状态:</span>
                <span class="value" :class="getStatusClass(dormInfo.status)">
                  {{ getStatusIcon(dormInfo.status) }} {{ dormInfo.status }}
                </span>
              </div>
            </div>
            <div v-else class="empty-hint">
              暂无宿舍信息
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行:室友信息 -->
      <div class="info-card full-width">
        <div class="card-header">
          <span class="card-icon">👥</span>
          <h3>室友信息 ({{ roommates.length }}人)</h3>
        </div>
        <div class="card-body">
          <div v-if="roommates.length > 0" class="roommate-list">
            <!-- 室友 -->
            <div
              v-for="mate in roommates"
              :key="mate.userNo"
              class="roommate-item"
            >
              <span class="roommate-icon">👨‍🎓</span>
              <span class="roommate-name">{{ mate.username }} ({{ mate.userNo }})</span>
              <span v-if="mate.phone" class="roommate-contact">📱 {{ formatPhone(mate.phone) }}</span>
            </div>
            <!-- 空床位 -->
            <div v-if="getEmptyBeds() > 0" class="roommate-item empty">
              <span class="roommate-icon">🔘</span>
              <span class="roommate-name">空床位 ({{ getEmptyBeds() }}个)</span>
            </div>
          </div>
          <div v-else class="empty-hint">
            暂无室友信息
          </div>
        </div>
      </div>

      <!-- 第三行:账号设置 -->
      <div class="info-card full-width">
        <div class="card-header">
          <span class="card-icon">⚙️</span>
          <h3>账号设置</h3>
        </div>
        <div class="card-body">
          <div class="action-buttons">
            <button class="action-btn" @click="showPasswordModal = true">
              🔒 修改密码
            </button>
            <button class="action-btn" @click="openEditModal">
              ✏️ 编辑个人资料
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <transition name="modal-fade">
      <div v-if="showPasswordModal" class="modal-overlay" @click="closePasswordModal">
        <div class="modal-box" @click.stop>
          <div class="modal-header">
            <h3>🔒 修改密码</h3>
            <button class="close-btn" @click="closePasswordModal">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-group">
              <label>新密码</label>
              <input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码(6-20位)"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>确认密码</label>
              <input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                class="form-input"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button class="modal-btn cancel-btn" @click="closePasswordModal">取消</button>
            <button class="modal-btn confirm-btn" @click="handlePasswordSubmit">确认修改</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 编辑个人资料弹窗 -->
    <transition name="modal-fade">
      <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
        <div class="modal-box" @click.stop>
          <div class="modal-header">
            <h3>✏️ 编辑个人资料</h3>
            <button class="close-btn" @click="closeEditModal">✕</button>
          </div>
          <div class="modal-content">
            <div class="form-group">
              <label>姓名</label>
              <input
                v-model="editForm.username"
                type="text"
                placeholder="请输入姓名"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input
                v-model="editForm.phone"
                type="text"
                placeholder="请输入手机号"
                class="form-input"
                maxlength="11"
              />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input
                v-model="editForm.email"
                type="email"
                placeholder="请输入邮箱"
                class="form-input"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button class="modal-btn cancel-btn" @click="closeEditModal">取消</button>
            <button class="modal-btn confirm-btn" @click="handleEditSubmit">保存修改</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getStudentProfile, getAvatarUrl, uploadAvatar, updateUserProfile, updatePassword } from '@/utils/api.js'

export default {
  name: 'StudentProfile',
  data() {
    return {
      loading: false,
      userInfo: {},
      dormInfo: null,
      roommates: [],
      avatarUrl: '',
      uploading: false,

      // 修改密码弹窗
      showPasswordModal: false,
      passwordForm: {
        newPassword: '',
        confirmPassword: ''
      },

      // 编辑资料弹窗
      showEditModal: false,
      editForm: {
        username: '',
        phone: '',
        email: ''
      }
    }
  },
  mounted() {
    this.loadProfile()
  },
  methods: {
    // 加载个人资料
    async loadProfile() {
      this.loading = true
      try {
        const userNo = sessionStorage.getItem('userNo')

        const [profileRes, avatarRes] = await Promise.all([
          getStudentProfile(userNo),
          getAvatarUrl(userNo)
        ])

        if (profileRes.data.code === 1) {
          const data = profileRes.data.data
          this.userInfo = data.userInfo || {}
          this.dormInfo = data.dormInfo
          this.roommates = data.roommates || []
        } else {
          ElMessage.error(profileRes.data.msg)
        }

        if (avatarRes.data.code === 1) {
          this.avatarUrl = avatarRes.data.data
        }
      } catch (error) {
        console.error('获取个人资料失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.loading = false
      }
    },

    // 触发文件选择
    triggerFileInput() {
      this.$refs.fileInput.click()
    },

    // 处理头像选择
    async handleAvatarChange(event) {
      const file = event.target.files[0]
      if (!file) return

      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }

      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('图片大小不能超过2MB')
        return
      }

      await this.uploadAvatarFile(file)
      event.target.value = ''
    },

    // 上传头像文件
    async uploadAvatarFile(file) {
      this.uploading = true
      const loadingMsg = ElMessage({
        message: '正在上传头像...',
        type: 'info',
        duration: 0
      })

      try {
        const userNo = sessionStorage.getItem('userNo')
        const response = await uploadAvatar(userNo, file)

        if (response.data.code === 1) {
          this.avatarUrl = response.data.data
          loadingMsg.close()
          ElMessage.success('头像上传成功!')
        } else {
          loadingMsg.close()
          ElMessage.error(response.data.msg)
        }
      } catch (error) {
        console.error('上传头像失败:', error)
        loadingMsg.close()
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.uploading = false
      }
    },

    // 打开编辑资料弹窗
    openEditModal() {
      this.editForm = {
        username: this.userInfo.username || '',
        phone: this.userInfo.phone || '',
        email: this.userInfo.email || ''
      }
      this.showEditModal = true
    },

    // 关闭编辑资料弹窗
    closeEditModal() {
      this.showEditModal = false
      this.editForm = {
        username: '',
        phone: '',
        email: ''
      }
    },

    // 提交编辑资料
    async handleEditSubmit() {
      // 验证
      if (!this.editForm.username?.trim()) {
        ElMessage.warning('请输入姓名')
        return
      }

      if (this.editForm.phone && !/^1[3-9]\d{9}$/.test(this.editForm.phone)) {
        ElMessage.warning('请输入正确的手机号')
        return
      }

      if (this.editForm.email && !/^[\w.-]+@[\w.-]+\.\w+$/.test(this.editForm.email)) {
        ElMessage.warning('请输入正确的邮箱')
        return
      }

      try {
        const userNo = sessionStorage.getItem('userNo')
        const response = await updateUserProfile({
          userNo,
          username: this.editForm.username,
          phone: this.editForm.phone,
          email: this.editForm.email
        })

        if (response.data.code === 1) {
          ElMessage.success('个人资料修改成功!')
          this.closeEditModal()
          // 刷新数据
          await this.loadProfile()
        } else {
          ElMessage.error(response.data.msg || '修改失败')
        }
      } catch (error) {
        console.error('修改个人资料失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      }
    },

    // 关闭修改密码弹窗
    closePasswordModal() {
      this.showPasswordModal = false
      this.passwordForm = {
        newPassword: '',
        confirmPassword: ''
      }
    },

    // 提交修改密码
    async handlePasswordSubmit() {
      // 验证
      if (!this.passwordForm.newPassword) {
        ElMessage.warning('请输入新密码')
        return
      }

      if (this.passwordForm.newPassword.length < 6 || this.passwordForm.newPassword.length > 20) {
        ElMessage.warning('密码长度应为6-20位')
        return
      }

      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        ElMessage.warning('两次输入的密码不一致')
        return
      }

      try {
        const userNo = sessionStorage.getItem('userNo')

        console.log("newPassword ",this.passwordForm.newPassword)

        const response = await updatePassword(
          userNo,
          this.passwordForm.newPassword
        )

        if (response.data.code === 1) {
          ElMessage.success('密码修改成功,请重新登录!')
          this.closePasswordModal()

          // 清除登录信息并跳转到登录页
          setTimeout(() => {
            sessionStorage.clear()
            this.$router.push('/')
          }, 1500)
        } else {
          ElMessage.error(response.data.msg || '修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('网络错误,请稍后重试')
      }
    },

    // 格式化手机号
    formatPhone(phone) {
      if (!phone) return '未绑定'
      return phone.substring(0, 3) + '****' + phone.substring(7)
    },

    // 获取状态样式类
    getStatusClass(status) {
      return status === '正常' ? 'status-normal' : 'status-repair'
    },

    // 获取状态图标
    getStatusIcon(status) {
      return status === '正常' ? '🟢' : '🔴'
    },

    // 获取空床位数
    getEmptyBeds() {
      if (!this.dormInfo) return 0
      return this.dormInfo.availableBeds
    }
  }
}
</script>

<style scoped>
/* ... 原有样式保持不变 ... */

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
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
  max-width: 450px;
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
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
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
  font-size: 18px;
  transition: all 0.3s;
}

.close-btn:hover {
  background: #e0e0e0;
  transform: rotate(90deg);
}

.modal-content {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}


.form-input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 25px;
  border-top: 1px solid #e0e0e0;
  background: #f8f9fa;
}

.modal-btn {
  flex: 1;
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #ecf0f1;
  color: #2c3e50;
}

.cancel-btn:hover {
  background: #bdc3c7;
}

.confirm-btn {
  background: #3498db;
  color: white;
}

.confirm-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
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

.modal-fade-enter-active .modal-box {
  animation: modalSlideIn 0.3s ease;
}

.modal-fade-leave-active .modal-box {
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

/* 原有样式... */
.profile-container {
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
}

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

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  overflow: hidden;
}

.info-card.full-width {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.card-icon {
  font-size: 20px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.card-body {
  padding: 20px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.upload-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.upload-text {
  color: white;
  font-size: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #e0e0e0;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
  width: 80px;
  flex-shrink: 0;
}

.value {
  font-size: 14px;
  color: #2c3e50;
  flex: 1;
}

.status-normal {
  color: #27ae60;
  font-weight: 600;
}

.status-repair {
  color: #e74c3c;
  font-weight: 600;
}

.empty-hint {
  text-align: center;
  color: #7f8c8d;
  font-size: 14px;
  padding: 20px;
}

.roommate-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.roommate-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 6px;
  gap: 10px;
}

.roommate-item.self {
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
  border: 1px solid #42a5f5;
}

.roommate-item.empty {
  background: #fff;
  border: 1px dashed #ddd;
}

.roommate-icon {
  font-size: 18px;
}

.roommate-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  flex: 1;
}

.roommate-tag {
  background: #3498db;
  color: white;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.roommate-contact {
  font-size: 12px;
  color: #7f8c8d;
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1;
  min-width: 150px;
  padding: 12px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

@media (max-width: 768px) {
  .info-row {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }

  .avatar-wrapper {
    width: 80px;
    height: 80px;
  }
}
</style>