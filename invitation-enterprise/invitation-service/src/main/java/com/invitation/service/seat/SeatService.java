package com.invitation.service.seat;

import com.invitation.common.model.R;
import com.invitation.model.dto.seat.SeatAssignmentDTO;
import com.invitation.model.dto.seat.SeatTableDTO;
import com.invitation.model.entity.SeatAssignment;
import com.invitation.model.entity.SeatTable;

import java.util.List;

/**
 * 座位管理服务接口
 */
public interface SeatService {

    /**
     * 创建座位表
     */
    R<SeatTable> createSeatTable(SeatTableDTO dto);

    /**
     * 更新座位表
     */
    R<SeatTable> updateSeatTable(Long tableId, SeatTableDTO dto);

    /**
     * 删除座位表
     */
    R<Void> deleteSeatTable(Long tableId);

    /**
     * 获取邀请函下所有座位表
     */
    R<List<SeatTable>> listSeatTables(Long invitationId);

    /**
     * 分配座位
     */
    R<SeatAssignment> assignSeat(SeatAssignmentDTO dto);

    /**
     * 批量分配座位
     */
    R<Void> batchAssignSeat(List<SeatAssignmentDTO> dtoList);

    /**
     * 取消座位分配
     */
    R<Void> unassignSeat(Long assignmentId);

    /**
     * 获取座位表下所有分配
     */
    R<List<SeatAssignment>> listAssignments(Long tableId);

    /**
     * 自动排座
     */
    R<Void> autoArrange(Long invitationId);

    /**
     * 获取嘉宾座位信息
     */
    R<SeatAssignment> getGuestSeat(Long invitationId, Long guestId);
}
