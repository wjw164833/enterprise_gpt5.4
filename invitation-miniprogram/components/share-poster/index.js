/**
 * 分享海报生成组件
 * Canvas 绘制 + 保存相册
 */
Component({
  properties: {
    /** 是否显示 */
    show: {
      type: Boolean,
      value: false
    },
    /** 邀请函数据 */
    invitation: {
      type: Object,
      value: {}
    },
    /** 小程序码 URL */
    qrcodeUrl: {
      type: String,
      value: ''
    }
  },

  data: {
    posterPath: '',
    isGenerating: false
  },

  lifetimes: {
    detached: function () {
      this.setData({ posterPath: '' })
    }
  },

  observers: {
    'show': function (show) {
      if (show && !this.data.posterPath) {
        this.generatePoster()
      }
    }
  },

  methods: {
    /** 生成海报 */
    generatePoster: function () {
      var that = this
      var invitation = this.properties.invitation

      if (!invitation || !invitation.id) {
        wx.showToast({ title: '邀请函信息不完整', icon: 'none' })
        return
      }

      this.setData({ isGenerating: true })

      // 获取 Canvas 上下文
      var query = this.createSelectorQuery()
      query.select('#posterCanvas')
        .fields({ node: true, size: true })
        .exec(function (res) {
          if (!res[0]) {
            that.setData({ isGenerating: false })
            wx.showToast({ title: '海报生成失败', icon: 'none' })
            return
          }

          var canvas = res[0].node
          var ctx = canvas.getContext('2d')
          var dpr = wx.getSystemInfoSync().pixelRatio

          canvas.width = 600 * dpr
          canvas.height = 900 * dpr
          ctx.scale(dpr, dpr)

          that.drawPoster(ctx, canvas, invitation, dpr)
        })
    },

    /** 绘制海报 */
    drawPoster: function (ctx, canvas, invitation, dpr) {
      var that = this
      var width = 600
      var height = 900

      // 背景
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, width, height)

      // 顶部渐变装饰
      var gradient = ctx.createLinearGradient(0, 0, width, 300)
      gradient.addColorStop(0, '#E8534A')
      gradient.addColorStop(1, '#FF7B73')
      ctx.fillStyle = gradient
      ctx.fillRect(0, 0, width, 300)

      // 标题
      ctx.fillStyle = '#ffffff'
      ctx.font = 'bold 36px sans-serif'
      ctx.textAlign = 'center'
      ctx.fillText(invitation.title || '邀请函', width / 2, 120)

      // 活动类型
      var typeMap = { 1: '婚礼', 2: '生日', 3: '宝宝宴', 4: '商务', 5: '升学', 6: '聚会', 7: '自定义' }
      ctx.font = '24px sans-serif'
      ctx.fillText(typeMap[invitation.activityType] || '', width / 2, 170)

      // 活动时间
      if (invitation.activityDate) {
        ctx.font = '22px sans-serif'
        ctx.fillText(invitation.activityDate, width / 2, 220)
      }

      // 活动地点
      if (invitation.activityAddress) {
        ctx.font = '20px sans-serif'
        ctx.fillStyle = 'rgba(255,255,255,0.9)'
        ctx.fillText(invitation.activityAddress, width / 2, 260)
      }

      // 中间内容区域
      ctx.fillStyle = '#333333'
      ctx.font = '26px sans-serif'
      ctx.textAlign = 'left'

      var content = invitation.aiGreeting || invitation.content || ''
      if (content) {
        // 简化：截取前60字
        if (content.length > 60) content = content.substring(0, 60) + '...'
        that.wrapText(ctx, content, 40, 360, width - 80, 34)
      }

      // 底部分享提示
      ctx.fillStyle = '#999999'
      ctx.font = '20px sans-serif'
      ctx.textAlign = 'center'
      ctx.fillText('长按识别小程序码查看详情', width / 2, height - 80)

      // 小程序码
      if (that.properties.qrcodeUrl) {
        that.loadImage(that.properties.qrcodeUrl).then(function (img) {
          ctx.drawImage(img, width / 2 - 60, height - 220, 120, 120)
          that.saveCanvasToTemp(canvas)
        }).catch(function () {
          that.saveCanvasToTemp(canvas)
        })
      } else {
        that.saveCanvasToTemp(canvas)
      }
    },

    /** Canvas 文字换行 */
    wrapText: function (ctx, text, x, y, maxWidth, lineHeight) {
      var words = text.split('')
      var line = ''
      var currentY = y

      for (var i = 0; i < words.length; i++) {
        var testLine = line + words[i]
        var metrics = ctx.measureText(testLine)
        if (metrics.width > maxWidth && i > 0) {
          ctx.fillText(line, x, currentY)
          line = words[i]
          currentY += lineHeight
        } else {
          line = testLine
        }
      }
      ctx.fillText(line, x, currentY)
    },

    /** 加载网络图片 */
    loadImage: function (url) {
      return new Promise(function (resolve, reject) {
        var img = wx.createImage()
        img.onload = function () { resolve(img) }
        img.onerror = reject
        img.src = url
      })
    },

    /** 保存 Canvas 到临时文件 */
    saveCanvasToTemp: function (canvas) {
      var that = this
      wx.canvasToTempFilePath({
        canvas: canvas,
        success: function (res) {
          that.setData({
            posterPath: res.tempFilePath,
            isGenerating: false
          })
        },
        fail: function () {
          that.setData({ isGenerating: false })
          wx.showToast({ title: '海报生成失败', icon: 'none' })
        }
      })
    },

    /** 保存到相册 */
    onSaveToAlbum: function () {
      var posterPath = this.data.posterPath
      if (!posterPath) {
        wx.showToast({ title: '请先生成海报', icon: 'none' })
        return
      }

      var wxApi = require('../../utils/wx-api')
      wxApi.authorize('scope.writePhotosAlbum').then(function (granted) {
        if (!granted) return
        return wxApi.saveImageToPhotosAlbum(posterPath)
      }).then(function () {
        if (arguments.length > 0) {
          wx.showToast({ title: '已保存到相册', icon: 'success' })
        }
      }).catch(function () {
        wx.showToast({ title: '保存失败', icon: 'none' })
      })
    },

    /** 预览海报 */
    onPreview: function () {
      if (!this.data.posterPath) return
      wx.previewImage({
        current: this.data.posterPath,
        urls: [this.data.posterPath]
      })
    },

    /** 关闭 */
    onClose: function () {
      this.setData({ posterPath: '' })
      this.triggerEvent('close')
    },

    /** 阻止冒泡 */
    preventTap: function () {}
  }
})
