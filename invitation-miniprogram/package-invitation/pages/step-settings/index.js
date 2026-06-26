/**
 * 步骤-设置
 * 回复截止时间、是否允许同伴、人数上限、是否公开留言、席位管理入口
 */
var http = require('../../../utils/request')

Page({
  data: {
    invitationId: null,
    templateId: null,
    activityType: 1,
    formData: {},
    contentData: {},
    /** 设置项 */
    settings: {
      replyDeadline: '',
      allowCompanion: true,
      maxGuestCount: 10,
      publicBless: true,
      enableSeat: false,
      watermark: true
    },
    /** 日期选择 */
    showDatePicker: false
  },

  onLoad: function (options) {
    if (options.id) this.setData({ invitationId: options.id })
    if (options.templateId) this.setData({ templateId: options.templateId })
    if (options.activityType) this.setData({ activityType: Number(options.activityType) })
    if (options.form) {
      try { this.setData({ formData: JSON.parse(decodeURIComponent(options.form)) }) } catch (e) {}
    }
    if (options.content) {
      try { this.setData({ contentData: JSON.parse(decodeURIComponent(options.content)) }) } catch (e) {}
    }

    if (this.data.invitationId) {
      this.loadSettings()
    }
  },

  /** 加载设置 */
  loadSettings: function () {
    var that = this
    http.get('/invitation/' + that.data.invitationId, {}, { loading: false })
      .then(function (data) {
        var extraData = {}
        try { extraData = JSON.parse(data.extraData || '{}') } catch (e) {}
        that.setData({
          settings: {
            replyDeadline: extraData.replyDeadline || '',
            allowCompanion: extraData.allowCompanion !== false,
            maxGuestCount: extraData.maxGuestCount || 10,
            publicBless: extraData.publicBless !== false,
            enableSeat: extraData.enableSeat || false,
            watermark: data.watermark !== 0
          }
        })
      })
  },

  /** 切换开关 */
  onSwitchChange: function (e) {
    var field = e.currentTarget.dataset.field
    this.setData({ ['settings.' + field]: e.detail.value })
  },

  /** 人数输入 */
  onMaxGuestInput: function (e) {
    this.setData({ 'settings.maxGuestCount': Number(e.detail.value) || 10 })
  },

  /** 截止日期选择 */
  onDeadlinePick: function () {
    this.setData({ showDatePicker: true })
  },

  onDeadlineChange: function (e) {
    this.setData({ 'settings.replyDeadline': e.detail.value, showDatePicker: false })
  },

  /** 上一步 */
  onPrev: function () {
    wx.navigateBack()
  },

  /** 下一步：预览 */
  onNext: function () {
    var allData = {
      form: this.data.formData,
      content: this.data.contentData,
      settings: this.data.settings
    }

    wx.navigateTo({
      url: '/package-invitation/pages/step-preview/index' +
        '?templateId=' + this.data.templateId +
        '&activityType=' + this.data.activityType +
        '&allData=' + encodeURIComponent(JSON.stringify(allData)) +
        (this.data.invitationId ? '&id=' + this.data.invitationId : '')
    })
  }
})
