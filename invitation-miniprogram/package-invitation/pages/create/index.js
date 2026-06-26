/**
 * 创建邀请函 - 选择活动类型
 * 选择活动类型卡片（婚礼/生日/宝宝宴/商务/升学/聚会/自定义）
 */
var app = getApp()

Page({
  data: {
    typeList: [
      { type: 1, name: '婚礼', icon: '💒', desc: '浪漫婚礼邀请', color: '#E8534A' },
      { type: 2, name: '生日', icon: '🎂', desc: '温馨生日聚会', color: '#FF9500' },
      { type: 3, name: '宝宝宴', icon: '👶', desc: '宝宝满月百天', color: '#FF6B9D' },
      { type: 4, name: '商务', icon: '💼', desc: '商务活动邀请', color: '#10AEFF' },
      { type: 5, name: '升学', icon: '🎓', desc: '金榜题名庆祝', color: '#5856D6' },
      { type: 6, name: '聚会', icon: '🎉', desc: '朋友同学聚会', color: '#34C759' },
      { type: 7, name: '自定义', icon: '✨', desc: '自定义活动类型', color: '#8E8E93' }
    ],
    selectedType: null
  },

  onLoad: function (options) {
    if (options.type) {
      this.setData({ selectedType: Number(options.type) })
    }
  },

  /** 选择类型 */
  onTypeSelect: function (e) {
    var type = e.currentTarget.dataset.type
    this.setData({ selectedType: type })
  },

  /** 下一步 */
  onNext: function () {
    var type = this.data.selectedType
    if (!type) {
      wx.showToast({ title: '请选择活动类型', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: '/package-invitation/pages/step-template/index?type=' + type
    })
  }
})
