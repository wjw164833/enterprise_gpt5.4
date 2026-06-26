/**
 * 步骤-内容编辑
 * 照片墙上传（多图）、祝福语段落编辑、视频链接
 */
var http = require('../../../utils/request')
var wxApi = require('../../../utils/wx-api')

Page({
  data: {
    invitationId: null,
    templateId: null,
    activityType: 1,
    formData: {},
    /** 照片列表 */
    photos: [],
    photoUploading: false,
    /** 祝福段落 */
    blessParagraphs: [],
    /** 视频 */
    videoUrl: ''
  },

  onLoad: function (options) {
    if (options.id) this.setData({ invitationId: options.id })
    if (options.templateId) this.setData({ templateId: options.templateId })
    if (options.activityType) this.setData({ activityType: Number(options.activityType) })
    if (options.form) {
      try {
        this.setData({ formData: JSON.parse(decodeURIComponent(options.form)) })
      } catch (e) {}
    }

    if (this.data.invitationId) {
      this.loadAlbum()
    }
  },

  /** 加载相册 */
  loadAlbum: function () {
    var that = this
    http.get('/invitation/' + that.data.invitationId + '/album', {}, { loading: false })
      .then(function (data) {
        that.setData({ photos: data || [] })
      })
      .catch(function () {})
  },

  /** 上传照片 */
  onUploadPhotos: function () {
    var that = this
    var remaining = 9 - that.data.photos.length
    if (remaining <= 0) {
      wx.showToast({ title: '最多上传9张', icon: 'none' })
      return
    }

    wxApi.chooseImage({ count: remaining }).then(function (paths) {
      that.setData({ photoUploading: true })
      var uploadPromises = paths.map(function (path) {
        return http.upload(path)
      })
      return Promise.all(uploadPromises)
    }).then(function (results) {
      var newPhotos = results.map(function (data, index) {
        return {
          id: Date.now() + index,
          url: data.url,
          sortOrder: that.data.photos.length + index
        }
      })
      that.setData({
        photos: that.data.photos.concat(newPhotos),
        photoUploading: false
      })
    }).catch(function () {
      that.setData({ photoUploading: false })
    })
  },

  /** 删除照片 */
  onDeletePhoto: function (e) {
    var index = e.currentTarget.dataset.index
    var photos = this.data.photos
    photos.splice(index, 1)
    this.setData({ photos: photos })
  },

  /** 预览照片 */
  onPreviewPhoto: function (e) {
    var current = e.currentTarget.dataset.url
    var urls = this.data.photos.map(function (p) { return p.url })
    wxApi.previewImage({ current: current, urls: urls })
  },

  /** 视频链接输入 */
  onVideoInput: function (e) {
    this.setData({ videoUrl: e.detail.value })
  },

  /** 上一步 */
  onPrev: function () {
    wx.navigateBack()
  },

  /** 下一步 */
  onNext: function () {
    var data = {
      photos: this.data.photos,
      videoUrl: this.data.videoUrl,
      blessParagraphs: this.data.blessParagraphs
    }

    wx.navigateTo({
      url: '/package-invitation/pages/step-settings/index' +
        '?templateId=' + this.data.templateId +
        '&activityType=' + this.data.activityType +
        '&content=' + encodeURIComponent(JSON.stringify(data)) +
        '&form=' + encodeURIComponent(JSON.stringify(this.data.formData)) +
        (this.data.invitationId ? '&id=' + this.data.invitationId : '')
    })
  }
})
