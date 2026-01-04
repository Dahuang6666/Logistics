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
 * @param {number} pageSize - 每页数量
 * @param {number}priority -优先级
 * @param {String} keyword -title
 * @returns {Promise}
 */
export function getAnnouncementList({ pageNum = 1, pageSize = 10, priority = null, keyword = null }) {
  const params = {
    pageNum,
    pageSize
  }

  if (priority !== null) {
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