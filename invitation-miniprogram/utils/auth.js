/**
 * Token 管理工具
 * 负责 accessToken 和 refreshToken 的存取、登录状态判断
 */

const TOKEN_KEY = 'invitation_access_token'
const REFRESH_TOKEN_KEY = 'invitation_refresh_token'
const USER_INFO_KEY = 'invitation_user_info'
const LOGIN_EXPIRE_KEY = 'invitation_login_expire'

/**
 * 获取 accessToken
 * @returns {string|null}
 */
function getToken() {
  return wx.getStorageSync(TOKEN_KEY) || null
}

/**
 * 设置 accessToken
 * @param {string} token
 */
function setToken(token) {
  wx.setStorageSync(TOKEN_KEY, token)
}

/**
 * 获取 refreshToken
 * @returns {string|null}
 */
function getRefreshToken() {
  return wx.getStorageSync(REFRESH_TOKEN_KEY) || null
}

/**
 * 设置 refreshToken
 * @param {string} token
 */
function setRefreshToken(token) {
  wx.setStorageSync(REFRESH_TOKEN_KEY, token)
}

/**
 * 保存登录信息（token 对）
 * @param {{accessToken: string, refreshToken: string}} data
 */
function setLoginInfo(data) {
  if (data.accessToken) {
    setToken(data.accessToken)
  }
  if (data.refreshToken) {
    setRefreshToken(data.refreshToken)
  }
  wx.setStorageSync(LOGIN_EXPIRE_KEY, Date.now() + 7200 * 1000)
}

/**
 * 清除所有登录信息
 */
function clearLoginInfo() {
  wx.removeStorageSync(TOKEN_KEY)
  wx.removeStorageSync(REFRESH_TOKEN_KEY)
  wx.removeStorageSync(USER_INFO_KEY)
  wx.removeStorageSync(LOGIN_EXPIRE_KEY)
}

/**
 * 判断是否已登录
 * @returns {boolean}
 */
function isLoggedIn() {
  const token = getToken()
  const expire = wx.getStorageSync(LOGIN_EXPIRE_KEY)
  if (!token) return false
  if (expire && Date.now() > expire) return false
  return true
}

/**
 * 获取缓存的用户信息
 * @returns {object|null}
 */
function getUserInfo() {
  const info = wx.getStorageSync(USER_INFO_KEY)
  return info || null
}

/**
 * 缓存用户信息
 * @param {object} userInfo
 */
function setUserInfo(userInfo) {
  wx.setStorageSync(USER_INFO_KEY, userInfo)
}

/**
 * 检查 token 是否即将过期（5分钟内）
 * @returns {boolean}
 */
function isTokenExpiringSoon() {
  const expire = wx.getStorageSync(LOGIN_EXPIRE_KEY)
  if (!expire) return true
  return Date.now() > (expire - 5 * 60 * 1000)
}

module.exports = {
  getToken,
  setToken,
  getRefreshToken,
  setRefreshToken,
  setLoginInfo,
  clearLoginInfo,
  isLoggedIn,
  getUserInfo,
  setUserInfo,
  isTokenExpiringSoon
}
