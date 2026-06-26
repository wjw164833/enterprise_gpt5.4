/**
 * 席位图组件
 * 餐桌圆形布局 + 宾客头像
 */
Component({
  properties: {
    /** 席位表列表 */
    tables: {
      type: Array,
      value: []
    },
    /** 是否可编辑（分配宾客） */
    editable: {
      type: Boolean,
      value: false
    }
  },

  data: {
    selectedTableId: null,
    selectedSeatNo: null
  },

  methods: {
    /** 点击座位 */
    onSeatTap: function (e) {
      var dataset = e.currentTarget.dataset
      var tableId = dataset.tableId
      var seatNo = dataset.seatNo
      var guestName = dataset.guestName

      if (!this.properties.editable) {
        // 只读模式，查看宾客信息
        if (guestName) {
          wx.showToast({ title: guestName, icon: 'none' })
        }
        return
      }

      this.setData({
        selectedTableId: tableId,
        selectedSeatNo: seatNo
      })

      this.triggerEvent('seattap', {
        tableId: tableId,
        seatNo: seatNo,
        guestName: guestName
      })
    },

    /** 点击餐桌（空座位区域） */
    onTableTap: function (e) {
      var tableId = e.currentTarget.dataset.tableId
      this.setData({ selectedTableId: tableId })
      this.triggerEvent('tabletap', { tableId: tableId })
    }
  }
})
