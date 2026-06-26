package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.dto.seat.SeatAssignmentDTO;
import com.invitation.model.dto.seat.SeatTableDTO;
import com.invitation.model.entity.SeatAssignment;
import com.invitation.model.entity.SeatTable;
import com.invitation.service.seat.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 座位管理控制器
 */
@Tag(name = "座位管理")
@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @Operation(summary = "创建座位表")
    @PostMapping("/table")
    public R<SeatTable> createSeatTable(@Validated @RequestBody SeatTableDTO dto) {
        return seatService.createSeatTable(dto);
    }

    @Operation(summary = "更新座位表")
    @PutMapping("/table/{tableId}")
    public R<SeatTable> updateSeatTable(@PathVariable Long tableId, @RequestBody SeatTableDTO dto) {
        return seatService.updateSeatTable(tableId, dto);
    }

    @Operation(summary = "删除座位表")
    @DeleteMapping("/table/{tableId}")
    public R<Void> deleteSeatTable(@PathVariable Long tableId) {
        return seatService.deleteSeatTable(tableId);
    }

    @Operation(summary = "获取邀请函座位表列表")
    @GetMapping("/tables/{invitationId}")
    public R<List<SeatTable>> listSeatTables(@PathVariable Long invitationId) {
        return seatService.listSeatTables(invitationId);
    }

    @Operation(summary = "分配座位")
    @PostMapping("/assign")
    public R<SeatAssignment> assignSeat(@RequestBody SeatAssignmentDTO dto) {
        return seatService.assignSeat(dto);
    }

    @Operation(summary = "取消座位分配")
    @DeleteMapping("/assign/{assignmentId}")
    public R<Void> unassignSeat(@PathVariable Long assignmentId) {
        return seatService.unassignSeat(assignmentId);
    }

    @Operation(summary = "获取座位表分配列表")
    @GetMapping("/assign/table/{tableId}")
    public R<List<SeatAssignment>> listAssignments(@PathVariable Long tableId) {
        return seatService.listAssignments(tableId);
    }

    @Operation(summary = "自动排座")
    @PostMapping("/auto-arrange/{invitationId}")
    public R<Void> autoArrange(@PathVariable Long invitationId) {
        return seatService.autoArrange(invitationId);
    }

    @Operation(summary = "获取嘉宾座位")
    @GetMapping("/guest/{invitationId}/{guestId}")
    public R<SeatAssignment> getGuestSeat(@PathVariable Long invitationId, @PathVariable Long guestId) {
        return seatService.getGuestSeat(invitationId, guestId);
    }
}
