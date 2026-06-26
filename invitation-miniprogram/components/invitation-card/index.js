/**
 * 邀请函卡片组件
 * 展示：封面 + 标题 + 时间 + 状态 + 活动类型
 */
Component({
  properties: {
    /** 邀请函数据 */
    item: {
      type: Object,
      value: {}
    },
    /** 是否显示操作按钮 */
    showAction: {
      type: Boolean,
      value: false
    },
    /** 模式：list列表 / grid网格 */
    mode: {
      type: String,
      value: 'list'
    }
  },

  data: {
    statusName: '',
    statusClass: '',
    typeName: ''
  },

  observers: {
    'item': function (item) {
      if (!item) return
      var statusMap = { 0: '草稿', 1: '已发布', 2: '已下架' }
      var statusClassMap = { 0: 'status-draft', 1: 'status-published', 2: 'status-unpublished' }
      var typeMap = { 1: '婚礼', 2: '生日', 3: '宝宝宴', 4: '商务', 5: '升学', 6: '聚会', 7: '自定义' }

      this.setData({
        statusName: statusMap[item.status] || '未知',
        statusClass: statusClassMap[item.status] || '',
        typeName: typeMap[item.activityType] || '其他'
      })
    }
  },

  methods: {
    /** 点击卡片 */
    onTap: function () {
      this.triggerEvent('tap', { item: this.properties.item })
    },

    /** 点击编辑 */
    onEdit: function (e) {
      e.stopPropagation()
      this.triggerEvent('edit', { item: this.properties.item })
    },

    /** 点击分享 */
    onShare: function (e) {
      e.stopPropagation()
      this.triggerEvent('share', { item: this.properties.item })
    },

    /** 预览封面图 */
    onPreviewCover: function () {
      var cover = this.properties.item.coverImage
      if (!cover) return
      wx.previewImage({
        current: cover,
        urls: [cover]
      })
    }
  }
})
