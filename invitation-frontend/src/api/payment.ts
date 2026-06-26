import { request } from './request'
import type { ApiResult, PaymentCreateDTO, PaymentInfo } from '@/types'

export function createPayment(data: PaymentCreateDTO): Promise<ApiResult<{ orderNo: string; payUrl: string }>> {
  return request.post('/payment/subscribe', data)
}

export function createGiftPayment(data: { invitationId: number; guestName: string; amount: number; message?: string }): Promise<ApiResult<{ orderNo: string; payUrl: string }>> {
  return request.post('/payment/gift', data)
}

export function getPaymentStatus(orderNo: string): Promise<ApiResult<PaymentInfo>> {
  return request.get(`/payment/orders/${orderNo}`)
}
