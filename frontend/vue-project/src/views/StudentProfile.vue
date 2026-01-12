<template>
  <div class="profile-container">
    <div class="page-header">
      <h1 class="page-title">👤 个人中心</h1>
    </div>

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

      <!-- 第二行:室友信息 (原版样式) -->
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
            <button class="action-btn" @click="handleChangePassword">
              🔒 修改密码
            </button>
            <button class="action-btn" @click="handleEditProfile">
              ✏️ 编辑个人资料
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { getStudentProfile, getAvatarUrl, uploadAvatar } from '@/utils/api.js'

export default {
  name: 'StudentProfile',
  data() {
    return {
      loading: false,
      userInfo: {},
      dormInfo: null,
      roommates: [],
      avatarUrl: '',
      uploading: false
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

        // 并发请求:获取个人资料 + 头像
        const [profileRes, avatarRes] = await Promise.all([
          getStudentProfile(userNo),
          getAvatarUrl(userNo)
        ])

        // 处理个人资料
        if (profileRes.data.code === 1) {
          const data = profileRes.data.data
          this.userInfo = data.userInfo || {}
          this.dormInfo = data.dormInfo
          this.roommates = data.roommates || []
        } else {
          ElMessage.error(profileRes.data.msg )
        }

        // 处理头像
        if (avatarRes.data.code === 1) {
          console.log("头像",avatarRes.data.data)
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

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }

      // 验证文件大小 (限制2MB)
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('图片大小不能超过2MB')
        return
      }

      // 上传头像
      await this.uploadAvatarFile(file)

      // 清空input,允许重复选择同一文件
      event.target.value = ''
    },

    // 上传头像文件
    async uploadAvatarFile(file) {
      this.uploading = true
      const loadingMsg = ElMessage({
        message: '正在上传头像...',
        type: 'info',
        duration: 0,
        iconClass: 'el-icon-loading'
      })

      try {
        const userNo = sessionStorage.getItem('userNo')
        const response = await uploadAvatar(userNo, file)

        if (response.data.code === 1) {
          // 上传成功,更新头像显示
          this.avatarUrl = response.data.data
          loadingMsg.close()
          ElMessage.success('头像上传成功!')
        } else {
          loadingMsg.close()
          ElMessage.error(response.data.msg )
        }
      } catch (error) {
        console.error('上传头像失败:', error)
        loadingMsg.close()
        ElMessage.error('网络错误,请稍后重试')
      } finally {
        this.uploading = false
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
    },

    // 修改密码
    handleChangePassword() {
      ElMessage.info('修改密码功能开发中...')
    },

    // 编辑个人资料
    handleEditProfile() {
      ElMessage.info('编辑资料功能开发中...')
    }
  }
}
</script>

<style scoped>
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

/* 个人资料内容 */
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

/* 头像区域 */
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

/* 室友列表 */
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

/* 操作按钮 */
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

/* 响应式 */
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