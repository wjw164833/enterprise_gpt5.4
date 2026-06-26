package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("subscription")
public class Subscription {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long planId;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private Integer autoRenew;
    private Integer status;
    private Integer invitationQuota;
    private Integer invitationUsed;
    private Integer templateQuota;
    private Integer aiQuota;
    private Integer aiUsed;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

    public boolean canCreateInvitation() {
        return invitationUsed < invitationQuota;
    }

    public boolean canUseAi() {
        return aiUsed < aiQuota;
    }
}
