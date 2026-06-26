import { request } from './request'
import type { ApiResult, PageResult, ChatMessage, ChatMessageDTO, PageParams } from '@/types'

export function getChatHistory(invitationId: number, params: PageParams): Promise<ApiResult<PageResult<ChatMessage>>> {
  return request.get(`/chat/invitations/${invitationId}/messages`, { params })
}

export function sendMessage(invitationId: number, data: ChatMessageDTO): Promise<ApiResult<ChatMessage>> {
  return request.post(`/chat/invitations/${invitationId}/messages`, data)
}
