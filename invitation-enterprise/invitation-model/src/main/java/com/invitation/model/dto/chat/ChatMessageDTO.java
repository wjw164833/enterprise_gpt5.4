package com.invitation.model.dto.chat;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChatMessageDTO {
    @NotNull(message = "邀请函ID不能为空")
    private Long invitationId;
    @NotBlank(message = "消息内容不能为空")
    private String content;
    private Integer messageType;
    private Integer msgType = 1;
}
