package com.invitation.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.enums.PaymentStatus;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.enums.SubscriptionPlan;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.LoginUser;
import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.common.util.SnowflakeIdUtil;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.dto.payment.PaymentCreateDTO;
import com.invitation.model.dto.payment.PaymentNotifyDTO;
import com.invitation.model.entity.Payment;
import com.invitation.model.entity.Subscription;
import com.invitation.model.mapper.PaymentMapper;
import com.invitation.model.mapper.SubscriptionMapper;
import com.invitation.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 支付服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionService subscriptionService;
    private final RedisService redisService;
    private final SnowflakeIdUtil snowflakeIdUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Payment> createPayment(PaymentCreateDTO dto) {
        Long userId = LoginUser.getUserId();

        String lockKey = RedisKeyConstant.PAYMENT_LOCK + userId;
        Boolean locked = redisService.setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (locked == null || !locked) {
            throw new BusinessException(ResultCode.OPERATION_TOO_FREQUENT);
        }

        try {
            LambdaQueryWrapper<Payment> existWrapper = new LambdaQueryWrapper<>();
            existWrapper.eq(Payment::getUserId, userId)
                        .eq(Payment::getStatus, PaymentStatus.PENDING.getCode())
                        .eq(Payment::getPlanId, dto.getPlanId());
            if (paymentMapper.selectCount(existWrapper) > 0) {
                throw new BusinessException(ResultCode.PAYMENT_ORDER_EXISTS);
            }

            Payment payment = new Payment();
            payment.setOrderNo(generateOrderNo());
            payment.setUserId(userId);
            payment.setPlanId(dto.getPlanId());
            payment.setAmount(calculateAmount(dto.getPlanId()));
            payment.setPayChannel(dto.getPayChannel());
            payment.setStatus(PaymentStatus.PENDING.getCode());
            payment.setDescription(getPlanDescription(dto.getPlanId()));
            paymentMapper.insert(payment);

            log.info("支付订单创建成功: orderNo={}, userId={}, planId={}", payment.getOrderNo(), userId, dto.getPlanId());
            return R.ok(payment);
        } finally {
            redisService.delete(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> handleNotify(PaymentNotifyDTO dto) {
        Payment payment = paymentMapper.selectByOrderNo(dto.getOrderNo());
        if (payment == null) {
            log.warn("支付回调订单不存在: orderNo={}", dto.getOrderNo());
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (payment.getStatus() != PaymentStatus.PENDING.getCode()) {
            log.warn("支付订单状态异常: orderNo={}, status={}", dto.getOrderNo(), payment.getStatus());
            return R.ok();
        }

        LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Payment::getId, payment.getId())
                     .set(Payment::getStatus, PaymentStatus.PAID.getCode())
                     .set(Payment::getTransactionId, dto.getTransactionId())
                     .set(Payment::getPaidAt, LocalDateTime.now());
        paymentMapper.update(null, updateWrapper);

        subscriptionService.activateSubscription(payment.getUserId(), payment.getPlanId());

        log.info("支付成功: orderNo={}, transactionId={}", dto.getOrderNo(), dto.getTransactionId());
        return R.ok();
    }

    @Override
    public R<Payment> queryPaymentStatus(String orderNo) {
        Payment payment = paymentMapper.selectByOrderNo(orderNo);
        if (payment == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return R.ok(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> refund(String orderNo, String reason) {
        Long userId = LoginUser.getUserId();
        Payment payment = paymentMapper.selectByOrderNo(orderNo);
        if (payment == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED);
        }
        if (payment.getStatus() != PaymentStatus.PAID.getCode()) {
            throw new BusinessException(ResultCode.PAYMENT_STATUS_INVALID);
        }

        LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Payment::getId, payment.getId())
                     .set(Payment::getStatus, PaymentStatus.REFUNDED.getCode())
                     .set(Payment::getRefundReason, reason)
                     .set(Payment::getRefundedAt, LocalDateTime.now());
        paymentMapper.update(null, updateWrapper);

        log.info("退款申请: orderNo={}, reason={}", orderNo, reason);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> handleRefundNotify(PaymentNotifyDTO dto) {
        Payment payment = paymentMapper.selectByOrderNo(dto.getOrderNo());
        if (payment == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        subscriptionService.deactivateSubscription(payment.getUserId());

        log.info("退款完成: orderNo={}", dto.getOrderNo());
        return R.ok();
    }

    @Override
    public R<PageResult<Payment>> pagePayment(Long userId, Integer status, Integer page, Integer size) {
        Page<Payment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getUserId, userId);
        if (status != null) {
            wrapper.eq(Payment::getStatus, status);
        }
        wrapper.orderByDesc(Payment::getCreatedAt);

        Page<Payment> result = paymentMapper.selectPage(pageParam, wrapper);
        PageResult<Payment> pageResult = new PageResult<>();
        pageResult.setRecords(result.getRecords());
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page);
        pageResult.setSize(size);
        pageResult.setPages(result.getPages());
        return R.ok(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeExpiredOrders() {
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getStatus, PaymentStatus.PENDING.getCode())
               .lt(Payment::getCreatedAt, LocalDateTime.now().minusMinutes(30));
        wrapper.select(Payment::getId);

        Payment closeUpdate = new Payment();
        closeUpdate.setStatus(PaymentStatus.CLOSED.getCode());

        paymentMapper.selectList(wrapper).forEach(payment -> {
            LambdaUpdateWrapper<Payment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Payment::getId, payment.getId())
                         .set(Payment::getStatus, PaymentStatus.CLOSED.getCode());
            paymentMapper.update(null, updateWrapper);
        });

        log.info("超时订单关闭完成");
    }

    private String generateOrderNo() {
        return "PAY" + snowflakeIdUtil.nextId();
    }

    private BigDecimal calculateAmount(Integer planId) {
        if (planId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        switch (planId) {
            case 1:
                return BigDecimal.ZERO;
            case 2:
                return BigDecimal.valueOf(9900);
            case 3:
                return BigDecimal.valueOf(29900);
            default:
                throw new BusinessException(ResultCode.PARAM_ERROR);
        }
    }

    private String getPlanDescription(Integer planId) {
        if (planId == null) {
            return "";
        }
        switch (planId) {
            case 1:
                return "免费版";
            case 2:
                return "专业版-月度订阅";
            case 3:
                return "企业版-月度订阅";
            default:
                return "";
        }
    }
}
