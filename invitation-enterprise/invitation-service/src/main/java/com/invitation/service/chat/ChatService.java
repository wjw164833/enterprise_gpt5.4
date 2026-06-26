package com.invitation.service.chat;

import com.invitation.common.model.R;
import com.invitation.model.dto.chat.ChatMessageDTO;
import com.invitation.model.entity.ChatMessage;

import java.util.List;

/**
 * 聊天消息服务接口
 */
public interface ChatService {

    /**
     * 发送消息
     */
    R<ChatMessage> sendMessage(ChatMessageDTO dto);

    /**
     * 获取邀请函消息列表
     */
    R<List<ChatMessage>> listMessages(Long invitationId, Long lastMessageId, Integer limit);

    /**
     * 撤回消息
     */
    R<Void> revokeMessage(Long messageId);

    /**
     * 删除消息
     */
    R<Void> deleteMessage(Long messageId);

    /**
     * 获取未读消息数量
     */
    R<Integer> getUnreadCount(Long invitationId, Long userId);

    /**
     * 标记已读
     */
    R<Void> markAsRead(Long invitationId, Long userId);
}
