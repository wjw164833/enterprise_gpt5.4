package com.invitation.infra.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义指标 - 业务埋点
 */
@Slf4j
@Component
public class CustomMetrics {

    @Autowired
    private MeterRegistry meterRegistry;

    /** 邀请函创建计数 */
    public void recordInvitationCreate(Long userId) {
        Counter.builder("invitation.create.count")
                .tag("userId", String.valueOf(userId))
                .register(meterRegistry)
                .increment();
    }

    /** 邀请函发布计数 */
    public void recordInvitationPublish(Long invitationId) {
        Counter.builder("invitation.publish.count")
                .tag("invitationId", String.valueOf(invitationId))
                .register(meterRegistry)
                .increment();
    }

    /** 宾客回复计数 */
    public void recordGuestReply(Long invitationId, int replyStatus) {
        Counter.builder("guest.reply.count")
                .tag("invitationId", String.valueOf(invitationId))
                .tag("status", String.valueOf(replyStatus))
                .register(meterRegistry)
                .increment();
    }

    /** 支付计数 */
    public void recordPayment(int payType, String status) {
        Counter.builder("payment.count")
                .tag("payType", String.valueOf(payType))
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }

    /** API响应时间记录 */
    public void recordApiResponse(String apiName, long durationMs) {
        Timer.builder("api.response.time")
                .tag("api", apiName)
                .register(meterRegistry)
                .record(java.time.Duration.ofMillis(durationMs));
    }

    /** SMS发送计数 */
    public void recordSmsSend(boolean success) {
        Counter.builder("sms.send.count")
                .tag("result", success ? "success" : "fail")
                .register(meterRegistry)
                .increment();
    }
}
