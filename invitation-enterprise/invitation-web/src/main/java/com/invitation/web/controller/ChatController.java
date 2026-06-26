package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.dto.chat.ChatMessageDTO;
import com.invitation.model.entity.ChatMessage;
import com.invitation.service.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天控制器
 */
@Tag(name = "聊天消息")
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "发送消息")
    @PostMapping("/message")
    public R<ChatMessage> sendMessage(@RequestBody ChatMessageDTO dto) {
        return chatService.sendMessage(dto);
    }

    @Operation(summary = "获取消息列表")
    @GetMapping("/messages/{invitationId}")
    public R<List<ChatMessage>> listMessages(@PathVariable Long invitationId,
                                              @RequestParam(required = false) Long lastMessageId,
                                              @RequestParam(defaultValue = "50") Integer limit) {
        return chatService.listMessages(invitationId, lastMessageId, limit);
    }

    @Operation(summary = "撤回消息")
    @PostMapping("/message/{messageId}/revoke")
    public R<Void> revokeMessage(@PathVariable Long messageId) {
        return chatService.revokeMessage(messageId);
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/message/{messageId}")
    public R<Void> deleteMessage(@PathVariable Long messageId) {
        return chatService.deleteMessage(messageId);
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread/{invitationId}")
    public R<Integer> getUnreadCount(@PathVariable Long invitationId) {
        return chatService.getUnreadCount(invitationId, null);
    }

    @Operation(summary = "标记已读")
    @PostMapping("/read/{invitationId}")
    public R<Void> markAsRead(@PathVariable Long invitationId) {
        return chatService.markAsRead(invitationId, null);
    }
}
