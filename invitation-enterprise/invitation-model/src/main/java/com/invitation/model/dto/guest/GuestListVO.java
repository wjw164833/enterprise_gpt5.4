package com.invitation.model.dto.guest;

import lombok.Data;

@Data
public class GuestListVO {
    private Long id;
    private String guestName;
    private String guestPhone;
    private Integer replyStatus;
    private Integer guestCount;
    private String remark;
    private String seatTable;
    private String seatNo;
}
