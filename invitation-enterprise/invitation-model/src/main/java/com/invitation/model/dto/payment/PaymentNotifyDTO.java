package com.invitation.model.dto.payment;

import lombok.Data;

@Data
public class PaymentNotifyDTO {
    private String orderNo;
    private String transactionId;
    private Integer status;
    private String callbackData;
}
