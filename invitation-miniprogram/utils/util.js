/**
 * 通用工具函数
 */

/**
 * 格式化日期
 * @param {Date|string|number} date - 日期对象/时间戳/日期字符串
 * @param {string} [fmt='yyyy-MM-dd HH:mm'] - 格式化模板
 * @returns {string}
 */
function formatDate(date, fmt) {
  if (!date) return ''
  fmt = fmt || 'yyyy-MM-dd HH:mm'

  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'))
  } else if (typeof date === 'number') {
    date = new Date(date)
  }

  if (!(date instanceof Date) || isNaN(date.getTime())) return ''

  var o = {
    'M+': date.getMonth() + 1,
    'd+': date.getDate(),
    'H+': date.getHours(),
    'h+': date.getHours() % 12 || 12,
    'm+': date.getMinutes(),
    's+': date.getSeconds(),
    'q+': Math.floor((date.getMonth() + 3) / 3),
    'S': date.getMilliseconds()
  }

  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }

  for (var k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      )
    }
  }

  return fmt
}

/**
 * 格式化相对时间（刚刚/x分钟前/x小时前/昨天/x天前）
 * @param {Date|string|number} date
 * @returns {string}
 */
function formatRelativeTime(date) {
  if (!date) return ''
  if (typeof date === 'string') {
    date = new Date(date.replace(/-/g, '/'))
  } else if (typeof date === 'number') {
    date = new Date(date)
  }

  var now = Date.now()
  var diff = now - date.getTime()
  var seconds = Math.floor(diff / 1000)
  var minutes = Math.floor(seconds / 60)
  var hours = Math.floor(minutes / 60)
  var days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return minutes + '分钟前'
  if (hours < 24) return hours + '小时前'
  if (days < 2) return '昨天'
  if (days < 30) return days + '天前'
  return formatDate(date, 'yyyy-MM-dd')
}

/**
 * 手机号脱敏
 * @param {string} phone
 * @returns {string}
 */
function maskPhone(phone) {
  if (!phone || phone.length < 7) return phone || ''
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

/**
 * 姓名脱敏
 * @param {string} name
 * @returns {string}
 */
function maskName(name) {
  if (!name) return ''
  if (name.length <= 2) return name.charAt(0) + '*'
  return name.charAt(0) + '*'.repeat(name.length - 2) + name.charAt(name.length - 1)
}

/**
 * 活动类型映射
 * @param {number} type
 * @returns {string}
 */
function getActivityTypeName(type) {
  var map = {
    1: '婚礼',
    2: '生日',
    3: '宝宝宴',
    4: '商务',
    5: '升学',
    6: '聚会',
    7: '自定义'
  }
  return map[type] || '其他'
}

/**
 * 邀请函状态映射
 * @param {number} status
 * @returns {string}
 */
function getInvitationStatusName(status) {
  var map = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return map[status] || '未知'
}

/**
 * 邀请函状态对应样式类
 * @param {number} status
 * @returns {string}
 */
function getInvitationStatusClass(status) {
  var map = {
    0: 'status-draft',
    1: 'status-published',
    2: 'status-unpublished'
  }
  return map[status] || ''
}

/**
 * 回复状态映射
 * @param {number} status
 * @returns {string}
 */
function getReplyStatusName(status) {
  var map = {
    1: '出席',
    2: '不确定',
    3: '不出席'
  }
  return map[status] || '未知'
}

/**
 * 回复状态对应样式
 * @param {number} status
 * @returns {string}
 */
function getReplyStatusClass(status) {
  var map = {
    1: 'reply-attend',
    2: 'reply-uncertain',
    3: 'reply-decline'
  }
  return map[status] || ''
}

/**
 * 计算倒计时
 * @param {Date|string} targetDate
 * @returns {{days:number,hours:number,minutes:number,seconds:number,isExpired:boolean}}
 */
function getCountdown(targetDate) {
  if (typeof targetDate === 'string') {
    targetDate = new Date(targetDate.replace(/-/g, '/'))
  }
  var now = Date.now()
  var target = targetDate.getTime()
  var diff = target - now

  if (diff <= 0) {
    return { days: 0, hours: 0, minutes: 0, seconds: 0, isExpired: true }
  }

  return {
    days: Math.floor(diff / (1000 * 60 * 60 * 24)),
    hours: Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)),
    minutes: Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60)),
    seconds: Math.floor((diff % (1000 * 60)) / 1000),
    isExpired: false
  }
}

/**
 * 生成分享配置（给 onShareAppMessage 使用）
 * @param {object} invitation
 * @returns {object}
 */
function getShareConfig(invitation) {
  return {
    title: invitation.title || '邀请函',
    path: '/pages/guest/index?shareCode=' + (invitation.shortCode || ''),
    imageUrl: invitation.coverImage || '',
    success: function () {
      wx.showToast({ title: '分享成功', icon: 'success' })
    }
  }
}

/**
 * 防抖
 * @param {Function} fn
 * @param {number} [delay=500]
 * @returns {Function}
 */
function debounce(fn, delay) {
  delay = delay || 500
  var timer = null
  return function () {
    var context = this
    var args = arguments
    if (timer) clearTimeout(timer)
    timer = setTimeout(function () {
      fn.apply(context, args)
    }, delay)
  }
}

/**
 * 节流
 * @param {Function} fn
 * @param {number} [interval=500]
 * @returns {Function}
 */
function throttle(fn, interval) {
  interval = interval || 500
  var last = 0
  return function () {
    var now = Date.now()
    if (now - last >= interval) {
      last = now
      fn.apply(this, arguments)
    }
  }
}

/**
 * 深拷贝
 * @param {object} obj
 * @returns {object}
 */
function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  return JSON.parse(JSON.stringify(obj))
}

/**
 * 格式化数字（加千分位）
 * @param {number} num
 * @returns {string}
 */
function formatNumber(num) {
  if (num === null || num === undefined) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

module.exports = {
  formatDate,
  formatRelativeTime,
  maskPhone,
  maskName,
  getActivityTypeName,
  getInvitationStatusName,
  getInvitationStatusClass,
  getReplyStatusName,
  getReplyStatusClass,
  getCountdown,
  getShareConfig,
  debounce,
  throttle,
  deepClone,
  formatNumber
}
