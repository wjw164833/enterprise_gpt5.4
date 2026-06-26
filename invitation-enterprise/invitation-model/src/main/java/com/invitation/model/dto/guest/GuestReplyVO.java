package com.invitation.model.dto.guest;

import lombok.Data;
import java.util.Date;

@Data
public class GuestReplyVO {
    private Long id;
    private Long invitationId;
    private String guestName;
    private String guestPhone;
    private Integer replyStatus;
    private Integer guestCount;
    private String remark;
    private Date createdAt;
}
