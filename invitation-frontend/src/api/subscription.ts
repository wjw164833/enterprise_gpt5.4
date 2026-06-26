import { request } from './request'
import type { ApiResult, SubscriptionInfo, PlanInfo } from '@/types'

export function getSubscriptionPlans(): Promise<ApiResult<PlanInfo[]>> {
  return request.get('/subscription/plans')
}

export function getCurrentSubscription(): Promise<ApiResult<SubscriptionInfo>> {
  return request.get('/subscription/current')
}

export function subscribe(data: { plan: number; period: number }): Promise<ApiResult<{ orderNo: string; payUrl: string }>> {
  return request.post('/subscription/upgrade', data)
}
