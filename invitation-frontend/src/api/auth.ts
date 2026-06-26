import { request } from './request'
import type { ApiResult, SmsSendDTO, SmsLoginDTO, TokenInfo, WxLoginDTO, RefreshTokenDTO } from '@/types'

// P0-08: 短信验证码路径从 /auth/sms/send 改为 /auth/sms/code
export function sendSmsCode(data: SmsSendDTO): Promise<ApiResult<void>> {
  return request.post('/auth/sms/code', data)
}

export function smsLogin(data: SmsLoginDTO): Promise<ApiResult<TokenInfo>> {
  return request.post('/auth/sms/login', data)
}

export function wxMiniLogin(data: WxLoginDTO): Promise<ApiResult<TokenInfo>> {
  return request.post('/auth/wx/mini/login', data)
}

// P0-09: 微信扫码登录路径与后端匹配
export function getWxScanQrCode(): Promise<ApiResult<{ url: string; state: string }>> {
  return request.get('/auth/wx/scan/url')
}

export function checkWxScanStatus(state: string): Promise<ApiResult<TokenInfo | null>> {
  return request.get('/auth/wx/scan/poll', { params: { state } })
}

// P0-06: Token刷新路径从 /auth/token/refresh 改为 /auth/refresh
export function refreshTokenApi(data: RefreshTokenDTO): Promise<ApiResult<TokenInfo>> {
  return request.post('/auth/refresh', data)
}

export function logout(): Promise<ApiResult<void>> {
  return request.post('/auth/logout')
}
