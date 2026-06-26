/**
 * 数据分析
 * 邀请函统计（浏览量/回复/出席），图表展示
 */
var http = require('../../../utils/request')
var util = require('../../../utils/util')

Page({
  data: {
    invitationId: null,
    /** 统计概览 */
    stats: null,
    /** 回复分布 */
    replyStats: null,
    /** 时间维度 Tab */
    timeTabs: [
      { key: '7d', name: '近7天' },
      { key: '30d', name: '近30天' },
      { key: 'all', name: '全部' }
    ],
    currentTimeTab: 0,
    /** PV/UV 趋势数据 */
    pvuvTrend: [],
    loading: true
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ invitationId: options.id })
      this.loadStats()
      this.loadReplyStats()
      this.loadPvUvTrend()
    }
  },

  onPullDownRefresh: function () {
    var that = this
    Promise.all([
      that.loadStats(),
      that.loadReplyStats(),
      that.loadPvUvTrend()
    ]).finally(function () {
      wx.stopPullDownRefresh()
    })
  },

  /** 加载统计数据 */
  loadStats: function () {
    var that = this
    return http.get('/invitation/' + that.data.invitationId + '/stats', {}, { loading: false })
      .then(function (data) {
        that.setData({ stats: data })
      })
      .catch(function () {})
  },

  /** 加载回复统计 */
  loadReplyStats: function () {
    var that = this
    return http.get('/analytics/invitations/' + that.data.invitationId + '/reply-stats', {}, { loading: false })
      .then(function (data) {
        that.setData({ replyStats: data })
      })
      .catch(function () {})
  },

  /** 加载 PV/UV 趋势 */
  loadPvUvTrend: function () {
    var that = this
    var timeKey = that.data.timeTabs[that.data.currentTimeTab].key
    var days = timeKey === '7d' ? 7 : (timeKey === '30d' ? 30 : 365)

    return http.get('/analytics/invitations/' + that.data.invitationId + '/pvuv', {
      days: days
    }, { loading: false }).then(function (data) {
      that.setData({
        pvuvTrend: data.trend || [],
        loading: false
      })
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  /** 时间维度切换 */
  onTimeTabChange: function (e) {
    var index = e.currentTarget.dataset.index
    if (index === this.data.currentTimeTab) return
    this.setData({ currentTimeTab: index })
    this.loadPvUvTrend()
  }
})
