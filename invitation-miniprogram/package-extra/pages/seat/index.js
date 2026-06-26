/**
 * 席位管理
 * 餐桌列表，点击选择分配宾客到餐桌，座位图展示
 */
var http = require('../../../utils/request')

Page({
  data: {
    invitationId: null,
    tables: [],
    /** 未分配宾客 */
    unassignedGuests: [],
    /** 选中的餐桌 */
    selectedTableId: null,
    /** 分配弹窗 */
    showAssignModal: false,
    assignGuestName: '',
    assignSeatNo: '',
    /** 新增餐桌弹窗 */
    showAddTableModal: false,
    newTableName: '',
    newTableCapacity: 10
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ invitationId: options.id })
      this.loadSeats()
      this.loadGuests()
    }
  },

  /** 加载席位数据 */
  loadSeats: function () {
    var that = this
    http.get('/invitation/' + that.data.invitationId + '/seats')
      .then(function (data) {
        that.setData({ tables: data || [] })
      })
  },

  /** 加载宾客列表 */
  loadGuests: function () {
    var that = this
    http.get('/invitation/' + that.data.invitationId + '/replies', { page: 1, size: 100 }, { loading: false })
      .then(function (data) {
        var guests = (data.records || data || []).filter(function (g) {
          return g.replyStatus === 1
        })
        that.setData({ unassignedGuests: guests })
      })
  },

  /** 点击座位 */
  onSeatTap: function (e) {
    var detail = e.detail
    this.setData({
      selectedTableId: detail.tableId,
      showAssignModal: true,
      assignSeatNo: detail.seatNo,
      assignGuestName: detail.guestName || ''
    })
  },

  /** 分配宾客输入 */
  onAssignInput: function (e) {
    this.setData({ assignGuestName: e.detail.value })
  },

  /** 提交分配 */
  onSubmitAssign: function () {
    var that = this
    var tableId = that.data.selectedTableId
    var seatNo = that.data.assignSeatNo
    var guestName = that.data.assignGuestName

    if (!guestName.trim()) {
      wx.showToast({ title: '请输入宾客姓名', icon: 'none' })
      return
    }

    http.post('/seats/tables/' + tableId + '/assign', {
      seatNo: seatNo,
      guestName: guestName
    }).then(function () {
      wx.showToast({ title: '分配成功', icon: 'success' })
      that.setData({ showAssignModal: false })
      that.loadSeats()
    })
  },

  /** 取消分配 */
  onCancelAssign: function () {
    this.setData({ showAssignModal: false })
  },

  /** 显示新增餐桌弹窗 */
  onShowAddTable: function () {
    this.setData({ showAddTableModal: true })
  },

  /** 新增餐桌输入 */
  onTableNameInput: function (e) {
    this.setData({ newTableName: e.detail.value })
  },

  onTableCapacityInput: function (e) {
    this.setData({ newTableCapacity: Number(e.detail.value) || 10 })
  },

  /** 提交新增餐桌 */
  onSubmitAddTable: function () {
    var that = this
    if (!that.data.newTableName.trim()) {
      wx.showToast({ title: '请输入餐桌名称', icon: 'none' })
      return
    }

    http.post('/seats/invitations/' + that.data.invitationId + '/tables', {
      name: that.data.newTableName,
      capacity: that.data.newTableCapacity
    }).then(function () {
      wx.showToast({ title: '添加成功', icon: 'success' })
      that.setData({
        showAddTableModal: false,
        newTableName: '',
        newTableCapacity: 10
      })
      that.loadSeats()
    })
  },

  /** 阻止冒泡 */
  preventTap: function () {}
})
