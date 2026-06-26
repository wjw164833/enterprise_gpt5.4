/**
 * 小程序入口 App
 * 全局生命周期、登录逻辑、全局数据
 */

const auth = require('./utils/auth')
const wxApi = require('./utils/wx-api')
const http = require('./utils/request')

App({
  /** 全局数据 */
  globalData: {
    userInfo: null,
    systemInfo: null,
    statusBarHeight: 20,
    navBarHeight: 44,
    isLogin: false
  },

  /**
   * 小程序初始化
   */
  onLaunch: function () {
    this.initSystemInfo()
    this.checkLoginStatus()
  },

  onShow: function () {
    // 检查更新
    this.checkUpdate()
  },

  onHide: function () {},

  /**
   * 初始化系统信息
   */
  initSystemInfo: function () {
    try {
      var systemInfo = wx.getSystemInfoSync()
      this.globalData.systemInfo = systemInfo
      this.globalData.statusBarHeight = systemInfo.statusBarHeight || 20

      // 计算导航栏高度
      var menuButton = wx.getMenuButtonBoundingClientRect()
      this.globalData.navBarHeight =
        menuButton.bottom + (menuButton.top - systemInfo.statusBarHeight) * 2
    } catch (e) {
      console.error('获取系统信息失败', e)
    }
  },

  /**
   * 检查登录状态
   */
  checkLoginStatus: function () {
    var isLoggedIn = auth.isLoggedIn()
    this.globalData.isLogin = isLoggedIn

    if (isLoggedIn) {
      this.globalData.userInfo = auth.getUserInfo()
      this.fetchUserInfo()
    }
  },

  /**
   * 执行微信登录
   * @returns {Promise}
   */
  wxLogin: function () {
    var that = this
    return wxApi.getWxLoginCode().then(function (code) {
      // P0-10: 修复微信登录API路径，与后端 POST /api/v1/auth/wx/mini/login 匹配
      return http.post('/auth/wx/mini/login', {
        code: code
      }, { authRequired: false, loading: false })
    }).then(function (data) {
      auth.setLoginInfo(data)
      that.globalData.isLogin = true
      return that.fetchUserInfo()
    }).catch(function (err) {
      console.error('微信登录失败', err)
      throw err
    })
  },

  /**
   * 获取用户信息
   * @returns {Promise}
   */
  fetchUserInfo: function () {
    var that = this
    return http.get('/user/info', {}, { loading: false }).then(function (data) {
      that.globalData.userInfo = data
      auth.setUserInfo(data)
      return data
    }).catch(function (err) {
      console.error('获取用户信息失败', err)
      return null
    })
  },

  /**
   * 退出登录
   */
  logout: function () {
    auth.clearLoginInfo()
    this.globalData.userInfo = null
    this.globalData.isLogin = false
    wx.reLaunch({ url: '/pages/index/index' })
  },

  /**
   * 检查小程序更新
   */
  checkUpdate: function () {
    if (wx.canIUse('getUpdateManager')) {
      var updateManager = wx.getUpdateManager()
      updateManager.onUpdateReady(function () {
        wx.showModal({
          title: '更新提示',
          content: '新版本已经准备好，是否重启应用？',
          success: function (res) {
            if (res.confirm) {
              updateManager.applyUpdate()
            }
          }
        })
      })
      updateManager.onUpdateFailed(function () {
        console.error('新版本下载失败')
      })
    }
  }
})
