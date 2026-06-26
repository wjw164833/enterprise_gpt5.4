package com.invitation.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.R;
import com.invitation.common.util.SensitiveWordUtil;
import com.invitation.model.dto.chat.ChatMessageDTO;
import com.invitation.model.entity.ChatMessage;
import com.invitation.model.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final SensitiveWordUtil sensitiveWordUtil;

    @Override
    public R<ChatMessage> sendMessage(ChatMessageDTO dto) {
        Long userId = LoginUser.getUserId();

        String content = dto.getContent();
        if (content != null && sensitiveWordUtil.contains(content)) {
            content = sensitiveWordUtil.filter(content);
        }

        ChatMessage message = new ChatMessage();
        message.setInvitationId(dto.getInvitationId());
        message.setUserId(userId);
        message.setContent(content);
        message.setMessageType(dto.getMessageType() != null ? dto.getMessageType() : 0);
        message.setIsRead(0);
        message.setIsRevoked(0);
        chatMessageMapper.insert(message);

        log.info("聊天消息发送成功: messageId={}, invitationId={}", message.getId(), dto.getInvitationId());
        return R.ok(message);
    }

    @Override
    public R<List<ChatMessage>> listMessages(Long invitationId, Long lastMessageId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }

        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getInvitationId, invitationId)
               .eq(ChatMessage::getIsRevoked, 0);
        if (lastMessageId != null && lastMessageId > 0) {
            wrapper.gt(ChatMessage::getId, lastMessageId);
        }
        wrapper.orderByAsc(ChatMessage::getId)
               .last("LIMIT " + limit);

        List<ChatMessage> messages = chatMessageMapper.selectList(wrapper);
        return R.ok(messages);
    }

    @Override
    public R<Void> revokeMessage(Long messageId) {
        Long userId = LoginUser.getUserId();
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }

        LocalDateTime sendTime = message.getCreatedAt();
        if (sendTime != null && sendTime.plusMinutes(2).isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.MESSAGE_REVOKE_EXPIRED);
        }

        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ChatMessage::getId, messageId)
                     .set(ChatMessage::getIsRevoked, 1)
                     .set(ChatMessage::getContent, "该消息已撤回");
        chatMessageMapper.update(null, updateWrapper);
        return R.ok();
    }

    @Override
    public R<Void> deleteMessage(Long messageId) {
        Long userId = LoginUser.getUserId();
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }
        chatMessageMapper.deleteById(messageId);
        return R.ok();
    }

    @Override
    public R<Integer> getUnreadCount(Long invitationId, Long userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getInvitationId, invitationId)
               .eq(ChatMessage::getIsRead, 0)
               .eq(ChatMessage::getIsRevoked, 0)
               .ne(ChatMessage::getUserId, userId);
        Integer count = Math.toIntExact(chatMessageMapper.selectCount(wrapper));
        return R.ok(count);
    }

    @Override
    public R<Void> markAsRead(Long invitationId, Long userId) {
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ChatMessage::getInvitationId, invitationId)
                     .eq(ChatMessage::getIsRead, 0)
                     .eq(ChatMessage::getIsRevoked, 0)
                     .ne(ChatMessage::getUserId, userId)
                     .set(ChatMessage::getIsRead, 1);
        chatMessageMapper.update(null, updateWrapper);
        return R.ok();
    }
}
