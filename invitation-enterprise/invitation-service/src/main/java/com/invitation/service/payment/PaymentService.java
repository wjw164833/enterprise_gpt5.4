package com.invitation.service.payment;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.payment.PaymentCreateDTO;
import com.invitation.model.dto.payment.PaymentNotifyDTO;
import com.invitation.model.entity.Payment;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单
     */
    R<Payment> createPayment(PaymentCreateDTO dto);

    /**
     * 处理支付回调通知
     */
    R<Void> handleNotify(PaymentNotifyDTO dto);

    /**
     * 查询支付状态
     */
    R<Payment> queryPaymentStatus(String orderNo);

    /**
     * 申请退款
     */
    R<Void> refund(String orderNo, String reason);

    /**
     * 处理退款回调
     */
    R<Void> handleRefundNotify(PaymentNotifyDTO dto);

    /**
     * 分页查询支付记录
     */
    R<PageResult<Payment>> pagePayment(Long userId, Integer status, Integer page, Integer size);

    /**
     * 关闭超时未支付订单
     */
    void closeExpiredOrders();
}
