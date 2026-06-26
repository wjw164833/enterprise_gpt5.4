/**
 * 宾客展示页
 * 全屏沉浸式展示邀请函，背景音乐、倒计时、回复表单、留言墙、地图导航
 * 通过 shareCode 鉴权
 */
var http = require('../../utils/request')
var util = require('../../utils/util')
var wxApi = require('../../utils/wx-api')

Page({
  data: {
    shareCode: '',
    detail: null,
    loading: true,
    /** 倒计时 */
    countdown: { days: 0, hours: 0, minutes: 0, seconds: 0, isExpired: false },
    countdownTimer: null,
    /** 留言列表 */
    blesses: [],
    blessPage: 1,
    blessHasMore: true,
    /** 回复表单 */
    showReplyModal: false,
    replyForm: {
      guestName: '',
      guestPhone: '',
      replyStatus: 1,
      guestCount: 1,
      remark: ''
    },
    /** 留言表单 */
    showBlessModal: false,
    blessForm: {
      guestName: '',
      content: ''
    },
    /** 音乐播放 */
    musicPlaying: false,
    musicSrc: ''
  },

  onLoad: function (options) {
    if (options.shareCode) {
      this.setData({ shareCode: options.shareCode })
      this.loadInvitation()
    } else if (options.scene) {
      // 小程序码扫码进入
      var scene = decodeURIComponent(options.scene)
      this.setData({ shareCode: scene })
      this.loadInvitation()
    }
  },

  onUnload: function () {
    if (this.data.countdownTimer) {
      clearInterval(this.data.countdownTimer)
    }
  },

  onShareAppMessage: function () {
    return util.getShareConfig(this.data.detail || {})
  },

  onShareTimeline: function () {
    var detail = this.data.detail
    return {
      title: detail ? detail.title : '邀请函',
      query: 'shareCode=' + (detail ? detail.shortCode : '')
    }
  },

  /** 加载邀请函详情 */
  loadInvitation: function () {
    var that = this
    that.setData({ loading: true })

    http.get('/public/invitation/' + that.data.shareCode, {}, { authRequired: false })
      .then(function (data) {
        data.activityTypeName = util.getActivityTypeName(data.activityType)
        data.formattedDate = util.formatDate(data.activityDate, 'yyyy年MM月dd日 HH:mm')
        that.setData({
          detail: data,
          loading: false,
          musicSrc: data.customMusicUrl || ''
        })
        that.startCountdown(data.activityDate)
        that.loadBlesses()
      })
      .catch(function () {
        that.setData({ loading: false })
        wx.showToast({ title: '邀请函不存在或已下架', icon: 'none' })
      })
  },

  /** 启动倒计时 */
  startCountdown: function (targetDate) {
    if (!targetDate) return
    var that = this
    var update = function () {
      var cd = util.getCountdown(targetDate)
      that.setData({ countdown: cd })
      if (cd.isExpired && that.data.countdownTimer) {
        clearInterval(that.data.countdownTimer)
      }
    }
    update()
    var timer = setInterval(update, 1000)
    that.setData({ countdownTimer: timer })
  },

  /** 加载留言列表 */
  loadBlesses: function () {
    var that = this
    var detail = that.data.detail
    if (!detail) return

    http.get('/invitation/' + detail.id + '/blesses', {
      page: that.data.blessPage,
      size: 20
    }, { authRequired: false, loading: false }).then(function (data) {
      var records = data.records || data || []
      records.forEach(function (item) {
        item.formattedTime = util.formatRelativeTime(item.createdAt)
      })

      var newList = that.data.blessPage === 1
        ? records
        : that.data.blesses.concat(records)

      that.setData({
        blesses: newList,
        blessHasMore: records.length >= 20,
        blessPage: that.data.blessPage + 1
      })
    }).catch(function () {})
  },

  /** 显示回复弹窗 */
  onReplyTap: function () {
    this.setData({ showReplyModal: true })
  },

  /** 关闭回复弹窗 */
  onReplyClose: function () {
    this.setData({ showReplyModal: false })
  },

  /** 回复表单输入 */
  onReplyInput: function (e) {
    var field = e.currentTarget.dataset.field
    var value = e.detail.value
    this.setData({ ['replyForm.' + field]: value })
  },

  /** 选择回复状态 */
  onReplyStatusChange: function (e) {
    this.setData({ 'replyForm.replyStatus': Number(e.currentTarget.dataset.value) })
  },

  /** 人数加减 */
  onGuestCountChange: function (e) {
    var type = e.currentTarget.dataset.type
    var count = this.data.replyForm.guestCount
    if (type === 'plus') {
      this.setData({ 'replyForm.guestCount': count + 1 })
    } else if (type === 'minus' && count > 1) {
      this.setData({ 'replyForm.guestCount': count - 1 })
    }
  },

  /** 提交回复 */
  onSubmitReply: function () {
    var form = this.data.replyForm
    var detail = this.data.detail

    if (!form.guestName.trim()) {
      wx.showToast({ title: '请填写姓名', icon: 'none' })
      return
    }

    http.post('/invitation/' + detail.id + '/reply', {
      guestName: form.guestName,
      guestPhone: form.guestPhone,
      replyStatus: form.replyStatus,
      guestCount: form.guestCount,
      remark: form.remark
    }, { authRequired: false }).then(function () {
      wx.showToast({ title: '回复成功', icon: 'success' })
      this.setData({ showReplyModal: false })
    }).catch(function () {})
  },

  /** 显示留言弹窗 */
  onBlessTap: function () {
    this.setData({ showBlessModal: true })
  },

  /** 关闭留言弹窗 */
  onBlessClose: function () {
    this.setData({ showBlessModal: false })
  },

  /** 留言表单输入 */
  onBlessInput: function (e) {
    var field = e.currentTarget.dataset.field
    this.setData({ ['blessForm.' + field]: e.detail.value })
  },

  /** 提交留言 */
  onSubmitBless: function () {
    var form = this.data.blessForm
    var detail = this.data.detail

    if (!form.guestName.trim()) {
      wx.showToast({ title: '请填写姓名', icon: 'none' })
      return
    }
    if (!form.content.trim()) {
      wx.showToast({ title: '请填写祝福语', icon: 'none' })
      return
    }

    var that = this
    http.post('/invitation/' + detail.id + '/bless', {
      guestName: form.guestName,
      content: form.content
    }, { authRequired: false }).then(function () {
      wx.showToast({ title: '祝福已送出', icon: 'success' })
      that.setData({
        showBlessModal: false,
        'blessForm.content': '',
        blessPage: 1
      })
      that.loadBlesses()
    }).catch(function () {})
  },

  /** 导航 */
  onNavigateTap: function () {
    var detail = this.data.detail
    if (!detail || !detail.latitude || !detail.longitude) {
      wx.showToast({ title: '暂无位置信息', icon: 'none' })
      return
    }
    wxApi.openLocation({
      latitude: Number(detail.latitude),
      longitude: Number(detail.longitude),
      name: detail.activityAddress || '',
      address: detail.activityAddress || ''
    })
  },

  /** 拨打电话 */
  onCallTap: function () {
    wx.makePhoneCall({ phoneNumber: '400-000-0000' })
  },

  /** 音乐播放切换 */
  onMusicToggle: function () {
    this.setData({ musicPlaying: !this.data.musicPlaying })
  },

  /** 阻止冒泡 */
  preventTap: function () {}
})
