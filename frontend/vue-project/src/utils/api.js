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
 * @returns {Promise}
 */
export function getAnnouncementList(pageNum, pageSize) {
  return request.get('/school/student/announcementList', {
    params: {
      pageNum: pageNum || 1,
      pageSize: pageSize || 10
    }
  });
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
