package com.invitation.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("seat_table")
public class SeatTable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long invitationId;
    private String tableName;
    private String tableNumber;
    private Integer seatCount;
    private Integer usedSeats;
    private String layoutConfig;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
