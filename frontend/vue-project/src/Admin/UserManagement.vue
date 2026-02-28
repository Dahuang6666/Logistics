<template>
  <div class="user-management-container">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">👥 用户管理</h1>
        <p class="page-subtitle">管理系统内的学生及管理员账号权限</p>
      </div>
      <button class="add-btn" @click="openAddDialog">
        <span class="icon">+</span> 新增系统用户
      </button>
    </div>

    <div class="filter-bar">
      <div class="search-wrapper">
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索姓名、学号或工号..."
          class="search-box"
          @keyup.enter="handleSearch"
        >
        <!-- 移除了放大镜图标，保留按钮但隐藏视觉显示 -->
        <button class="search-btn" @click="handleSearch"></button>
      </div>
    </div>

    <div class="table-card">
      <table class="admin-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>学号/工号</th>
          <th>姓名</th>
          <th>角色</th>
          <th>性别</th>
          <th>联系方式</th>
          <th>状态</th>
          <th style="text-align: center">操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in filteredUserList" :key="user.id">
          <td>{{ user.id }}</td>
          <td><span class="user-no-tag">{{ user.userNo }}</span></td>
          <td class="user-name">{{ user.username || '未填写' }}</td>
          <td>
              <span :class="['role-badge', user.role === 4 ? 'role-admin' : 'role-student']">
                {{ user.role === 4 ? '系统管理员' : '学生' }}
              </span>
          </td>
          <td>{{ user.gender || '-' }}</td>
          <td>{{ user.phone || '-' }}</td>
          <td>
            <div class="status-wrapper">
              <span :class="['status-dot', user.status === 1 ? 'status-active' : 'status-disabled']"></span>
              {{ user.status === 1 ? '正常' : '禁用' }}
            </div>
          </td>
          <td class="action-cells">
            <button class="btn-text edit" @click="handleEdit(user)">编辑</button>
            <button class="btn-text reset" @click="handleResetPassword(user)">重置密码</button>
            <button class="btn-text delete" @click="handleDelete(user)">删除</button>
          </td>
        </tr>
        </tbody>
      </table>
      <div v-if="filteredUserList.length === 0" class="empty-placeholder">
        <p>暂无符合条件的数据</p>
      </div>
    </div>

    <Transition name="modal-fade">
      <div v-if="showDialog" class="modal-overlay" @click.self="closeDialog">
        <div class="modal-box">
          <div class="modal-header">
            <div class="title-area">
              <span class="modal-tag" :class="isEdit ? 'tag-rule' : 'tag-activity'">
                {{ isEdit ? '修改' : '新增' }}
              </span>
              <h3 class="modal-title-text">{{ isEdit ? '用户信息编辑' : '创建新系统账号' }}</h3>
            </div>
            <button class="close-btn" @click="closeDialog">✕</button>
          </div>

          <div class="modal-content">
            <div class="form-grid">
              <div class="form-item">
                <label>姓名 <span class="required">*</span></label>
                <input v-model="form.username" placeholder="请输入姓名" :class="{'input-error': errors.username}">
                <span class="error-msg">{{ errors.username }}</span>
              </div>
              <div class="form-item">
                <label>学号/工号 <span class="required">*</span></label>
                <input v-model="form.userNo" :disabled="isEdit" placeholder="唯一识别号" :class="{'input-error': errors.userNo}">
                <span class="error-msg">{{ errors.userNo }}</span>
              </div>
              <div class="form-item full-width" v-if="!isEdit">
                <label>初始密码 <span class="required">*</span></label>
                <input type="password" v-model="form.password" placeholder="设置登录密码" :class="{'input-error': errors.password}">
                <span class="error-msg">{{ errors.password }}</span>
              </div>
              <div class="form-item">
                <label>账号角色</label>
                <select v-model="form.role" class="modal-select">
                  <option :value="1">学生</option>
                  <option :value="4">系统管理员</option>
                </select>
              </div>
              <div class="form-item">
                <label>性别</label>
                <select v-model="form.gender" class="modal-select">
                  <option value="男">男</option>
                  <option value="女">女</option>
                </select>
              </div>
              <div class="form-item">
                <label>手机号</label>
                <input v-model="form.phone" placeholder="11位手机号" :class="{'input-error': errors.phone}" @input="validateField('phone')">
                <span class="error-msg">{{ errors.phone }}</span>
              </div>
              <div class="form-item">
                <label>电子邮箱</label>
                <input v-model="form.email" placeholder="example@mail.com" :class="{'input-error': errors.email}" @input="validateField('email')">
                <span class="error-msg">{{ errors.email }}</span>
              </div>
              <div class="form-item full-width">
                <label>账号状态</label>
                <div class="radio-group">
                  <label class="radio-label">
                    <input type="radio" :value="1" v-model="form.status"> 正常使用
                  </label>
                  <label class="radio-label">
                    <input type="radio" :value="0" v-model="form.status"> 立即禁用
                  </label>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="cancel-btn" @click="closeDialog">取消</button>
            <button class="confirm-btn" @click="submitForm">确认并保存</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import {
  getAdminUserList, createAdminUser, updateAdminUser,
  deleteAdminUser, resetPassword
} from '@/utils/api.js'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserManagement',
  data() {
    return {
      userList: [],
      searchKeyword: '',
      showDialog: false,
      isEdit: false,
      form: {
        id: null, username: '', userNo: '', password: '', role: 1,
        gender: '男', phone: '', email: '', status: 1
      },
      errors: { username: '', userNo: '', password: '', phone: '', email: '' }
    }
  },
  computed: {
    filteredUserList() {
      const k = this.searchKeyword.toLowerCase()
      let list = this.userList.filter(u =>
        (u.username && u.username.toLowerCase().includes(k)) ||
        (u.userNo && u.userNo.toLowerCase().includes(k))
      )
      return list.sort((a, b) => b.role - a.role)
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        const res = await getAdminUserList()
        if (res.data.code === 1) this.userList = res.data.data
      } catch (e) {
        ElMessage.error('无法连接到服务器')
      }
    },
    handleSearch() {
      // 这里的搜索逻辑由 computed 自动完成，如需后端搜索可在此调用
    },
    async handleResetPassword(user) {
      ElMessageBox.confirm(
        `确定要重置用户 [${user.username}] 的密码吗？`,
        '重置确认',
        { confirmButtonText: '确定重置', cancelButtonText: '取消', type: 'warning' }
      ).then(async () => {
        try {
          const res = await resetPassword(user.userNo);
          if (res.data.code === 1) ElMessage.success('密码重置成功');
          else ElMessage.error(res.data.msg || '重置失败');
        } catch (e) {
          ElMessage.error('网络请求失败');
        }
      }).catch(() => {
      });
    },
    validateForm() {
      let isValid = true;
      this.clearErrors();
      if (!this.form.username) {
        this.errors.username = '姓名不能为空';
        isValid = false;
      }
      if (!this.form.userNo) {
        this.errors.userNo = '学工号不能为空';
        isValid = false;
      }
      if (!this.isEdit && !this.form.password) {
        this.errors.password = '初始密码不能为空';
        isValid = false;
      }
      if (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) {
        this.errors.phone = '手机号格式错误';
        isValid = false;
      }
      if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = '邮箱格式错误';
        isValid = false;
      }
      return isValid;
    },
    validateField(field) {
      if (field === 'phone') this.errors.phone = (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) ? '手机号格式错误' : '';
      if (field === 'email') this.errors.email = (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) ? '邮箱格式错误' : '';
    },
    clearErrors() {
      Object.keys(this.errors).forEach(k => this.errors[k] = '');
    },
    openAddDialog() {
      this.isEdit = false;
      this.clearErrors();
      this.form = {
        id: null,
        username: '',
        userNo: '',
        password: '',
        role: 1,
        gender: '男',
        status: 1,
        phone: '',
        email: ''
      };
      this.showDialog = true;
    },
    handleEdit(user) {
      this.isEdit = true;
      this.clearErrors();
      this.form = { ...user };
      this.showDialog = true;
    },
    closeDialog() {
      this.showDialog = false
    },
    async submitForm() {
      if (!this.validateForm()) return;
      try {
        const res = this.isEdit ? await updateAdminUser(this.form) : await createAdminUser(this.form);
        if (res.data.code === 1) {
          ElMessage.success('保存成功');
          this.closeDialog();
          this.loadUsers();
        } else {
          ElMessage.error(res.data.msg || '操作失败');
        }
      } catch (e) {
        ElMessage.error('服务器错误');
      }
    },
    handleDelete(user) {
      ElMessageBox.confirm(`确定删除用户 [${user.username}] 吗？`, '危险操作', { type: 'error' })
        .then(async () => {
          const res = await deleteAdminUser(user.id);
          if (res.data.code === 1) {
            ElMessage.success('已删除');
            this.loadUsers();
          }
        })
    }
  }
}
</script>

<style scoped>
/* 基础容器 */
.user-management-container {
  padding: 20px;
  background: #fcfcfc;
  min-height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
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
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.add-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

/* --- 搜索栏：修改后样式 --- */
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
}

.search-wrapper {
  flex: 1;
  min-width: 200px;
  max-width: 350px;
  display: flex;
}

.search-box {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  /* 恢复完整的边框，移除 border-right: none */
  border-radius: 4px; /* 修改为完整的圆角 */
  font-size: 13px;
  transition: border-color 0.3s;
}

.search-box:focus {
  outline: none;
  border-color: #3498db;
}

/* 隐藏搜索按钮但保留点击功能 */
.search-btn {
  /* 完全隐藏按钮 */
  display: none;
  /* 如果你想保留点击功能但视觉隐藏，可替换为以下样式：
  width: 0;
  height: 0;
  border: none;
  background: transparent;
  padding: 0;
  margin: 0;
  */
}

/* 表格样式 */
.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #eee;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th {
  background: #f8f9fb;
  padding: 16px;
  text-align: left;
  color: #666;
  font-size: 14px;
  font-weight: 600;
}

.admin-table td {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

/* 操作按钮 */
.action-cells {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-text {
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 500;
  padding: 6px 10px;
  border-radius: 6px;
  transition: 0.2s;
  font-size: 13px;
}

.btn-text.edit {
  color: #1890ff;
}

.btn-text.edit:hover {
  background: #e6f7ff;
}

.btn-text.reset {
  color: #fa8c16;
}

.btn-text.reset:hover {
  background: #fff7e6;
}

.btn-text.delete {
  color: #ff4d4f;
}

.btn-text.delete:hover {
  background: #fff1f0;
}

/* --- 弹窗：参考公告详情弹窗样式 --- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(3px);
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
  max-width: 600px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: modalSlideIn 0.3s ease;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e0e0e0;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-tag {
  padding: 4px 12px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.tag-rule {
  background: #3498db;
}

.tag-activity {
  background: #27ae60;
}

.modal-title-text {
  font-size: 18px;
  font-weight: bold;
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
  overflow-y: auto;
  flex: 1;
}

/* 表单网格 */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.full-width {
  grid-column: span 2;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 13px;
  font-weight: 600;
  color: #555;
}

.required {
  color: #e74c3c;
}

.form-item input, .modal-select {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-item input:focus, .modal-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.input-error {
  border-color: #e74c3c !important;
}

.error-msg {
  font-size: 11px;
  color: #e74c3c;
  min-height: 14px;
}

/* 底部操作 */
.modal-footer {
  padding: 16px 25px;
  background: #f8f9fa;
  border-top: 1px solid #e0e0e0;
  text-align: right;
}

.cancel-btn {
  padding: 10px 20px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 12px;
}

.confirm-btn {
  padding: 10px 25px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: 0.3s;
}

.confirm-btn:hover {
  background: #2980b9;
}

/* 动画效果 */
@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

/* 状态标签 */
.role-badge {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.role-admin {
  background: #fff1f0;
  color: #f5222d;
  border: 1px solid #ffa39e;
}

.role-student {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 5px;
}

.status-active {
  background: #52c41a;
}

.empty-placeholder {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  width: 100%;
}

.empty-placeholder p {
  margin: 0;
  color: #999;
  font-size: 14px;
}
</style>