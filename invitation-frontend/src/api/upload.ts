import { request } from './request'
import type { ApiResult } from '@/types'

export function uploadImage(formData: FormData): Promise<ApiResult<{ url: string; thumbnailUrl?: string }>> {
  return request.post('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
