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
    userNo,
    code
  });
}