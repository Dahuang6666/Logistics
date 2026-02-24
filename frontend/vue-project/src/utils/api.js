import request from '@/utils/request.js'

/**
 * 用户登录
 * @param {Object} data - 登录数据 { username, password, role }
 * @returns {Promise} Axios 响应 Promise
 */
export function login(data) {
  return request.post('/school/user/login', data);
}

/**
 * 用户注册
 * @param {Object} data
 * @returns {*}
 */
export function register(data) {
  return request.post('/school/user/register', data);
}

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request.get('/school/user/captcha');
}

/**
 * 验证验证码
 */

export function verifyCaptcha(data) {
  return request.post('/school/user/verifyCaptcha' , data);
}


/**
 * 获取邮箱
 * @param {string} userNo - 用户账号
 */
export function getEmail(userNo) {
  return request.get('/school/user/getEmail', {
    params: {
      userNo: userNo
    }
  });
}

/**
 * 发送邮件验证码
 * @param {string} userNo - 用户账号
 */
export function sendEmail(userNo) {
  return request.get('/school/user/sendEmail', {
    params: {
      userNo: userNo
    }
  });
}

/**
 * 验证邮件
 * @param userNo
 * @param code
 */
export function verifyEmail(userNo, code) {
  return request.post('/school/user/verifyEmail', {
    userNo, code
  });
}

/**
 * 更新密码
 * @param userNo
 * @param password
 */
export function updatePassword(userNo, password) {
  return request.put('/school/user/updateUserInfo', {
    userNo, password
  });
}

/**
 * 获取公告列表
 * @param {number} pageNum - 页码
 * @param {{pageSize: number}} pageSize - 每页数量
 * @param {{priority: *}}priority -优先级
 * @param keyword -查询关键词
 * @returns {Promise}
 */
// 获取公告列表
export function getAnnouncementList(pageNum, pageSize, priority, keyword) {
  const params = {
    pageNum,
    pageSize
  }

  if (priority !== null && priority !== undefined) {
    params.priority = priority
  }

  if (keyword && keyword.trim() !== '') {
    params.keyword = keyword.trim()
  }

  return request.get('/school/student/announcementList', {
    params
  })
}


/**
 * 获取用户名
 *@Param {String} userNo
 */
export function getUserName(userNo) {
  return request.get('/school/user/getUserName', {
    params: {
      userNo: userNo
    }
  });
}
/**
 * 提交投诉建议
 * @param {Object} data - {userNo, content}
 */
export function submitFeedback(data) {
  return request.post('/school/student/submitSuggest', data);
}

/**
 * 获取我的投诉建议列表
 * @param {string} userNo - 用户学号
 */
export function getMyFeedbackList(userNo) {
  return request.get('/school/student/mySuggestList', {
    params: {
      userNo: userNo
    }
  });
}

/**
 * 检查是否首次登录
 * @param {string} userNo
 */
export function checkFirstLogin(userNo) {
  return request.get('/school/student/firstLoginInfo', {
    params: { userNo }
  });
}

/**
 * 获取可选宿舍楼(根据性别)
 * @param {string} userNo
 */
export function getAvailableBuildings(userNo) {
  return request.get('/school/student/getAvailableBuildings', {
    params: { userNo }
  });
}

/**
 * 获取可用宿舍列表(根据楼号)
 * @param {number} buildingId
 */
export function getAvailableDorms(buildingId) {
  return request.get('/school/student/getAvailableDorms', {
    params: { buildingId }
  });
}

/**
 * 提交首次宿舍信息
 * @param {Object} data - { userNo, buildingId, dormitoryNo }
 */
export function submitFirstDormInfo(data) {
  return request.post('/school/student/submitFirstDormInfo', data);
}

/**
 * 获取学生个人资料(含宿舍和室友信息)
 * @param {string} userNo - 学号
 */
export function getStudentProfile(userNo) {
  return request.get('/school/student/getStudentProfile', {
    params: { userNo }
  })
}

/**
 * 上传用户头像
 * @param {string} userNo - 用户学号/工号
 * @param {File} file - 头像图片文件
 * @returns {Promise}
 */
export function uploadAvatar(userNo, file) {
  const formData = new FormData();
  formData.append('userNo', userNo);
  formData.append('file', file);
  return request.post('/school/user/uploadAvatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 获取用户头像 URL
 * @param {string} userNo - 用户学号/工号
 * @returns {Promise}
 */
export function getAvatarUrl(userNo) {
  return request.get('/school/user/avatar', {
    params: {
      userNo: userNo
    }
  });
}

/**
 * 更新用户信息(姓名、手机、邮箱)
 * @param {Object} data - { userNo, username, phone, email }
 */
export function updateUserProfile(data) {
  return request.put('/school/user/updateUserInfo', data);
}

/**
 * 上传报修图片 (新增功能，根据后端代码)
 * @param {File} file - 报修图片文件
 * @returns {Promise}
 */
export function uploadRepairImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/school/student/uploadAvatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}


/**
 * 提交报修申请
 * @param {Object} data - { userNo, dormitoryId, content, imageUrl}
 * @returns {Promise}
 */
export function submitRepair(data) {
  return request.post('/school/student/submitRepair', data);
}

/**
 * 根据学号获取宿舍ID
 * @param {string} userNo - 学号
 * @returns {Promise}
 */
export function getDormitoryId(userNo) {
  return request.get('/school/student/getDormitoryId', {
    params: { userNo }
  });
}

  /**
   * 获取我的报修列表(分页)
   * @param {Object} params - { userNo, pageNum, pageSize, status? }
   */
  export function getMyRepairs(params) {
    return request.get('/school/student/myRepairs', { params });
  }

  /**
   * 撤销报修
   * @param {number} repairId - 报修单ID
   * @param {string} userNo - 用户学号
   */
  export function cancelRepair(repairId, userNo) {
    return request.put('/school/student/cancelRepair', null, {
      params: { repairId, userNo }
    });
  }


/**
 * 提交换宿申请
 * @param {Object} data - { studentNo, currentDormitoryId, targetDormitoryId, reason }
 * @returns {Promise}
 */
export function submitDormChangeApplication(data) {
  return request.post('/school/student/application', data);
}

/**
 * 获取我的换宿申请列表
 * @returns {Promise}
 * @param studentNo 学号
 */
export function getMyDormApplications(studentNo) {
  return request.get('/school/student/getMyDormApplications', {
    params: { studentNo }
  });
}

/**
 * 撤销换宿申请
 * @param {number} applicationId - 申请ID
 * @param {string} studentNo - 学号
 * @returns {Promise}
 */
export function cancelDormApplication(applicationId, studentNo) {
  return request.put('/school/student/cancelDormApplication', null, {
    params: { applicationId, studentNo }
  });
}

/**
 * 获取宿舍信息
 * @param {string} studentNo - 学号
 * @returns {Promise}
 */
export function getCurrentDormInfo(studentNo) {
  return request.get('/school/student/getCurrentDormInfo', {
    params: { studentNo }
  });
}



/**
 * 获取宿舍信息
 * @param {string} dormitoryId - 宿舍id
 * @returns {Promise}
 */
export function getDormNameById(dormitoryId) {
  return request.get('/school/student/getDormNameById', {
    params: {dormitoryId }
  });
}

/**
 * 管理员：获取所有用户列表
 */
export function getAdminUserList() {
  return request.get('/school/admin/getListUser');
}

/**
 * 管理员：创建新用户
 * @param {Object} data - User 对象数据
 */
export function createAdminUser(data) {
  return request.post('/school/admin/createUser', data);
}

/**
 * 管理员：更新用户信息
 * @param {Object} data - User 对象数据
 */
export function updateAdminUser(data) {
  return request.put('/school/admin/updateUser', data);
}

/**
 * 管理员：删除用户
 * @param {number} id - 用户 ID
 */
export function deleteAdminUser(id) {
  return request.delete(`/school/admin/deleteUser/${id}`);
}

/**
 * 管理员：根据 ID 查询单个用户
 * @param {number} id
 */
export function getAdminUserById(id) {
  return request.get(`/school/admin/${id}`);
}