import { request } from './request'
import type { ApiResult, PageResult, TemplateInfo, TemplateQueryDTO } from '@/types'

export function getTemplateList(params: TemplateQueryDTO): Promise<ApiResult<PageResult<TemplateInfo>>> {
  return request.get('/templates', { params })
}

export function getTemplateDetail(id: number): Promise<ApiResult<TemplateInfo>> {
  return request.get(`/templates/${id}`)
}

export function incrementTemplateUsage(id: number): Promise<ApiResult<void>> {
  return request.post(`/templates/${id}/use`)
}
