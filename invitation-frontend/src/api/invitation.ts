import { request } from './request'
import type {
  ApiResult, PageResult, InvitationCreateDTO, InvitationUpdateDTO,
  InvitationDetail, InvitationListItem, InvitationQueryDTO, InvitationVersion
} from '@/types'

export function createInvitation(data: InvitationCreateDTO): Promise<ApiResult<InvitationDetail>> {
  return request.post('/invitations', data)
}

export function updateInvitation(id: number, data: InvitationUpdateDTO): Promise<ApiResult<InvitationDetail>> {
  return request.put(`/invitations/${id}`, data)
}

export function deleteInvitation(id: number): Promise<ApiResult<void>> {
  return request.delete(`/invitations/${id}`)
}

export function getInvitationDetail(id: number): Promise<ApiResult<InvitationDetail>> {
  return request.get(`/invitations/${id}`)
}

export function getMyInvitations(params: InvitationQueryDTO): Promise<ApiResult<PageResult<InvitationListItem>>> {
  return request.get('/invitations/mine', { params })
}

export function publishInvitation(id: number): Promise<ApiResult<void>> {
  return request.post(`/invitations/${id}/publish`)
}

export function unpublishInvitation(id: number): Promise<ApiResult<void>> {
  return request.post(`/invitations/${id}/unpublish`)
}

export function getInvitationVersions(id: number): Promise<ApiResult<InvitationVersion[]>> {
  return request.get(`/invitations/${id}/versions`)
}

export function rollbackVersion(id: number, versionId: number): Promise<ApiResult<void>> {
  return request.post(`/invitations/${id}/versions/${versionId}/rollback`)
}

export function getPublicInvitation(shortCode: string): Promise<ApiResult<InvitationDetail>> {
  return request.get(`/guest/i/${shortCode}`)
}
