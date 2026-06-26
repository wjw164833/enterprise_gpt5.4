/**
 * 网络请求封装
 * 基于 wx.request，支持拦截器、Token 自动注入、401 自动刷新、统一错误处理
 */

const config = require('../config')
const auth = require('./auth')

/** 正在刷新 token 的标记 */
let isRefreshing = false
/** 刷新 token 期间排队的请求 */
let pendingRequests = []

/**
 * 发起网络请求
 * @param {object} options
 * @param {string} options.url - 接口路径（不含 baseUrl）
 * @param {string} [options.method='GET'] - 请求方法
 * @param {object} [options.data] - 请求数据
 * @param {object} [options.header] - 自定义请求头
 * @param {boolean} [options.loading=true] - 是否显示加载提示
 * @param {boolean} [options.auth=true] - 是否携带 Token
 * @param {string} [options.loadingText='加载中...'] - 加载提示文字
 * @returns {Promise}
 */
function request(options) {
  const {
    url,
    method = 'GET',
    data = {},
    header = {},
    loading = true,
    authRequired = true,
    loadingText = '加载中...'
  } = options

  if (loading) {
    wx.showLoading({ title: loadingText, mask: true })
  }

  return new Promise((resolve, reject) => {
    const requestHeader = {
      'Content-Type': 'application/json',
      ...header
    }

    if (authRequired) {
      const token = auth.getToken()
      if (token) {
        requestHeader['Authorization'] = 'Bearer ' + token
      }
    }

    wx.request({
      url: config.baseUrl + url,
      method: method,
      data: data,
      header: requestHeader,
      success: function (res) {
        if (loading) {
          wx.hideLoading()
        }

        const statusCode = res.statusCode
        const responseData = res.data

        if (statusCode === 200) {
          if (responseData.code === 200) {
            resolve(responseData.data)
          } else if (responseData.code === 401) {
            handleUnauthorized(options, resolve, reject)
          } else {
            const errMsg = responseData.message || '请求失败'
            wx.showToast({ title: errMsg, icon: 'none', duration: 2000 })
            reject(new Error(errMsg))
          }
        } else if (statusCode === 401) {
          handleUnauthorized(options, resolve, reject)
        } else if (statusCode === 403) {
          wx.showToast({ title: '无权限访问', icon: 'none' })
          reject(new Error('无权限访问'))
        } else if (statusCode === 429) {
          wx.showToast({ title: '请求过于频繁，请稍后再试', icon: 'none' })
          reject(new Error('请求过于频繁'))
        } else {
          wx.showToast({ title: '网络异常，请稍后重试', icon: 'none' })
          reject(new Error('网络异常'))
        }
      },
      fail: function (err) {
        if (loading) {
          wx.hideLoading()
        }
        wx.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

/**
 * 处理 401 未授权
 * 尝试刷新 token 后重试请求，否则跳转登录
 */
function handleUnauthorized(options, resolve, reject) {
  const refreshToken = auth.getRefreshToken()

  if (!refreshToken) {
    auth.clearLoginInfo()
    redirectToLogin()
    reject(new Error('未登录'))
    return
  }

  if (!isRefreshing) {
    isRefreshing = true
    pendingRequests = []

    refreshAccessToken(refreshToken)
      .then(function (tokenData) {
        isRefreshing = false
        auth.setLoginInfo(tokenData)
        pendingRequests.forEach(function (cb) {
          cb()
        })
        pendingRequests = []
        request(options).then(resolve).catch(reject)
      })
      .catch(function () {
        isRefreshing = false
        auth.clearLoginInfo()
        pendingRequests = []
        redirectToLogin()
        reject(new Error('Token 刷新失败'))
      })
  } else {
    pendingRequests.push(function () {
      request(options).then(resolve).catch(reject)
    })
  }
}

/**
 * 刷新 accessToken
 * @param {string} refreshToken
 * @returns {Promise}
 */
function refreshAccessToken(refreshToken) {
  return new Promise(function (resolve, reject) {
    wx.request({
      url: config.baseUrl + '/auth/refresh',
      method: 'POST',
      data: { refreshToken: refreshToken },
      header: { 'Content-Type': 'application/json' },
      success: function (res) {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data)
        } else {
          reject(new Error('刷新失败'))
        }
      },
      fail: reject
    })
  })
}

/**
 * 跳转到登录
 */
function redirectToLogin() {
  wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
  setTimeout(function () {
    wx.reLaunch({ url: '/pages/mine/index' })
  }, 1500)
}

/** ===== 便捷方法 ===== */

function get(url, data, options) {
  return request({ url: url, method: 'GET', data: data, ...options })
}

function post(url, data, options) {
  return request({ url: url, method: 'POST', data: data, ...options })
}

function put(url, data, options) {
  return request({ url: url, method: 'PUT', data: data, ...options })
}

function del(url, data, options) {
  return request({ url: url, method: 'DELETE', data: data, ...options })
}

/**
 * 上传文件
 * @param {string} filePath - 本地临时文件路径
 * @param {string} [name='file'] - 文件对应 key
 * @param {object} [formData] - 额外表单数据
 * @returns {Promise}
 */
function upload(filePath, name, formData) {
  const token = auth.getToken()
  const header = {}
  if (token) {
    header['Authorization'] = 'Bearer ' + token
  }

  return new Promise(function (resolve, reject) {
    wx.uploadFile({
      url: config.uploadUrl,
      filePath: filePath,
      name: name || 'file',
      formData: formData || {},
      header: header,
      success: function (res) {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data)
        } else {
          wx.showToast({ title: data.message || '上传失败', icon: 'none' })
          reject(new Error(data.message))
        }
      },
      fail: function (err) {
        wx.showToast({ title: '上传失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  upload
}
