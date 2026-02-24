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
        <span class="search-icon">🔍</span>
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="搜索姓名、学号或工号..."
          class="search-input"
        >
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
          <th>邮箱</th>
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
          <td class="email-cell">{{ user.email || '-' }}</td>
          <td>
            <div class="status-wrapper">
              <span :class="['status-dot', user.status === 1 ? 'status-active' : 'status-disabled']"></span>
              {{ user.status === 1 ? '正常' : '禁用' }}
            </div>
          </td>
          <td class="action-cells">
            <button class="btn-text edit" @click="handleEdit(user)">编辑</button>
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
        <div class="modal-content">
          <div class="modal-header">
            <div class="title-area">
              <span class="title-icon">{{ isEdit ? '📝' : '👤' }}</span>
              <h3>{{ isEdit ? '修改用户信息' : '创建新账号' }}</h3>
            </div>
            <span class="close-icon" @click="closeDialog">&times;</span>
          </div>

          <div class="modal-body">
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
                <div class="select-wrapper">
                  <select v-model="form.role">
                    <option :value="1">学生</option>
                    <option :value="4">系统管理员</option>
                  </select>
                </div>
              </div>
              <div class="form-item">
                <label>性别</label>
                <div class="select-wrapper">
                  <select v-model="form.gender">
                    <option value="男">男</option>
                    <option value="女">女</option>
                  </select>
                </div>
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
  getAdminUserList,
  createAdminUser,
  updateAdminUser,
  deleteAdminUser
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
        id: null,
        username: '',
        userNo: '',
        password: '',
        role: 1,
        gender: '男',
        phone: '',
        email: '',
        status: 1
      },
      // 错误提示状态
      errors: {
        username: '',
        userNo: '',
        password: '',
        phone: '',
        email: ''
      }
    }
  },
  computed: {
    filteredUserList() {
      const k = this.searchKeyword.toLowerCase()

      // 1. 先进行关键词过滤
      let list = this.userList.filter(u =>
        (u.username && u.username.toLowerCase().includes(k)) ||
        (u.userNo && u.userNo.toLowerCase().includes(k))
      )

      // 2. 进行排序：管理员(4) 在前，学生(1) 在后
      // 逻辑：b.role - a.role 会按照数值从大到小排列 (4 > 1)
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
        if (res.data.code === 1) {
          this.userList = res.data.data
        }
      } catch (e) {
        ElMessage.error('无法连接到服务器')
      }
    },
    // 表单校验逻辑
    validateForm() {
      let isValid = true;
      this.clearErrors();

      if (!this.form.username) { this.errors.username = '姓名不能为空'; isValid = false; }
      if (!this.form.userNo) { this.errors.userNo = '学工号不能为空'; isValid = false; }
      if (!this.isEdit && !this.form.password) { this.errors.password = '初始密码不能为空'; isValid = false; }

      // 手机号正则 (中国大陆11位)
      if (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) {
        this.errors.phone = '请输入有效的11位手机号';
        isValid = false;
      }

      // 邮箱正则
      if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
        this.errors.email = '邮箱格式不正确';
        isValid = false;
      }

      return isValid;
    },
    // 单个字段实时校验
    validateField(field) {
      if (field === 'phone') {
        this.errors.phone = (this.form.phone && !/^1[3-9]\d{9}$/.test(this.form.phone)) ? '手机号格式错误' : '';
      }
      if (field === 'email') {
        this.errors.email = (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) ? '邮箱格式错误' : '';
      }
    },
    clearErrors() {
      Object.keys(this.errors).forEach(k => this.errors[k] = '');
    },
    openAddDialog() {
      this.isEdit = false;
      this.clearErrors();
      this.form = { id: null, username: '', userNo: '', password: '', role: 1, gender: '男', status: 1, phone: '', email: '' };
      this.showDialog = true;
    },
    handleEdit(user) {
      this.isEdit = true;
      this.clearErrors();
      this.form = { ...user };
      this.showDialog = true;
    },
    closeDialog() { this.showDialog = false },
    async submitForm() {
      if (!this.validateForm()) return;

      try {
        const res = this.isEdit ? await updateAdminUser(this.form) : await createAdminUser(this.form);
        if (res.data.code === 1) {
          ElMessage.success('数据保存成功');
          this.closeDialog();
          this.loadUsers();
        } else {
          ElMessage.error(res.data.msg || '保存失败');
        }
      } catch (e) { ElMessage.error('服务器响应错误'); }
    },
    handleDelete(user) {
      ElMessageBox.confirm(`确定要彻底删除用户 [${user.username || user.userNo}] 吗？删除后不可恢复。`, '危险操作', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }).then(async () => {
        const res = await deleteAdminUser(user.id);
        if (res.data.code === 1) {
          ElMessage.success('删除成功');
          this.loadUsers();
        }
      })
    }
  }
}
</script>

<style scoped>
/* 全局容器 */
.user-management-container {
  padding: 20px;
  background: #fcfcfc;
  min-height: 100%;
}

/* 页头美化 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
}

.page-title {
  font-size: 26px;
  color: #1a1a1a;
  margin-bottom: 5px;
}

.page-subtitle {
  color: #888;
  font-size: 14px;
}

.add-btn {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  transition: all 0.3s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(52, 152, 219, 0.4);
}

/* 搜索框美化 */
.filter-bar {
  margin-bottom: 25px;
}

.search-wrapper {
  position: relative;
  width: 320px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
}

.search-input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  background: white;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

/* 表格卡片化 */
.table-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  overflow: hidden;
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
  color: #444;
  font-size: 14px;
}

.user-no-tag {
  background: #f0f2f5;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: monospace;
  color: #555;
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
}

/* 角色与状态 */
.role-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
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

.status-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-active {
  background: #52c41a;
  box-shadow: 0 0 5px rgba(82, 196, 26, 0.5);
}

.status-disabled {
  background: #bfbfbf;
}

/* 操作按钮 */
.btn-text {
  background: none;
  border: none;
  cursor: pointer;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  transition: 0.2s;
}

.btn-text.edit {
  color: #1890ff;
}

.btn-text.edit:hover {
  background: #e6f7ff;
}

.btn-text.delete {
  color: #ff4d4f;
}

.btn-text.delete:hover {
  background: #fff1f0;
}

/* --- 弹窗美化 --- */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  width: 560px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
  overflow: hidden;
  animation: modal-up 0.4s cubic-bezier(0.18, 0.89, 0.32, 1.28);
}

@keyframes modal-up {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.title-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 20px;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1a1a1a;
}

.close-icon {
  font-size: 24px;
  color: #aaa;
  cursor: pointer;
  transition: 0.2s;
}

.close-icon:hover {
  color: #333;
}

.modal-body {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

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
  gap: 6px;
}

.form-item label {
  font-size: 13px;
  font-weight: 600;
  color: #555;
}

.required {
  color: #ff4d4f;
}

.form-item input, .form-item select {
  padding: 10px 14px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-item input:focus, .form-item select:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

.input-error {
  border-color: #ff4d4f !important;
  background: #fff2f0;
}

.error-msg {
  font-size: 11px;
  color: #ff4d4f;
  min-height: 14px;
}

/* 状态单选框美化 */
.radio-group {
  display: flex;
  gap: 20px;
  padding: 5px 0;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  cursor: pointer;
}

.modal-footer {
  padding: 16px 24px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.cancel-btn {
  background: #fff;
  border: 1px solid #d9d9d9;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  margin-right: 12px;
  transition: 0.2s;
}

.cancel-btn:hover {
  background: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: 0.2s;
}

.confirm-btn:hover {
  background: #2980b9;
}

.empty-placeholder {
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 16px;
}
</style>