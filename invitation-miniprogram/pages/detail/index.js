/**
 * 邀请函详情（主人视角）
 * 统计数据卡片、分享按钮、编辑入口、宾客回复列表
 */
var http = require('../../utils/request')
var util = require('../../utils/util')
var app = getApp()

Page({
  data: {
    id: 0,
    detail: null,
    /** 统计 */
    stats: null,
    /** 回复列表 */
    replies: [],
    replyPage: 1,
    replyHasMore: true,
    /** 加载状态 */
    loading: true,
    /** 是否显示海报组件 */
    showPoster: false,
    /** 操作面板 */
    showActionSheet: false,
    actionList: [
      { key: 'edit', name: '编辑', icon: '✏️' },
      { key: 'share', name: '分享', icon: '🔗' },
      { key: 'poster', name: '生成海报', icon: '🖼️' },
      { key: 'qrcode', name: '小程序码', icon: '📱' },
      { key: 'unpublish', name: '下架', icon: '⏸️' },
      { key: 'delete', name: '删除', icon: '🗑️' }
    ]
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ id: options.id })
      this.loadDetail()
      this.loadStats()
      this.loadReplies()
    }
  },

  onPullDownRefresh: function () {
    var that = this
    Promise.all([
      that.loadDetail(),
      that.loadStats(),
      that.refreshReplies()
    ]).finally(function () {
      wx.stopPullDownRefresh()
    })
  },

  onShareAppMessage: function () {
    var detail = this.data.detail
    return util.getShareConfig(detail || {})
  },

  onShareTimeline: function () {
    var detail = this.data.detail
    return {
      title: detail ? detail.title : '邀请函',
      query: 'shareCode=' + (detail ? detail.shortCode : '')
    }
  },

  /** 加载邀请函详情 */
  loadDetail: function () {
    var that = this
    return http.get('/invitation/' + that.data.id)
      .then(function (data) {
        data.activityTypeName = util.getActivityTypeName(data.activityType)
        data.statusText = util.getInvitationStatusName(data.status)
        data.formattedDate = util.formatDate(data.activityDate, 'yyyy年MM月dd日 HH:mm')
        that.setData({ detail: data, loading: false })
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  /** 加载统计数据 */
  loadStats: function () {
    var that = this
    return http.get('/invitation/' + that.data.id + '/stats', {}, { loading: false })
      .then(function (data) {
        that.setData({ stats: data })
      })
      .catch(function () {})
  },

  /** 加载回复列表 */
  loadReplies: function () {
    var that = this
    return http.get('/invitation/' + that.data.id + '/replies', {
      page: that.data.replyPage,
      size: 20
    }, { loading: false }).then(function (data) {
      var records = data.records || data || []
      records.forEach(function (item) {
        item.replyStatusText = util.getReplyStatusName(item.replyStatus)
        item.replyStatusClass = util.getReplyStatusClass(item.replyStatus)
        item.formattedTime = util.formatRelativeTime(item.createdAt)
      })

      var newReplies = that.data.replyPage === 1
        ? records
        : that.data.replies.concat(records)

      that.setData({
        replies: newReplies,
        replyHasMore: records.length >= 20,
        replyPage: that.data.replyPage + 1
      })
    }).catch(function () {})
  },

  /** 刷新回复 */
  refreshReplies: function () {
    this.setData({ replyPage: 1, replies: [], replyHasMore: true })
    return this.loadReplies()
  },

  /** 点击更多操作 */
  onMoreAction: function () {
    this.setData({ showActionSheet: true })
  },

  /** 操作项点击 */
  onActionTap: function (e) {
    var key = e.currentTarget.dataset.key
    this.setData({ showActionSheet: false })

    switch (key) {
      case 'edit':
        wx.navigateTo({
          url: '/package-invitation/pages/step-form/index?id=' + this.data.id + '&mode=edit'
        })
        break
      case 'share':
        this.onShare()
        break
      case 'poster':
        this.generatePoster()
        break
      case 'qrcode':
        this.generateQrcode()
        break
      case 'unpublish':
        this.togglePublish()
        break
      case 'delete':
        this.deleteInvitation()
        break
    }
  },

  /** 关闭操作面板 */
  onActionSheetClose: function () {
    this.setData({ showActionSheet: false })
  },

  /** 分享 */
  onShare: function () {
    // 触发微信分享（onShareAppMessage 会自动调用）
  },

  /** 生成海报 */
  generatePoster: function () {
    this.setData({ showPoster: true })
  },

  /** 关闭海报 */
  onPosterClose: function () {
    this.setData({ showPoster: false })
  },

  /** 生成小程序码 */
  generateQrcode: function () {
    var that = this
    http.post('/invitation/' + that.data.id + '/qrcode', {})
      .then(function (data) {
        wx.previewImage({
          current: data.url,
          urls: [data.url]
        })
      })
      .catch(function () {
        wx.showToast({ title: '生成失败', icon: 'none' })
      })
  },

  /** 发布/下架切换 */
  togglePublish: function () {
    var that = this
    var detail = that.data.detail
    if (!detail) return

    if (detail.status === 1) {
      wx.showModal({
        title: '提示',
        content: '确定下架此邀请函吗？下架后宾客将无法查看',
        success: function (res) {
          if (res.confirm) {
            http.put('/invitation/' + that.data.id + '/status', { status: 2 })
              .then(function () {
                wx.showToast({ title: '已下架', icon: 'success' })
                that.loadDetail()
              })
          }
        }
      })
    } else {
      http.put('/invitation/' + that.data.id + '/status', { status: 1 })
        .then(function () {
          wx.showToast({ title: '发布成功', icon: 'success' })
          that.loadDetail()
        })
    }
  },

  /** 删除邀请函 */
  deleteInvitation: function () {
    var that = this
    wx.showModal({
      title: '警告',
      content: '删除后无法恢复，确定删除吗？',
      confirmColor: '#FA5151',
      success: function (res) {
        if (res.confirm) {
          http.del('/invitation/' + that.data.id)
            .then(function () {
              wx.showToast({ title: '已删除', icon: 'success' })
              setTimeout(function () {
                wx.navigateBack()
              }, 1500)
            })
        }
      }
    })
  },

  /** 跳转席位管理 */
  onSeatTap: function () {
    wx.navigateTo({
      url: '/package-extra/pages/seat/index?id=' + this.data.id
    })
  },

  /** 跳转数据分析 */
  onAnalyticsTap: function () {
    wx.navigateTo({
      url: '/package-extra/pages/analytics/index?id=' + this.data.id
    })
  },

  /** 跳转聊天室 */
  onChatTap: function () {
    wx.navigateTo({
      url: '/package-extra/pages/chat/index?id=' + this.data.id
    })
  },

  /** 查看更多回复 */
  onMoreReplies: function () {
    if (this.data.replyHasMore) {
      this.loadReplies()
    }
  },

  /** 导航到活动地点 */
  onNavigateTap: function () {
    var detail = this.data.detail
    if (!detail || !detail.latitude || !detail.longitude) {
      wx.showToast({ title: '暂无位置信息', icon: 'none' })
      return
    }
    wx.openLocation({
      latitude: detail.latitude,
      longitude: detail.longitude,
      name: detail.activityAddress || '',
      address: detail.activityAddress || ''
    })
  },

  /** 阻止冒泡 */
  preventTap: function () {}
})
