package com.invitation.web.controller;

import com.invitation.common.annotation.OperationLog;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.guest.GuestListVO;
import com.invitation.model.dto.guest.GuestReplyDTO;
import com.invitation.model.dto.guest.GuestReplyVO;
import com.invitation.service.guest.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 嘉宾回复控制器
 */
@Tag(name = "嘉宾管理")
@RestController
@RequestMapping("/api/v1/guest")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @Operation(summary = "嘉宾通过短码回复")
    @PostMapping("/reply/{shortCode}")
    public R<GuestReplyVO> replyByShortCode(@PathVariable String shortCode,
                                             @RequestBody GuestReplyDTO dto) {
        return guestService.replyByShortCode(shortCode, dto);
    }

    @Operation(summary = "获取邀请函嘉宾列表")
    @GetMapping("/list/{invitationId}")
    public R<PageResult<GuestListVO>> listGuests(@PathVariable Long invitationId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        return guestService.listGuests(invitationId, page, size);
    }

    @Operation(summary = "导出嘉宾列表Excel")
    @GetMapping("/export/{invitationId}")
    @OperationLog(module = "嘉宾", action = "导出", description = "导出嘉宾列表")
    public void exportGuests(@PathVariable Long invitationId, HttpServletResponse response) {
        guestService.exportGuests(invitationId, response);
    }

    @Operation(summary = "获取嘉宾回复详情")
    @GetMapping("/reply/{replyId}")
    public R<GuestReplyVO> getReplyDetail(@PathVariable Long replyId) {
        return guestService.getReplyDetail(replyId);
    }
}
