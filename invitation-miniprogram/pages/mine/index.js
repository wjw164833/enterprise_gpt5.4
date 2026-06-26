/**
 * 个人中心
 * 头像昵称、订阅状态、账号绑定、关于我们、清除缓存
 */
var app = getApp()
var http = require('../../utils/request')
var auth = require('../../utils/auth')
var wxApi = require('../../utils/wx-api')

Page({
  data: {
    userInfo: null,
    isLogin: false,
    /** 订阅信息 */
    subscription: null,
    /** 菜单项 */
    menuList: [
      { key: 'subscription', icon: '👑', name: '订阅管理', desc: '' },
      { key: 'phone', icon: '📱', name: '绑定手机号', desc: '' },
      { key: 'cache', icon: '🗑️', name: '清除缓存', desc: '' },
      { key: 'about', icon: 'ℹ️', name: '关于我们', desc: '' },
      { key: 'feedback', icon: '💬', name: '意见反馈', desc: '' }
    ],
    cacheSize: '0KB'
  },

  onLoad: function () {
    this.calculateCacheSize()
  },

  onShow: function () {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 })
    }
    this.checkLoginAndLoadData()
  },

  /** 检查登录并加载数据 */
  checkLoginAndLoadData: function () {
    var isLogin = app.globalData.isLogin
    this.setData({ isLogin: isLogin })

    if (isLogin) {
      this.setData({ userInfo: app.globalData.userInfo })
      this.loadSubscription()
    }
  },

  /** 加载订阅信息 */
  loadSubscription: function () {
    var that = this
    http.get('/subscription/current', {}, { loading: false })
      .then(function (data) {
        that.setData({ subscription: data })
        var planMap = { 1: '免费版', 2: '专业版', 3: '企业版' }
        that.setData({
          'menuList[0].desc': planMap[data.plan] || '免费版'
        })
      })
      .catch(function () {})
  },

  /** 点击登录 */
  onLoginTap: function () {
    var that = this
    app.wxLogin().then(function () {
      that.checkLoginAndLoadData()
    }).catch(function () {
      wx.showToast({ title: '登录失败', icon: 'none' })
    })
  },

  /** 点击头像 */
  onAvatarTap: function () {
    var that = this
    if (!this.data.isLogin) {
      this.onLoginTap()
      return
    }

    wxApi.getUserProfile('用于更新头像和昵称').then(function (userInfo) {
      return http.put('/user/info', {
        nickname: userInfo.nickName,
        avatar: userInfo.avatarUrl
      })
    }).then(function () {
      return app.fetchUserInfo()
    }).then(function () {
      that.setData({ userInfo: app.globalData.userInfo })
      wx.showToast({ title: '更新成功', icon: 'success' })
    }).catch(function () {})
  },

  /** 菜单点击 */
  onMenuItemTap: function (e) {
    var key = e.currentTarget.dataset.key
    switch (key) {
      case 'subscription':
        wx.navigateTo({ url: '/package-extra/pages/subscription/index' })
        break
      case 'phone':
        this.bindPhone()
        break
      case 'cache':
        this.clearCache()
        break
      case 'about':
        wx.showModal({
          title: '关于我们',
          content: '邀请函 v1.0.0\n一款专业的多端邀请函创作平台',
          showCancel: false
        })
        break
      case 'feedback':
        wx.navigateTo({ url: '/package-extra/pages/feedback/index' })
        break
    }
  },

  /** 绑定手机号 */
  bindPhone: function () {
    var that = this
    if (!this.data.isLogin) {
      this.onLoginTap()
      return
    }

    wx.showModal({
      title: '绑定手机号',
      editable: true,
      placeholderText: '请输入手机号',
      success: function (res) {
        if (res.confirm && res.content) {
          var phone = res.content.trim()
          if (!/^1\d{10}$/.test(phone)) {
            wx.showToast({ title: '手机号格式不正确', icon: 'none' })
            return
          }
          http.post('/user/bind-phone', { phone: phone })
            .then(function () {
              wx.showToast({ title: '绑定成功', icon: 'success' })
              that.setData({ 'menuList[1].desc': phone })
            })
            .catch(function () {})
        }
      }
    })
  },

  /** 清除缓存 */
  clearCache: function () {
    var that = this
    wx.showModal({
      title: '提示',
      content: '确定清除所有缓存数据吗？',
      success: function (res) {
        if (res.confirm) {
          wx.clearStorageSync()
          auth.setLoginInfo({
            accessToken: auth.getToken() || '',
            refreshToken: auth.getRefreshToken() || ''
          })
          that.calculateCacheSize()
          wx.showToast({ title: '缓存已清除', icon: 'success' })
        }
      }
    })
  },

  /** 计算缓存大小 */
  calculateCacheSize: function () {
    try {
      var res = wx.getStorageInfoSync()
      var size = res.currentSize
      var sizeText = size < 1024 ? size + 'KB' : (size / 1024).toFixed(1) + 'MB'
      this.setData({ cacheSize: sizeText })
    } catch (e) {
      this.setData({ cacheSize: '0KB' })
    }
  },

  /** 退出登录 */
  onLogout: function () {
    var that = this
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: function (res) {
        if (res.confirm) {
          app.logout()
          that.setData({ isLogin: false, userInfo: null, subscription: null })
        }
      }
    })
  }
})
