import { request } from './request'
import type { ApiResult, DashboardData, TrendItem, ReplyStats, PageResult } from '@/types'

export function getDashboard(invitationId: number): Promise<ApiResult<DashboardData>> {
  return request.get(`/analytics/invitations/${invitationId}/dashboard`)
}

export function getPvUvTrend(invitationId: number, params: { startDate: string; endDate: string }): Promise<ApiResult<TrendItem[]>> {
  return request.get(`/analytics/invitations/${invitationId}/pvuv`, { params })
}

export function getReplyStats(invitationId: number): Promise<ApiResult<ReplyStats>> {
  return request.get(`/analytics/invitations/${invitationId}/reply-stats`)
}

export function reportEvent(data: { eventName: string; eventData?: string; invitationId?: number; platform?: string }): Promise<ApiResult<void>> {
  return request.post('/analytics/events', data)
}

export function getAdminOverview(): Promise<ApiResult<{
  totalUsers: number
  totalInvitations: number
  totalPv: number
  totalRevenue: number
  todayUsers: number
  todayInvitations: number
}>> {
  return request.get('/analytics/dashboard')
}
