package com.invitation.model.dto.analytics;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class AnalyticsEventDTO {
    @NotNull(message = "事件名称不能为空")
    private String eventName;
    private String eventType;
    private String eventData;
    private Long invitationId;
    private Long userId;
    private String sessionId;
    private String platform;
    private String ip;
    private String userAgent;
    private String extraData;
}
