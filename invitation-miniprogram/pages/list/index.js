/**
 * 我的邀请函列表
 * Tab切换（全部/草稿/已发布/已过期），下拉刷新，上拉加载
 */
var http = require('../../utils/request')
var util = require('../../utils/util')
var app = getApp()

Page({
  data: {
    /** Tab 列表 */
    tabs: [
      { key: -1, name: '全部' },
      { key: 0, name: '草稿' },
      { key: 1, name: '已发布' },
      { key: 2, name: '已下架' }
    ],
    currentTab: 0,
    /** 邀请函列表 */
    list: [],
    /** 分页 */
    page: 1,
    size: 10,
    hasMore: true,
    /** 骨架屏 */
    loading: true,
    /** 刷新中 */
    refreshing: false
  },

  onLoad: function () {
    this.loadList()
  },

  onShow: function () {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 })
    }
    // 每次显示时刷新
    this.refreshList()
  },

  onPullDownRefresh: function () {
    this.refreshList().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom: function () {
    if (this.data.hasMore && !this.data.loading) {
      this.loadList()
    }
  },

  onShareAppMessage: function () {
    return {
      title: '邀请函 - 我的邀请函列表',
      path: '/pages/list/index'
    }
  },

  /** 切换 Tab */
  onTabChange: function (e) {
    var index = e.currentTarget.dataset.index
    if (index === this.data.currentTab) return

    this.setData({
      currentTab: index,
      list: [],
      page: 1,
      hasMore: true
    })
    this.loadList()
  },

  /** 刷新列表 */
  refreshList: function () {
    this.setData({
      page: 1,
      hasMore: true,
      list: []
    })
    return this.loadList()
  },

  /** 加载列表 */
  loadList: function () {
    var that = this
    var currentTab = that.data.currentTab
    var statusParam = that.data.tabs[currentTab].key

    if (!app.globalData.isLogin) {
      that.setData({ loading: false, list: [] })
      return Promise.resolve()
    }

    that.setData({ loading: true })

    var params = {
      page: that.data.page,
      size: that.data.size
    }
    if (statusParam >= 0) {
      params.status = statusParam
    }

    return http.get('/invitation/my', params, { loading: false })
      .then(function (data) {
        var records = data.records || data || []
        records.forEach(function (item) {
          item.activityTypeName = util.getActivityTypeName(item.activityType)
          item.statusText = util.getInvitationStatusName(item.status)
          item.statusClass = util.getInvitationStatusClass(item.status)
          item.formattedDate = util.formatDate(item.activityDate, 'yyyy-MM-dd HH:mm')
        })

        var newList = that.data.page === 1 ? records : that.data.list.concat(records)
        var hasMore = records.length >= that.data.size

        that.setData({
          list: newList,
          hasMore: hasMore,
          page: that.data.page + 1,
          loading: false
        })
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  /** 点击邀请函卡片 */
  onItemTap: function (e) {
    var id = e.currentTarget.dataset.id
    var status = e.currentTarget.dataset.status
    if (status === 0) {
      wx.navigateTo({
        url: '/package-invitation/pages/step-form/index?id=' + id + '&mode=edit'
      })
    } else {
      wx.navigateTo({
        url: '/pages/detail/index?id=' + id
      })
    }
  },

  /** 点击编辑 */
  onItemEdit: function (e) {
    var id = e.detail.item.id
    wx.navigateTo({
      url: '/package-invitation/pages/step-form/index?id=' + id + '&mode=edit'
    })
  },

  /** 点击分享 */
  onItemShare: function (e) {
    var item = e.detail.item
    wx.navigateTo({
      url: '/pages/detail/index?id=' + item.id + '&action=share'
    })
  },

  /** 创建新邀请函 */
  onCreateTap: function () {
    wx.navigateTo({
      url: '/package-invitation/pages/create/index'
    })
  }
})
