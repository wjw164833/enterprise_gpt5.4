/**
 * 祝福留言卡片组件
 * 展示：头像 + 昵称 + 内容 + 时间
 */
Component({
  properties: {
    /** 留言数据 */
    item: {
      type: Object,
      value: {}
    },
    /** 是否显示删除按钮（主人视角） */
    showDelete: {
      type: Boolean,
      value: false
    }
  },

  data: {
    relativeTime: ''
  },

  observers: {
    'item.createdAt': function (createdAt) {
      if (!createdAt) return
      var util = require('../../utils/util')
      this.setData({
        relativeTime: util.formatRelativeTime(createdAt)
      })
    }
  },

  methods: {
    /** 删除留言 */
    onDelete: function () {
      var that = this
      wx.showModal({
        title: '提示',
        content: '确定删除该留言吗？',
        success: function (res) {
          if (res.confirm) {
            that.triggerEvent('delete', { id: that.properties.item.id })
          }
        }
      })
    }
  }
})
