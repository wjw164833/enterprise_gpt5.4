/**
 * 浮动音乐播放器组件
 * 播放/暂停，旋转动画
 */
Component({
  properties: {
    /** 音乐 URL */
    src: {
      type: String,
      value: ''
    },
    /** 音乐名称 */
    name: {
      type: String,
      value: ''
    },
    /** 是否自动播放 */
    autoPlay: {
      type: Boolean,
      value: false
    },
    /** 是否显示（控制显隐） */
    show: {
      type: Boolean,
      value: true
    }
  },

  data: {
    isPlaying: false,
    rotatingClass: ''
  },

  lifetimes: {
    attached: function () {
      this._innerAudioContext = null
    },
    detached: function () {
      this.destroyAudio()
    }
  },

  observers: {
    'src': function (src) {
      if (!src) return
      this.destroyAudio()
      this.initAudio()
    }
  },

  methods: {
    /** 初始化音频 */
    initAudio: function () {
      var src = this.properties.src
      if (!src) return

      var audio = wx.createInnerAudioContext()
      audio.src = src
      audio.loop = true
      audio.obeyMuteSwitch = false

      var that = this
      audio.onPlay(function () {
        that.setData({ isPlaying: true, rotatingClass: 'rotating' })
      })
      audio.onPause(function () {
        that.setData({ isPlaying: false, rotatingClass: '' })
      })
      audio.onStop(function () {
        that.setData({ isPlaying: false, rotatingClass: '' })
      })
      audio.onError(function () {
        that.setData({ isPlaying: false, rotatingClass: '' })
        console.error('音乐播放错误')
      })

      this._innerAudioContext = audio

      if (this.properties.autoPlay) {
        this.play()
      }
    },

    /** 销毁音频 */
    destroyAudio: function () {
      if (this._innerAudioContext) {
        this._innerAudioContext.stop()
        this._innerAudioContext.destroy()
        this._innerAudioContext = null
      }
    },

    /** 切换播放/暂停 */
    togglePlay: function () {
      if (this.data.isPlaying) {
        this.pause()
      } else {
        this.play()
      }
      this.triggerEvent('toggle', { isPlaying: !this.data.isPlaying })
    },

    /** 播放 */
    play: function () {
      if (this._innerAudioContext) {
        this._innerAudioContext.play()
      }
    },

    /** 暂停 */
    pause: function () {
      if (this._innerAudioContext) {
        this._innerAudioContext.pause()
      }
    },

    /** 停止 */
    stop: function () {
      if (this._innerAudioContext) {
        this._innerAudioContext.stop()
      }
    }
  }
})
