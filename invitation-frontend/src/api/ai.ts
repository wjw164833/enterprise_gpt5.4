import { request } from './request'
import type { ApiResult, AiTask, AiGreetingDTO, AiMusicDTO, MusicInfo } from '@/types'

export function generateGreeting(data: AiGreetingDTO): Promise<ApiResult<AiTask>> {
  return request.post('/ai/greeting', data)
}

export function recommendMusic(data: AiMusicDTO): Promise<ApiResult<MusicInfo[]>> {
  return request.post('/ai/music', data)
}

export function recommendPhotoTheme(data: { imageUrl: string }): Promise<ApiResult<AiTask>> {
  return request.post('/ai/photo-theme', data)
}

export function getAiTaskStatus(taskId: string): Promise<ApiResult<AiTask>> {
  return request.get(`/ai/tasks/${taskId}`)
}
