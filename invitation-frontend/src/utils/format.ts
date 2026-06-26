import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

/** 格式化日期 */
export function formatDate(date: string | Date, format: string = 'YYYY-MM-DD HH:mm'): string {
  if (!date) return ''
  return dayjs(date).format(format)
}

/** 格式化为相对时间 */
export function formatRelativeTime(date: string | Date): string {
  if (!date) return ''
  return dayjs(date).fromNow()
}

/** 格式化数字（千分位） */
export function formatNumber(num: number): string {
  if (num === undefined || num === null) return '0'
  return num.toLocaleString('zh-CN')
}

/** 格式化金额 */
export function formatMoney(amount: number, prefix: string = '¥'): string {
  if (amount === undefined || amount === null) return `${prefix}0.00`
  return `${prefix}${amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`
}

/** 格式化手机号（脱敏） */
export function formatPhone(phone: string): string {
  if (!phone || phone.length < 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/** 格式化活动类型 */
export function formatActivityType(type: number): string {
  const map: Record<number, string> = {
    1: '婚礼',
    2: '生日',
    3: '商务',
    4: '升学',
    5: '其他'
  }
  return map[type] || '未知'
}

/** 格式化邀请函状态 */
export function formatInvitationStatus(status: number): string {
  const map: Record<number, string> = {
    0: '草稿',
    1: '已发布',
    2: '已下架'
  }
  return map[status] || '未知'
}

/** 格式化回复状态 */
export function formatReplyStatus(status: number): string {
  const map: Record<number, string> = {
    1: '出席',
    2: '不确定',
    3: '不出席'
  }
  return map[status] || '未知'
}

/** 格式化订阅计划 */
export function formatSubscriptionPlan(plan: number): string {
  const map: Record<number, string> = {
    1: '免费版',
    2: '专业版',
    3: '企业版'
  }
  return map[plan] || '未知'
}

/** 文件大小格式化 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`
}
