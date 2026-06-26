/**
 * 步骤-预览
 * 手机模拟器内预览邀请函最终效果，底部「发布」按钮
 */
var http = require('../../../utils/request')
var util = require('../../../utils/util')

Page({
  data: {
    invitationId: null,
    templateId: null,
    activityType: 1,
    allData: {},
    previewData: null,
    submitting: false
  },

  onLoad: function (options) {
    if (options.id) this.setData({ invitationId: options.id })
    if (options.templateId) this.setData({ templateId: options.templateId })
    if (options.activityType) this.setData({ activityType: Number(options.activityType) })
    if (options.allData) {
      try {
        var allData = JSON.parse(decodeURIComponent(options.allData))
        this.setData({ allData: allData })
        this.buildPreviewData(allData)
      } catch (e) {}
    }
  },

  /** 构建预览数据 */
  buildPreviewData: function (allData) {
    var form = allData.form || {}
    var content = allData.content || {}
    var settings = allData.settings || {}

    var typeName = util.getActivityTypeName(this.data.activityType)

    this.setData({
      previewData: {
        title: form.title || typeName + '邀请函',
        coverImage: form.coverImage || '',
        activityDate: form.activityDate || '',
        activityAddress: form.activityAddress || '',
        aiGreeting: form.aiGreeting || '',
        typeName: typeName,
        photos: content.photos || [],
        videoUrl: content.videoUrl || '',
        allowCompanion: settings.allowCompanion,
        publicBless: settings.publicBless,
        enableSeat: settings.enableSeat
      }
    })
  },

  /** 上一步 */
  onPrev: function () {
    wx.navigateBack()
  },

  /** 发布 / 保存草稿 */
  onPublish: function () {
    this.submitInvitation(1)
  },

  onSaveDraft: function () {
    this.submitInvitation(0)
  },

  /** 提交邀请函 */
  submitInvitation: function (status) {
    var that = this
    var allData = that.data.allData
    var form = allData.form || {}
    var content = allData.content || {}
    var settings = allData.settings || {}

    if (status === 1 && (!form.title || !form.activityDate)) {
      wx.showToast({ title: '请完善必填信息后再发布', icon: 'none' })
      return
    }

    that.setData({ submitting: true })

    var submitData = {
      title: form.title,
      activityType: that.data.activityType,
      templateId: that.data.templateId,
      coverImage: form.coverImage,
      content: form.content || '',
      activityDate: form.activityDate,
      activityAddress: form.activityAddress,
      latitude: form.latitude,
      longitude: form.longitude,
      musicId: form.musicId,
      customMusicUrl: form.customMusicUrl,
      aiGreeting: form.aiGreeting,
      watermark: settings.watermark ? 1 : 0,
      extraData: JSON.stringify({
        replyDeadline: settings.replyDeadline,
        allowCompanion: settings.allowCompanion,
        maxGuestCount: settings.maxGuestCount,
        publicBless: settings.publicBless,
        enableSeat: settings.enableSeat
      })
    }

    var request
    if (that.data.invitationId) {
      request = http.put('/invitation/' + that.data.invitationId, submitData)
    } else {
      request = http.post('/invitation', submitData)
    }

    request.then(function (data) {
      that.setData({ submitting: false })
      var newId = data.id || that.data.invitationId

      if (status === 1 && newId) {
        return http.put('/invitation/' + newId + '/status', { status: 1 })
          .then(function () {
            wx.showToast({ title: '发布成功', icon: 'success' })
            setTimeout(function () {
              wx.redirectTo({ url: '/pages/detail/index?id=' + newId })
            }, 1500)
          })
      }

      wx.showToast({ title: '草稿已保存', icon: 'success' })
      setTimeout(function () {
        wx.navigateBack({ delta: 5 })
      }, 1500)
    }).catch(function () {
      that.setData({ submitting: false })
    })
  }
})
