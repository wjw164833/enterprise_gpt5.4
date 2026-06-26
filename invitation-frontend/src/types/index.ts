/** 统一API返回结构 */
export interface ApiResult<T = any> {
  code: number
  message: string
  data: T
}

/** 分页参数 */
export interface PageParams {
  page: number
  size: number
}

/** 分页返回 */
export interface PageResult<T = any> {
  list: T[]
  total: number
  page: number
  size: number
}

/** 用户信息 */
export interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  userType: number
  enterpriseName: string
  enterpriseLogo: string
  status: number
  lastLoginTime: string
  createdAt: string
}

/** Token信息 */
export interface TokenInfo {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

/** 登录请求 - 短信 */
export interface SmsLoginDTO {
  phone: string
  code: string
}

/** 发送验证码请求 */
export interface SmsSendDTO {
  phone: string
}

/** 微信登录请求 */
export interface WxLoginDTO {
  code: string
  encryptedData?: string
  iv?: string
}

/** 刷新Token请求 */
export interface RefreshTokenDTO {
  refreshToken: string
}

/** 邀请函状态 */
export enum InvitationStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  EXPIRED = 2
}

/** 活动类型 */
export enum ActivityType {
  WEDDING = 1,
  BIRTHDAY = 2,
  BUSINESS = 3,
  SCHOOL = 4,
  OTHER = 5
}

/** 邀请函创建DTO */
export interface InvitationCreateDTO {
  title: string
  activityType: ActivityType
  templateId: number
  coverImage?: string
  content?: string
  activityDate?: string
  activityAddress?: string
  latitude?: number
  longitude?: number
  musicId?: number
  customMusicUrl?: string
  videoUrl?: string
  aiGreeting?: string
  extraData?: string
}

/** 邀请函更新DTO */
export interface InvitationUpdateDTO extends Partial<InvitationCreateDTO> {}

/** 邀请函详情 */
export interface InvitationDetail {
  id: number
  userId: number
  title: string
  activityType: ActivityType
  templateId: number
  coverImage: string
  content: string
  activityDate: string
  activityAddress: string
  latitude: number
  longitude: number
  musicId: number
  customMusicUrl: string
  videoUrl: string
  aiGreeting: string
  shortCode: string
  qrcodeUrl: string
  posterUrl: string
  pv: number
  uv: number
  replyCount: number
  attendCount: number
  blessCount: number
  giftAmount: number
  status: InvitationStatus
  watermark: number
  publishedAt: string
  createdAt: string
  updatedAt: string
  template?: TemplateInfo
}

/** 邀请函列表项 */
export interface InvitationListItem {
  id: number
  title: string
  activityType: ActivityType
  coverImage: string
  status: InvitationStatus
  pv: number
  uv: number
  replyCount: number
  attendCount: number
  createdAt: string
  activityDate: string
}

/** 邀请函查询参数 */
export interface InvitationQueryDTO extends PageParams {
  keyword?: string
  activityType?: ActivityType
  status?: InvitationStatus
}

/** 宾客回复状态 */
export enum ReplyStatus {
  ATTEND = 1,
  UNCERTAIN = 2,
  DECLINE = 3
}

/** 宾客回复DTO */
export interface GuestReplyDTO {
  guestName: string
  guestPhone?: string
  replyStatus: ReplyStatus
  guestCount: number
  remark?: string
}

/** 宾客回复VO */
export interface GuestReplyVO {
  id: number
  invitationId: number
  guestName: string
  guestPhone: string
  replyStatus: ReplyStatus
  guestCount: number
  remark: string
  createdAt: string
}

/** 祝福留言DTO */
export interface BlessCreateDTO {
  guestName: string
  guestAvatar?: string
  content: string
  theme?: string
}

/** 祝福留言 */
export interface BlessMessage {
  id: number
  invitationId: number
  guestName: string
  guestAvatar: string
  content: string
  theme: string
  status: number
  createdAt: string
}

/** 模板信息 */
export interface TemplateInfo {
  id: number
  name: string
  coverImage: string
  previewImages: string[]
  templateType: number
  style: string
  config: string
  isFree: number
  minPlan: number
  usageCount: number
  status: number
}

/** 模板查询参数 */
export interface TemplateQueryDTO extends PageParams {
  templateType?: number
  style?: string
}

/** 席位表 */
export interface SeatTable {
  id: number
  invitationId: number
  name: string
  capacity: number
  layoutConfig: string
  sortOrder: number
  assignments: SeatAssignment[]
}

/** 席位分配 */
export interface SeatAssignment {
  id: number
  seatTableId: number
  invitationId: number
  seatNo: string
  guestName: string
  guestPhone: string
  guestReplyId: number
}

/** 席位表DTO */
export interface SeatTableDTO {
  name: string
  capacity: number
  layoutConfig?: string
  sortOrder?: number
}

/** 席位分配DTO */
export interface SeatAssignmentDTO {
  seatNo: string
  guestName: string
  guestPhone?: string
  guestReplyId?: number
}

/** 聊天消息 */
export interface ChatMessage {
  id: number
  invitationId: number
  userId: number
  guestName: string
  content: string
  msgType: number
  createdAt: string
}

/** 聊天消息DTO */
export interface ChatMessageDTO {
  content: string
  msgType: number
}

/** 支付类型 */
export enum PayType {
  SUBSCRIPTION = 1,
  GIFT = 2,
  TEMPLATE = 3
}

/** 支付状态 */
export enum PaymentStatus {
  PENDING = 0,
  PAID = 1,
  REFUNDED = 2,
  CLOSED = 3
}

/** 支付创建DTO */
export interface PaymentCreateDTO {
  payType: PayType
  amount: number
  invitationId?: number
  plan?: number
  templateId?: number
}

/** 支付信息 */
export interface PaymentInfo {
  id: number
  orderNo: string
  payType: PayType
  amount: number
  status: PaymentStatus
  payMethod: number
  payTime: string
  createdAt: string
}

/** 订阅计划 */
export enum SubscriptionPlan {
  FREE = 1,
  PROFESSIONAL = 2,
  ENTERPRISE = 3
}

/** 订阅信息 */
export interface SubscriptionInfo {
  id: number
  userId: number
  plan: SubscriptionPlan
  startDate: string
  endDate: string
  autoRenew: number
  status: number
  invitationQuota: number
  invitationUsed: number
  templateQuota: number
  aiQuota: number
  aiUsed: number
}

/** 订阅方案 */
export interface PlanInfo {
  plan: SubscriptionPlan
  name: string
  price: number
  invitationQuota: number
  templateQuota: number
  aiQuota: number
  features: string[]
}

/** 仪表盘数据 */
export interface DashboardData {
  totalInvitations: number
  publishedInvitations: number
  totalPv: number
  totalUv: number
  totalReplies: number
  totalGifts: number
  recentInvitations: InvitationListItem[]
  pvTrend: TrendItem[]
}

/** 趋势数据项 */
export interface TrendItem {
  date: string
  pv: number
  uv: number
}

/** 回复统计 */
export interface ReplyStats {
  attendCount: number
  uncertainCount: number
  declineCount: number
  totalCount: number
  attendRate: number
}

/** AI任务 */
export interface AiTask {
  taskId: string
  status: string
  result: string
}

/** AI邀请语DTO */
export interface AiGreetingDTO {
  activityType: ActivityType
  style: string
  keywords?: string
}

/** AI音乐推荐DTO */
export interface AiMusicDTO {
  activityType: ActivityType
  mood: string
}

/** 音乐信息 */
export interface MusicInfo {
  id: number
  name: string
  artist: string
  url: string
  coverUrl: string
  duration: number
  category: string
}

/** 照片信息 */
export interface PhotoInfo {
  id: number
  invitationId: number
  url: string
  thumbnailUrl: string
  sortOrder: number
  effect: string
  createdAt: string
}

/** 短链信息 */
export interface ShortLinkInfo {
  shortCode: string
  shortUrl: string
  originalUrl: string
  clickCount: number
}

/** 管理员用户列表项 */
export interface AdminUserItem {
  id: number
  phone: string
  nickname: string
  avatar: string
  userType: number
  status: number
  invitationCount: number
  createdAt: string
}

/** 管理员邀请函列表项 */
export interface AdminInvitationItem {
  id: number
  userId: number
  userName: string
  title: string
  activityType: ActivityType
  status: InvitationStatus
  pv: number
  createdAt: string
}

/** 审核DTO */
export interface AuditReviewDTO {
  approved: boolean
  reason?: string
}

/** 版本信息 */
export interface InvitationVersion {
  id: number
  invitationId: number
  versionNo: number
  snapshot: string
  changeDesc: string
  createdAt: string
}

/** WebSocket消息 */
export interface WsMessage {
  type: string
  data: any
  timestamp: number
}
