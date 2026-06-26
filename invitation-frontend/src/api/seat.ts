import { request } from './request'
import type { ApiResult, SeatTable, SeatTableDTO, SeatAssignmentDTO, SeatAssignment } from '@/types'

export function getSeatTables(invitationId: number): Promise<ApiResult<SeatTable[]>> {
  return request.get(`/seats/invitations/${invitationId}/tables`)
}

export function createSeatTable(invitationId: number, data: SeatTableDTO): Promise<ApiResult<SeatTable>> {
  return request.post(`/seats/invitations/${invitationId}/tables`, data)
}

export function updateSeatTable(tableId: number, data: SeatTableDTO): Promise<ApiResult<SeatTable>> {
  return request.put(`/seats/tables/${tableId}`, data)
}

export function deleteSeatTable(tableId: number): Promise<ApiResult<void>> {
  return request.delete(`/seats/tables/${tableId}`)
}

export function assignSeat(tableId: number, data: SeatAssignmentDTO): Promise<ApiResult<void>> {
  return request.post(`/seats/tables/${tableId}/assign`, data)
}

export function batchAssignSeats(tableId: number, data: SeatAssignmentDTO[]): Promise<ApiResult<void>> {
  return request.post(`/seats/tables/${tableId}/assign/batch`, data)
}

export function unassignSeat(assignmentId: number): Promise<ApiResult<void>> {
  return request.delete(`/seats/assignments/${assignmentId}`)
}

export function getSeatMap(invitationId: number): Promise<ApiResult<any>> {
  return request.get(`/seats/invitations/${invitationId}/map`)
}
