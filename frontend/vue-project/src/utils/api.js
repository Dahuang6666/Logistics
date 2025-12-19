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
 * @param data
 * @returns {*}
 */
export function register(data) {
  return request.post('/school/user/register', data);
}