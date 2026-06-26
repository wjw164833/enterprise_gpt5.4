import { request } from './request'
import type { ApiResult, PageResult, AdminUserItem, AdminInvitationItem, PageParams, AuditReviewDTO } from '@/types'

export function getAdminUsers(params: PageParams & { keyword?: string; userType?: number; status?: number }): Promise<ApiResult<PageResult<AdminUserItem>>> {
  return request.get('/admin/users', { params })
}

export function toggleUserStatus(userId: number): Promise<ApiResult<void>> {
  return request.post(`/admin/users/${userId}/toggle-status`)
}

export function getAdminInvitations(params: PageParams & { keyword?: string; status?: number }): Promise<ApiResult<PageResult<AdminInvitationItem>>> {
  return request.get('/admin/invitations', { params })
}

export function auditInvitation(invitationId: number, data: AuditReviewDTO): Promise<ApiResult<void>> {
  return request.put(`/admin/invitation/${invitationId}/audit`, data)
}

export function getAdminOverview(): Promise<ApiResult<{
  totalUsers: number
  totalInvitations: number
  totalPv: number
  totalRevenue: number
}>> {
  return request.get('/admin/overview')
}
