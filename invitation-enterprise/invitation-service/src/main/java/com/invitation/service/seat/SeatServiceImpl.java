package com.invitation.service.seat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.model.dto.seat.SeatAssignmentDTO;
import com.invitation.model.dto.seat.SeatTableDTO;
import com.invitation.model.entity.GuestReply;
import com.invitation.model.entity.Invitation;
import com.invitation.model.entity.SeatAssignment;
import com.invitation.model.entity.SeatTable;
import com.invitation.model.mapper.InvitationMapper;
import com.invitation.model.mapper.SeatAssignmentMapper;
import com.invitation.model.mapper.SeatTableMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 座位管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatTableMapper seatTableMapper;
    private final SeatAssignmentMapper seatAssignmentMapper;
    private final InvitationMapper invitationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<SeatTable> createSeatTable(SeatTableDTO dto) {
        Long userId = LoginUser.getUserId();
        Invitation invitation = invitationMapper.selectById(dto.getInvitationId());
        if (invitation == null || !invitation.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.INVITATION_NOT_FOUND);
        }

        SeatTable seatTable = new SeatTable();
        seatTable.setInvitationId(dto.getInvitationId());
        seatTable.setTableName(dto.getTableName());
        seatTable.setTableNumber(dto.getTableNumber());
        seatTable.setSeatCount(dto.getSeatCount());
        seatTable.setUsedSeats(0);
        seatTable.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        seatTableMapper.insert(seatTable);

        log.info("座位表创建成功: tableId={}, invitationId={}", seatTable.getId(), dto.getInvitationId());
        return R.ok(seatTable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<SeatTable> updateSeatTable(Long tableId, SeatTableDTO dto) {
        SeatTable seatTable = seatTableMapper.selectById(tableId);
        if (seatTable == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (dto.getTableName() != null) {
            seatTable.setTableName(dto.getTableName());
        }
        if (dto.getTableNumber() != null) {
            seatTable.setTableNumber(dto.getTableNumber());
        }
        if (dto.getSeatCount() != null) {
            if (dto.getSeatCount() < seatTable.getUsedSeats()) {
                throw new BusinessException(ResultCode.SEAT_COUNT_INVALID);
            }
            seatTable.setSeatCount(dto.getSeatCount());
        }
        if (dto.getSortOrder() != null) {
            seatTable.setSortOrder(dto.getSortOrder());
        }
        seatTableMapper.updateById(seatTable);

        return R.ok(seatTable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteSeatTable(Long tableId) {
        SeatTable seatTable = seatTableMapper.selectById(tableId);
        if (seatTable == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (seatTable.getUsedSeats() > 0) {
            throw new BusinessException(ResultCode.SEAT_TABLE_HAS_GUESTS);
        }

        seatTableMapper.deleteById(tableId);
        return R.ok();
    }

    @Override
    public R<List<SeatTable>> listSeatTables(Long invitationId) {
        LambdaQueryWrapper<SeatTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeatTable::getInvitationId, invitationId)
               .orderByAsc(SeatTable::getSortOrder)
               .orderByAsc(SeatTable::getTableNumber);
        List<SeatTable> tables = seatTableMapper.selectList(wrapper);
        return R.ok(tables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<SeatAssignment> assignSeat(SeatAssignmentDTO dto) {
        SeatTable seatTable = seatTableMapper.selectById(dto.getTableId());
        if (seatTable == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (seatTable.getUsedSeats() >= seatTable.getSeatCount()) {
            throw new BusinessException(ResultCode.SEAT_TABLE_FULL);
        }

        LambdaQueryWrapper<SeatAssignment> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(SeatAssignment::getGuestId, dto.getGuestId())
                    .eq(SeatAssignment::getInvitationId, dto.getInvitationId());
        if (seatAssignmentMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException(ResultCode.GUEST_ALREADY_SEATED);
        }

        SeatAssignment assignment = new SeatAssignment();
        assignment.setInvitationId(dto.getInvitationId());
        assignment.setTableId(dto.getTableId());
        assignment.setGuestId(dto.getGuestId());
        assignment.setGuestName(dto.getGuestName());
        assignment.setSeatNumber(dto.getSeatNumber());
        seatAssignmentMapper.insert(assignment);

        seatTable.setUsedSeats(seatTable.getUsedSeats() + 1);
        seatTableMapper.updateById(seatTable);

        return R.ok(assignment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> batchAssignSeat(List<SeatAssignmentDTO> dtoList) {
        for (SeatAssignmentDTO dto : dtoList) {
            assignSeat(dto);
        }
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> unassignSeat(Long assignmentId) {
        SeatAssignment assignment = seatAssignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        seatAssignmentMapper.deleteById(assignmentId);

        SeatTable seatTable = seatTableMapper.selectById(assignment.getTableId());
        if (seatTable != null && seatTable.getUsedSeats() > 0) {
            seatTable.setUsedSeats(seatTable.getUsedSeats() - 1);
            seatTableMapper.updateById(seatTable);
        }
        return R.ok();
    }

    @Override
    public R<List<SeatAssignment>> listAssignments(Long tableId) {
        LambdaQueryWrapper<SeatAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeatAssignment::getTableId, tableId)
               .orderByAsc(SeatAssignment::getSeatNumber);
        List<SeatAssignment> assignments = seatAssignmentMapper.selectList(wrapper);
        return R.ok(assignments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> autoArrange(Long invitationId) {
        LambdaQueryWrapper<SeatAssignment> clearWrapper = new LambdaQueryWrapper<>();
        clearWrapper.eq(SeatAssignment::getInvitationId, invitationId);
        seatAssignmentMapper.delete(clearWrapper);

        LambdaQueryWrapper<SeatTable> tableWrapper = new LambdaQueryWrapper<>();
        tableWrapper.eq(SeatTable::getInvitationId, invitationId)
                    .orderByAsc(SeatTable::getSortOrder)
                    .orderByAsc(SeatTable::getTableNumber);
        List<SeatTable> tables = seatTableMapper.selectList(tableWrapper);

        LambdaQueryWrapper<GuestReply> guestWrapper = new LambdaQueryWrapper<>();
        guestWrapper.eq(GuestReply::getInvitationId, invitationId)
                    .eq(GuestReply::getReplyStatus, 1);
        List<GuestReply> guests = new ArrayList<>();

        if (tables.isEmpty()) {
            throw new BusinessException(ResultCode.SEAT_TABLE_NOT_FOUND);
        }

        int tableIndex = 0;
        int seatNum = 1;
        for (GuestReply guest : guests) {
            if (tableIndex >= tables.size()) {
                tableIndex = 0;
                seatNum = 1;
            }
            SeatTable table = tables.get(tableIndex);

            SeatAssignment assignment = new SeatAssignment();
            assignment.setInvitationId(invitationId);
            assignment.setTableId(table.getId());
            assignment.setGuestId(guest.getId());
            assignment.setGuestName(guest.getGuestName());
            assignment.setSeatNumber(String.valueOf(seatNum));
            seatAssignmentMapper.insert(assignment);

            seatNum++;
            if (seatNum > table.getSeatCount()) {
                table.setUsedSeats(table.getSeatCount());
                seatTableMapper.updateById(table);
                tableIndex++;
                seatNum = 1;
            } else {
                table.setUsedSeats(seatNum - 1);
                seatTableMapper.updateById(table);
            }
        }

        log.info("自动排座完成: invitationId={}", invitationId);
        return R.ok();
    }

    @Override
    public R<SeatAssignment> getGuestSeat(Long invitationId, Long guestId) {
        LambdaQueryWrapper<SeatAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeatAssignment::getInvitationId, invitationId)
               .eq(SeatAssignment::getGuestId, guestId);
        SeatAssignment assignment = seatAssignmentMapper.selectOne(wrapper);
        return R.ok(assignment);
    }
}
