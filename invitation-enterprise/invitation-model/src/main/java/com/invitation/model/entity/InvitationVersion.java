package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("invitation_version")
public class InvitationVersion {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private Integer versionNo;
    private String snapshot;
    private String changeDesc;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}
