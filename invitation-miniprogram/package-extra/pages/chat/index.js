/**
 * 聊天室
 * 活动专属聊天室，消息列表，发送文字/表情
 */
var http = require('../../../utils/request')
var util = require('../../../utils/util')

Page({
  data: {
    invitationId: null,
    /** 消息列表 */
    messages: [],
    page: 1,
    hasMore: true,
    /** 输入 */
    inputText: '',
    /** 常用表情 */
    emojis: ['😊', '🎉', '❤️', '👍', '🎊', '🥳', '🌹', '💐', '✨', '🎊', '🥂', '🎵'],
    showEmojiPanel: false,
    /** 发送中 */
    sending: false
  },

  onLoad: function (options) {
    if (options.id) {
      this.setData({ invitationId: options.id })
      this.loadMessages()
    }
  },

  onPullDownRefresh: function () {
    this.setData({ page: 1, hasMore: true, messages: [] })
    this.loadMessages().then(function () {
      wx.stopPullDownRefresh()
    })
  },

  /** 加载消息列表 */
  loadMessages: function () {
    var that = this
    return http.get('/chat/invitations/' + that.data.invitationId + '/messages', {
      page: that.data.page,
      size: 30
    }, { loading: false }).then(function (data) {
      var records = data.records || data || []
      records.reverse()
      records.forEach(function (msg) {
        msg.formattedTime = util.formatRelativeTime(msg.createdAt)
        msg.isSystem = msg.msgType === 3
      })

      var newList = that.data.page === 1
        ? records
        : records.concat(that.data.messages)

      that.setData({
        messages: newList,
        hasMore: records.length >= 30,
        page: that.data.page + 1
      })

      that.scrollToBottom()
    }).catch(function () {})
  },

  /** 滚动到底部 */
  scrollToBottom: function () {
    var that = this
    setTimeout(function () {
      that.setData({ scrollIntoView: 'msg-bottom' })
    }, 100)
  },

  /** 输入文字 */
  onInputChange: function (e) {
    this.setData({ inputText: e.detail.value })
  },

  /** 显示表情面板 */
  onToggleEmoji: function () {
    this.setData({ showEmojiPanel: !this.data.showEmojiPanel })
  },

  /** 选择表情 */
  onEmojiTap: function (e) {
    var emoji = e.currentTarget.dataset.emoji
    this.setData({ inputText: this.data.inputText + emoji })
  },

  /** 发送消息 */
  onSend: function () {
    var that = this
    var text = that.data.inputText.trim()
    if (!text) return
    if (that.data.sending) return

    that.setData({ sending: true, showEmojiPanel: false })

    http.post('/chat/invitations/' + that.data.invitationId + '/messages', {
      content: text,
      msgType: 1
    }).then(function (data) {
      var newMsg = data || {
        id: Date.now(),
        content: text,
        msgType: 1,
        formattedTime: '刚刚',
        isSystem: false
      }
      newMsg.formattedTime = util.formatRelativeTime(newMsg.createdAt)

      that.setData({
        messages: that.data.messages.concat([newMsg]),
        inputText: '',
        sending: false
      })
      that.scrollToBottom()
    }).catch(function () {
      that.setData({ sending: false })
    })
  },

  /** 隐藏表情面板 */
  onHideEmoji: function () {
    if (this.data.showEmojiPanel) {
      this.setData({ showEmojiPanel: false })
    }
  }
})
