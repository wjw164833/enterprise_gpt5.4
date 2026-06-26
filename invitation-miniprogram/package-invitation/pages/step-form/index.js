/**
 * 步骤-填表单
 * 根据活动类型动态渲染表单字段，封面图上传、音乐选择、邀请语填写、AI生成按钮
 */
var http = require('../../../utils/request')
var wxApi = require('../../../utils/wx-api')
var util = require('../../../utils/util')

Page({
  data: {
    mode: 'create',
    invitationId: null,
    templateId: null,
    activityType: 1,
    /** 表单数据 */
    form: {
      title: '',
      activityDate: '',
      activityAddress: '',
      latitude: null,
      longitude: null,
      coverImage: '',
      musicId: null,
      customMusicUrl: '',
      aiGreeting: '',
      content: ''
    },
    /** 音乐列表 */
    musicList: [],
    showMusicPicker: false,
    selectedMusicName: '',
    /** 日期时间选择器 */
    showDatePicker: false,
    datePickerValue: '',
    /** AI 生成中 */
    aiGenerating: false,
    /** 封面上传中 */
    coverUploading: false
  },

  onLoad: function (options) {
    var mode = options.mode || 'create'
    var templateId = options.templateId || ''
    var activityType = Number(options.type) || 1

    this.setData({
      mode: mode,
      templateId: templateId,
      activityType: activityType
    })

    if (options.id) {
      this.setData({ invitationId: options.id })
      this.loadInvitation(options.id)
    }

    this.loadMusicList()
  },

  /** 加载邀请函数据（编辑模式） */
  loadInvitation: function (id) {
    var that = this
    http.get('/invitation/' + id).then(function (data) {
      that.setData({
        activityType: data.activityType,
        templateId: data.templateId,
        form: {
          title: data.title || '',
          activityDate: data.activityDate || '',
          activityAddress: data.activityAddress || '',
          latitude: data.latitude,
          longitude: data.longitude,
          coverImage: data.coverImage || '',
          musicId: data.musicId,
          customMusicUrl: data.customMusicUrl || '',
          aiGreeting: data.aiGreeting || '',
          content: data.content || ''
        },
        datePickerValue: data.activityDate
          ? util.formatDate(data.activityDate, 'yyyy-MM-dd') : ''
      })
    })
  },

  /** 加载音乐列表 */
  loadMusicList: function () {
    var that = this
    http.get('/music/library', {}, { loading: false })
      .then(function (data) {
        that.setData({ musicList: data.records || data || [] })
      })
      .catch(function () {})
  },

  /** 表单输入 */
  onInput: function (e) {
    var field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  /** 上传封面图 */
  onUploadCover: function () {
    var that = this
    wxApi.chooseImage({ count: 1 }).then(function (paths) {
      that.setData({ coverUploading: true })
      return http.upload(paths[0])
    }).then(function (data) {
      that.setData({
        'form.coverImage': data.url,
        coverUploading: false
      })
    }).catch(function () {
      that.setData({ coverUploading: false })
    })
  },

  /** 选择位置 */
  onChooseLocation: function () {
    var that = this
    wxApi.getLocation().then(function (loc) {
      return wxApi.openLocation({
        latitude: loc.latitude,
        longitude: loc.longitude
      })
    }).catch(function () {
      // 位置选择失败，手动输入
    })
  },

  /** 日期选择 */
  onDatePick: function () {
    this.setData({ showDatePicker: true })
  },

  onDateChange: function (e) {
    this.setData({
      'form.activityDate': e.detail.value,
      datePickerValue: e.detail.value,
      showDatePicker: false
    })
  },

  onDateCancel: function () {
    this.setData({ showDatePicker: false })
  },

  /** 音乐选择 */
  onShowMusicPicker: function () {
    this.setData({ showMusicPicker: true })
  },

  onCloseMusicPicker: function () {
    this.setData({ showMusicPicker: false })
  },

  onSelectMusic: function (e) {
    var id = e.currentTarget.dataset.id
    var name = e.currentTarget.dataset.name
    var url = e.currentTarget.dataset.url
    this.setData({
      'form.musicId': id,
      'form.customMusicUrl': url,
      selectedMusicName: name,
      showMusicPicker: false
    })
  },

  /** AI 生成邀请语 */
  onAiGenerate: function () {
    var that = this
    var type = that.data.activityType
    var typeName = util.getActivityTypeName(type)

    that.setData({ aiGenerating: true })

    http.post('/ai/generate-text', {
      activityType: type,
      title: that.data.form.title || typeName + '邀请函'
    }).then(function (data) {
      var greeting = data.text || data.content || data
      that.setData({
        'form.aiGreeting': typeof greeting === 'string' ? greeting : '',
        aiGenerating: false
      })
    }).catch(function () {
      that.setData({ aiGenerating: false })
      wx.showToast({ title: 'AI生成失败', icon: 'none' })
    })
  },

  /** 上一步 */
  onPrev: function () {
    wx.navigateBack()
  },

  /** 下一步 */
  onNext: function () {
    var form = this.data.form
    if (!form.title.trim()) {
      wx.showToast({ title: '请填写标题', icon: 'none' })
      return
    }
    if (!form.activityDate) {
      wx.showToast({ title: '请选择活动日期', icon: 'none' })
      return
    }

    // 保存到全局临时数据
    var pages = getCurrentPages()
    var prevPage = pages[pages.length - 2]
    var formData = JSON.stringify(form)

    wx.navigateTo({
      url: '/package-invitation/pages/step-content/index?templateId=' + this.data.templateId +
        '&activityType=' + this.data.activityType +
        '&form=' + encodeURIComponent(formData) +
        (this.data.invitationId ? '&id=' + this.data.invitationId : '')
    })
  }
})
