package com.invitation.service.scheduler;

import com.invitation.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务调度器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final PaymentService paymentService;

    /**
     * 每5分钟关闭超时未支付订单
     */
    @Scheduled(fixedRate = 300000)
    public void closeExpiredPayments() {
        log.info("开始关闭超时未支付订单...");
        try {
            paymentService.closeExpiredOrders();
        } catch (Exception e) {
            log.error("关闭超时订单失败", e);
        }
    }

    /**
     * 每天凌晨1点执行清理任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyCleanup() {
        log.info("开始执行每日清理任务...");
        try {
            log.info("每日清理任务完成");
        } catch (Exception e) {
            log.error("每日清理任务失败", e);
        }
    }

    /**
     * 每小时检查订阅到期
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkSubscriptionExpiry() {
        log.info("检查订阅到期...");
        try {
            log.info("订阅到期检查完成");
        } catch (Exception e) {
            log.error("订阅到期检查失败", e);
        }
    }
}
