/**
 * 微信 API Promise 化封装
 * 统一将 wx.xxx 回调风格 API 转为 Promise
 */

/**
 * wx.login Promise 版
 * @returns {Promise<string>} code
 */
function login() {
  return new Promise(function (resolve, reject) {
    wx.login({
      success: function (res) { resolve(res.code) },
      fail: reject
    })
  })
}

/**
 * wx.getUserProfile Promise 版
 * @param {string} [desc='用于完善邀请函信息']
 * @returns {Promise<object>}
 */
function getUserProfile(desc) {
  return new Promise(function (resolve, reject) {
    wx.getUserProfile({
      desc: desc || '用于完善邀请函信息',
      success: function (res) { resolve(res.userInfo) },
      fail: reject
    })
  })
}

/**
 * wx.getSetting Promise 版
 * @returns {Promise<object>}
 */
function getSetting() {
  return new Promise(function (resolve, reject) {
    wx.getSetting({
      success: function (res) { resolve(res.authSetting) },
      fail: reject
    })
  })
}

/**
 * wx.chooseImage Promise 版
 * @param {object} [options]
 * @param {number} [options.count=9]
 * @param {string[]} [options.sizeType=['compressed']]
 * @param {string[]} [options.sourceType=['album','camera']]
 * @returns {Promise<string[]>} 临时文件路径数组
 */
function chooseImage(options) {
  options = options || {}
  return new Promise(function (resolve, reject) {
    wx.chooseImage({
      count: options.count || 9,
      sizeType: options.sizeType || ['compressed'],
      sourceType: options.sourceType || ['album', 'camera'],
      success: function (res) { resolve(res.tempFilePaths) },
      fail: reject
    })
  })
}

/**
 * wx.chooseMedia Promise 版（微信新版图片选择）
 * @param {object} [options]
 * @param {number} [options.count=9]
 * @param {string} [options.mediaType='image']
 * @returns {Promise<object[]>}
 */
function chooseMedia(options) {
  options = options || {}
  return new Promise(function (resolve, reject) {
    wx.chooseMedia({
      count: options.count || 9,
      mediaType: options.mediaType || ['image'],
      sourceType: options.sourceType || ['album', 'camera'],
      success: function (res) { resolve(res.tempFiles) },
      fail: reject
    })
  })
}

/**
 * wx.getLocation Promise 版
 * @param {string} [type='gcj02']
 * @returns {Promise<object>}
 */
function getLocation(type) {
  return new Promise(function (resolve, reject) {
    wx.getLocation({
      type: type || 'gcj02',
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.openLocation Promise 版
 * @param {object} options
 * @returns {Promise}
 */
function openLocation(options) {
  return new Promise(function (resolve, reject) {
    wx.openLocation({
      latitude: options.latitude,
      longitude: options.longitude,
      name: options.name || '',
      address: options.address || '',
      scale: options.scale || 18,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.saveImageToPhotosAlbum Promise 版
 * @param {string} filePath
 * @returns {Promise}
 */
function saveImageToPhotosAlbum(filePath) {
  return new Promise(function (resolve, reject) {
    wx.saveImageToPhotosAlbum({
      filePath: filePath,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.getSystemInfo Promise 版
 * @returns {Promise<object>}
 */
function getSystemInfo() {
  return new Promise(function (resolve, reject) {
    wx.getSystemInfo({
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.makePhoneCall Promise 版
 * @param {string} phoneNumber
 * @returns {Promise}
 */
function makePhoneCall(phoneNumber) {
  return new Promise(function (resolve, reject) {
    wx.makePhoneCall({
      phoneNumber: phoneNumber,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.setClipboardData Promise 版
 * @param {string} data
 * @returns {Promise}
 */
function setClipboardData(data) {
  return new Promise(function (resolve, reject) {
    wx.setClipboardData({
      data: data,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.showModal Promise 版
 * @param {object} options
 * @returns {Promise<boolean>} confirm 为 true
 */
function showModal(options) {
  return new Promise(function (resolve) {
    wx.showModal({
      title: options.title || '提示',
      content: options.content || '',
      showCancel: options.showCancel !== false,
      confirmText: options.confirmText || '确定',
      cancelText: options.cancelText || '取消',
      success: function (res) { resolve(res.confirm) },
      fail: function () { resolve(false) }
    })
  })
}

/**
 * wx.requestPayment Promise 版
 * @param {object} payParams
 * @returns {Promise}
 */
function requestPayment(payParams) {
  return new Promise(function (resolve, reject) {
    wx.requestPayment({
      timeStamp: payParams.timeStamp,
      nonceStr: payParams.nonceStr,
      package: payParams.package,
      signType: payParams.signType || 'MD5',
      paySign: payParams.paySign,
      success: resolve,
      fail: reject
    })
  })
}

/**
 * wx.previewImage Promise 版
 * @param {object} options
 * @returns {Promise}
 */
function previewImage(options) {
  return new Promise(function (resolve) {
    wx.previewImage({
      current: options.current || '',
      urls: options.urls || [],
      complete: resolve
    })
  })
}

/**
 * 检查并请求授权
 * @param {string} scope - 如 scope.writePhotosAlbum
 * @returns {Promise<boolean>}
 */
function authorize(scope) {
  return getSetting().then(function (authSetting) {
    if (authSetting[scope] === true) {
      return true
    }
    if (authSetting[scope] === false) {
      // 已拒绝，引导去设置
      return showModal({
        title: '授权提示',
        content: '需要您授权才能使用该功能，是否前往设置开启？'
      }).then(function (confirm) {
        if (confirm) {
          wx.openSetting()
        }
        return false
      })
    }
    // 未授权过，发起请求
    return new Promise(function (resolve) {
      wx.authorize({
        scope: scope,
        success: function () { resolve(true) },
        fail: function () { resolve(false) }
      })
    })
  })
}

/**
 * 获取微信登录 code（用于后端微信登录）
 * @returns {Promise<string>}
 */
function getWxLoginCode() {
  return login()
}

module.exports = {
  login,
  getUserProfile,
  getSetting,
  chooseImage,
  chooseMedia,
  getLocation,
  openLocation,
  saveImageToPhotosAlbum,
  getSystemInfo,
  makePhoneCall,
  setClipboardData,
  showModal,
  requestPayment,
  previewImage,
  authorize,
  getWxLoginCode
}
