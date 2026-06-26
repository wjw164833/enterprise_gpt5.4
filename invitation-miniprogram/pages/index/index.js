/**
 * 首页
 * 模板展示区（轮播）、最近邀请函列表、创建新邀请函大按钮、快捷入口
 */
var http = require('../../utils/request')
var util = require('../../utils/util')
var app = getApp()

Page({
  data: {
    /** 搜索关键词 */
    searchKey: '',
    /** 轮播模板列表 */
    bannerList: [],
    /** 最近邀请函列表 */
    recentList: [],
    /** 推荐模板 */
    hotTemplates: [],
    /** 骨架屏 */
    loading: true,
    /** 轮播当前索引 */
    currentBanner: 0
  },

  onLoad: function () {
    this.initPage()
  },

  onShow: function () {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 })
    }
  },

  onPullDownRefresh: function () {
    this.initPage().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  /** 分享 */
  onShareAppMessage: function () {
    return {
      title: '邀请函 - 轻松创建精美邀请函',
      path: '/pages/index/index'
    }
  },

  onShareTimeline: function () {
    return {
      title: '邀请函 - 轻松创建精美邀请函'
    }
  },

  /** 初始化页面数据 */
  initPage: function () {
    var that = this
    that.setData({ loading: true })

    return Promise.all([
      that.loadBanners(),
      that.loadRecentList(),
      that.loadHotTemplates()
    ]).finally(function () {
      that.setData({ loading: false })
    })
  },

  /** 加载轮播数据 */
  loadBanners: function () {
    var that = this
    return http.get('/template/list', { pageSize: 5, status: 1 }, { loading: false })
      .then(function (data) {
        that.setData({ bannerList: data.records || data || [] })
      })
      .catch(function () {
        that.setData({ bannerList: [] })
      })
  },

  /** 加载最近邀请函 */
  loadRecentList: function () {
    var that = this
    var globalData = app.globalData

    if (!globalData.isLogin) {
      that.setData({ recentList: [] })
      return Promise.resolve()
    }

    return http.get('/invitation/my', { page: 1, size: 4 }, { loading: false })
      .then(function (data) {
        var list = data.records || data || []
        list.forEach(function (item) {
          item.activityTypeName = util.getActivityTypeName(item.activityType)
          item.statusText = util.getInvitationStatusName(item.status)
          item.statusClass = util.getInvitationStatusClass(item.status)
        })
        that.setData({ recentList: list })
      })
      .catch(function () {
        that.setData({ recentList: [] })
      })
  },

  /** 加载热门模板 */
  loadHotTemplates: function () {
    var that = this
    return http.get('/template/list', { pageSize: 8, status: 1 }, { loading: false })
      .then(function (data) {
        that.setData({ hotTemplates: data.records || data || [] })
      })
      .catch(function () {
        that.setData({ hotTemplates: [] })
      })
  },

  /** 搜索输入 */
  onSearchInput: function (e) {
    this.setData({ searchKey: e.detail.value })
  },

  /** 搜索确认 */
  onSearchConfirm: function () {
    var key = this.data.searchKey.trim()
    if (!key) return
    wx.navigateTo({
      url: '/package-invitation/pages/step-template/index?searchKey=' + encodeURIComponent(key)
    })
  },

  /** 轮播变化 */
  onBannerChange: function (e) {
    this.setData({ currentBanner: e.detail.current })
  },

  /** 点击轮播模板 */
  onBannerTap: function (e) {
    var id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/package-invitation/pages/step-template/index?previewId=' + id
    })
  },

  /** 点击创建按钮 */
  onCreateTap: function () {
    wx.navigateTo({
      url: '/package-invitation/pages/create/index'
    })
  },

  /** 点击邀请函卡片 */
  onInvitationTap: function (e) {
    var id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/detail/index?id=' + id
    })
  },

  /** 点击模板 */
  onTemplateTap: function (e) {
    var id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/package-invitation/pages/step-template/index?previewId=' + id
    })
  },

  /** 快捷入口：婚礼 */
  onQuickWedding: function () {
    wx.navigateTo({
      url: '/package-invitation/pages/create/index?type=1'
    })
  },

  /** 快捷入口：生日 */
  onQuickBirthday: function () {
    wx.navigateTo({
      url: '/package-invitation/pages/create/index?type=2'
    })
  },

  /** 快捷入口：商务 */
  onQuickBusiness: function () {
    wx.navigateTo({
      url: '/package-invitation/pages/create/index?type=4'
    })
  },

  /** 查看更多邀请函 */
  onViewMore: function () {
    wx.switchTab({
      url: '/pages/list/index'
    })
  }
})
