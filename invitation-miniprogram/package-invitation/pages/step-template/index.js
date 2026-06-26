/**
 * 步骤-选模板
 * 模板网格展示，每个模板显示缩略图+名称，点击预览大图，选中高亮
 */
var http = require('../../../utils/request')

Page({
  data: {
    activityType: 0,
    templateList: [],
    selectedId: null,
    page: 1,
    hasMore: true,
    loading: true,
    /** 预览 */
    showPreview: false,
    previewTemplate: null
  },

  onLoad: function (options) {
    if (options.type) {
      this.setData({ activityType: Number(options.type) })
    }
    if (options.previewId) {
      this.setData({ selectedId: Number(options.previewId) })
    }
    if (options.searchKey) {
      this.setData({ searchKey: options.searchKey })
    }
    this.loadTemplates()
  },

  onPullDownRefresh: function () {
    this.setData({ page: 1, hasMore: true, templateList: [] })
    this.loadTemplates().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom: function () {
    if (this.data.hasMore && !this.data.loading) {
      this.loadTemplates()
    }
  },

  /** 加载模板列表 */
  loadTemplates: function () {
    var that = this
    that.setData({ loading: true })

    var params = {
      page: that.data.page,
      size: 20
    }
    if (that.data.activityType) {
      params.templateType = that.data.activityType
    }

    return http.get('/template/list', params, { loading: false })
      .then(function (data) {
        var records = data.records || data || []
        var newList = that.data.page === 1
          ? records
          : that.data.templateList.concat(records)

        that.setData({
          templateList: newList,
          hasMore: records.length >= 20,
          page: that.data.page + 1,
          loading: false
        })
      })
      .catch(function () {
        that.setData({ loading: false })
      })
  },

  /** 选择模板 */
  onSelectTemplate: function (e) {
    var id = e.currentTarget.dataset.id
    this.setData({ selectedId: id })
  },

  /** 预览模板 */
  onPreviewTemplate: function (e) {
    var id = e.currentTarget.dataset.id
    var template = this.data.templateList.find(function (t) { return t.id === id })
    if (template) {
      this.setData({ showPreview: true, previewTemplate: template })
    }
  },

  /** 关闭预览 */
  onClosePreview: function () {
    this.setData({ showPreview: false, previewTemplate: null })
  },

  /** 上一步 */
  onPrev: function () {
    wx.navigateBack()
  },

  /** 下一步 */
  onNext: function () {
    var selectedId = this.data.selectedId
    if (!selectedId) {
      wx.showToast({ title: '请选择模板', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: '/package-invitation/pages/step-form/index?templateId=' + selectedId + '&type=' + this.data.activityType
    })
  }
})
