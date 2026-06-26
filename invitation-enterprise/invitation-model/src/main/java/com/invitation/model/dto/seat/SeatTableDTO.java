package com.invitation.model.dto.seat;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SeatTableDTO {
    @NotNull(message = "邀请函ID不能为空")
    private Long invitationId;
    @NotBlank(message = "席位表名称不能为空")
    private String tableName;
    private String tableNumber;
    @NotNull(message = "座位数不能为空")
    private Integer seatCount;
    private String layoutConfig;
    private Integer sortOrder;
}
