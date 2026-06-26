import { request } from './request'
import type { ApiResult, PhotoInfo } from '@/types'

export function uploadPhoto(invitationId: number, formData: FormData): Promise<ApiResult<PhotoInfo>> {
  return request.post(`/invitations/${invitationId}/album`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getAlbum(invitationId: number): Promise<ApiResult<PhotoInfo[]>> {
  return request.get(`/invitations/${invitationId}/album`)
}

export function deletePhoto(invitationId: number, photoId: number): Promise<ApiResult<void>> {
  return request.delete(`/invitations/${invitationId}/album/${photoId}`)
}
