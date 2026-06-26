package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat_assignment")
public class SeatAssignment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seatTableId;
    private Long invitationId;
    private String seatNo;
    private String guestName;
    private String guestPhone;
    private Long guestReplyId;
    private Long tableId;
    private Long guestId;
    private String seatNumber;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
