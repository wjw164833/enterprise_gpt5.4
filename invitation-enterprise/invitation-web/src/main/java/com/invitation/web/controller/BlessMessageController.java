package com.invitation.web.controller;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.entity.BlessMessage;
import com.invitation.service.bless.BlessMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 祝福消息控制器
 */
@Tag(name = "祝福管理")
@RestController
@RequestMapping("/api/v1/bless/invitations")
@RequiredArgsConstructor
public class BlessMessageController {

    private final BlessMessageService blessMessageService;

    @Operation(summary = "发送祝福")
    @PostMapping("/{invitationId}/bless")
    public R<BlessMessage> createBless(@PathVariable Long invitationId,
                                        @RequestParam String content,
                                        @RequestParam(required = false) String guestName,
                                        @RequestParam(required = false) String guestAvatar) {
        return blessMessageService.createBless(invitationId, content, guestName, guestAvatar);
    }

    @Operation(summary = "获取祝福列表")
    @GetMapping("/{invitationId}/bless")
    public R<PageResult<BlessMessage>> listBless(@PathVariable Long invitationId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer size) {
        return blessMessageService.listBless(invitationId, page, size);
    }

    @Operation(summary = "删除祝福")
    @DeleteMapping("/bless/{blessId}")
    public R<Void> deleteBless(@PathVariable Long blessId) {
        return blessMessageService.deleteBless(blessId);
    }
}
