package com.invitation.model.dto.invitation;

import lombok.Data;
import java.util.Date;

@Data
public class InvitationListVO {
    private Long id;
    private String title;
    private Integer activityType;
    private String coverImage;
    private Date activityDate;
    private String activityAddress;
    private Integer pv;
    private Integer uv;
    private Integer replyCount;
    private Integer attendCount;
    private Integer status;
    private Date publishedAt;
    private Date createdAt;
    private String templateName;
}
