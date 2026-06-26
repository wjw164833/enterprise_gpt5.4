package com.invitation.model.dto.payment;

import lombok.Data;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

@Data
public class PaymentCreateDTO {
    @NotNull(message = "支付类型不能为空")
    private Integer payType;
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    private Long invitationId;
    private Integer plan;
    private Integer planId;
    private String payChannel;

    public Integer getPlanId() {
        return planId != null ? planId : plan;
    }

    public String getPayChannel() {
        return payChannel != null ? payChannel : "wechat";
    }
}
