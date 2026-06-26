package com.invitation.model.dto.subscription;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubscriptionVO {
    private Long id;
    private Long userId;
    private Long planId;
    private String planName;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private Boolean expired;
    private Integer autoRenew;
    private Integer status;
    private Integer invitationQuota;
    private Integer invitationUsed;
    private Integer templateQuota;
    private Integer aiQuota;
    private Integer aiUsed;
}
