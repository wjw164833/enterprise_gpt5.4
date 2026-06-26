import { request } from './request'
import type { ApiResult, PageResult, GuestReplyDTO, GuestReplyVO, PageParams } from '@/types'

// P1-12: 修复嘉宾回复API路径，与后端 POST /api/v1/guest/reply/{shortCode} 匹配
export function submitReply(shortCode: string, data: GuestReplyDTO): Promise<ApiResult<GuestReplyVO>> {
  return request.post(`/guest/reply/${shortCode}`, data)
}

export function getReplies(invitationId: number, params: PageParams): Promise<ApiResult<PageResult<GuestReplyVO>>> {
  return request.get(`/guest/invitations/${invitationId}/replies`, { params })
}

export function exportGuests(invitationId: number): Promise<Blob> {
  return request.get(`/guest/invitations/${invitationId}/guests/export`, {
    responseType: 'blob'
  })
}
