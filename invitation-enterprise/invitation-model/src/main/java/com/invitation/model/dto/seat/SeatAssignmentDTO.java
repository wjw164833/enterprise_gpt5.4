package com.invitation.model.dto.seat;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SeatAssignmentDTO {
    @NotNull(message = "席位表ID不能为空")
    private Long tableId;
    @NotNull(message = "邀请函ID不能为空")
    private Long invitationId;
    @NotBlank(message = "座位号不能为空")
    private String seatNumber;
    private Long guestId;
    private String guestName;
    private String guestPhone;
    private Long guestReplyId;
    private String seatNo;
}
