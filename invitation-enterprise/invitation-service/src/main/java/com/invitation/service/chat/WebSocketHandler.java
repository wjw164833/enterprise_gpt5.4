package com.invitation.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.invitation.common.model.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket聊天处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    /** 邀请函ID -> (用户ID -> WebSocketSession) */
    private static final Map<Long, Map<Long, WebSocketSession>> ROOM_SESSIONS = new ConcurrentHashMap<>();

    /** Session ID -> 用户信息 */
    private static final Map<String, UserInfo> SESSION_USER_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接建立: sessionId={}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (Exception e) {
            log.warn("WebSocket消息解析失败: {}", payload);
            return;
        }

        String type = jsonNode.path("type").asText("");

        switch (type) {
            case "join":
                handleJoin(session, jsonNode);
                break;
            case "chat":
                handleChat(session, jsonNode);
                break;
            case "heartbeat":
                handleHeartbeat(session);
                break;
            default:
                log.warn("未知WebSocket消息类型: {}", type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserInfo userInfo = SESSION_USER_MAP.remove(session.getId());
        if (userInfo != null) {
            Map<Long, WebSocketSession> room = ROOM_SESSIONS.get(userInfo.invitationId);
            if (room != null) {
                room.remove(userInfo.userId);
                if (room.isEmpty()) {
                    ROOM_SESSIONS.remove(userInfo.invitationId);
                }
            }
            broadcastToRoom(userInfo.invitationId, buildSystemMessage(
                    "leave", userInfo.userId, userInfo.nickname + " 离开了聊天室"));
        }
        log.info("WebSocket连接关闭: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 处理加入聊天室
     */
    private void handleJoin(WebSocketSession session, JsonNode jsonNode) {
        Long invitationId = jsonNode.path("invitationId").asLong();
        Long userId = jsonNode.path("userId").asLong();
        String nickname = jsonNode.path("nickname").asText("匿名用户");

        SESSION_USER_MAP.put(session.getId(), new UserInfo(invitationId, userId, nickname));

        ROOM_SESSIONS.computeIfAbsent(invitationId, k -> new ConcurrentHashMap<>())
                     .put(userId, session);

        broadcastToRoom(invitationId, buildSystemMessage(
                "join", userId, nickname + " 加入了聊天室"));

        sendMessageToSession(session, buildAckMessage("join_ack", "加入成功"));
    }

    /**
     * 处理聊天消息
     */
    private void handleChat(WebSocketSession session, JsonNode jsonNode) {
        UserInfo userInfo = SESSION_USER_MAP.get(session.getId());
        if (userInfo == null) {
            sendMessageToSession(session, buildErrorMessage("请先加入聊天室"));
            return;
        }

        String content = jsonNode.path("content").asText("");
        Integer messageType = jsonNode.path("messageType").asInt(0);

        ObjectNode chatMsg = objectMapper.createObjectNode();
        chatMsg.put("type", "chat");
        chatMsg.put("invitationId", userInfo.invitationId);
        chatMsg.put("userId", userInfo.userId);
        chatMsg.put("nickname", userInfo.nickname);
        chatMsg.put("content", content);
        chatMsg.put("messageType", messageType);
        chatMsg.put("timestamp", System.currentTimeMillis());

        broadcastToRoom(userInfo.invitationId, chatMsg.toString());
    }

    /**
     * 处理心跳
     */
    private void handleHeartbeat(WebSocketSession session) {
        sendMessageToSession(session, buildAckMessage("heartbeat_ack", "pong"));
    }

    /**
     * 向聊天室广播消息
     */
    private void broadcastToRoom(Long invitationId, String message) {
        Map<Long, WebSocketSession> room = ROOM_SESSIONS.get(invitationId);
        if (room == null || room.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, WebSocketSession> entry : room.entrySet()) {
            WebSocketSession s = entry.getValue();
            if (s.isOpen()) {
                sendMessageToSession(s, message);
            }
        }
    }

    /**
     * 向指定session发送消息
     */
    private void sendMessageToSession(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            log.error("WebSocket发送消息失败: sessionId={}", session.getId(), e);
        }
    }

    private String buildSystemMessage(String type, Long userId, String content) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", type);
        node.put("userId", userId);
        node.put("content", content);
        node.put("timestamp", System.currentTimeMillis());
        return node.toString();
    }

    private String buildAckMessage(String type, String message) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", type);
        node.put("message", message);
        node.put("timestamp", System.currentTimeMillis());
        return node.toString();
    }

    private String buildErrorMessage(String message) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("type", "error");
        node.put("message", message);
        node.put("timestamp", System.currentTimeMillis());
        return node.toString();
    }

    /**
     * 用户信息内部类
     */
    private static class UserInfo {
        Long invitationId;
        Long userId;
        String nickname;

        UserInfo(Long invitationId, Long userId, String nickname) {
            this.invitationId = invitationId;
            this.userId = userId;
            this.nickname = nickname;
        }
    }
}
