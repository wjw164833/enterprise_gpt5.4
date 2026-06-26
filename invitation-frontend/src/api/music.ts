import { request } from './request'
import type { ApiResult, MusicInfo } from '@/types'

export function getMusicLibrary(params: { category?: string; page?: number; size?: number }): Promise<ApiResult<MusicInfo[]>> {
  return request.get('/music/library', { params })
}

export function uploadMusic(formData: FormData): Promise<ApiResult<MusicInfo>> {
  return request.post('/music/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
