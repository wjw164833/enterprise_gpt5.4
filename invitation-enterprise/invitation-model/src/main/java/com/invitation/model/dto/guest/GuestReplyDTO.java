package com.invitation.model.dto.guest;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GuestReplyDTO {
    @NotBlank(message = "宾客姓名不能为空")
    private String guestName;
    private String guestPhone;
    @NotNull(message = "回复状态不能为空")
    private Integer replyStatus;
    private Integer guestCount = 1;
    private String remark;
}
