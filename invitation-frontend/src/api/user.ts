import { request } from './request'
import type { ApiResult, UserInfo } from '@/types'

export function getUserInfo(): Promise<ApiResult<UserInfo>> {
  return request.get('/user/info')
}

export function updateUserInfo(data: Partial<UserInfo>): Promise<ApiResult<UserInfo>> {
  return request.put('/user/info', data)
}

export function bindPhone(data: { phone: string; code: string }): Promise<ApiResult<void>> {
  return request.post('/user/bind-phone', data)
}
