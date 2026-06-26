package com.invitation.infra.sentinel;

import com.invitation.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 降级处理 - Sentinel降级回调
 */
@Slf4j
@Component
public class FallbackHandler {

    /**
     * 通用降级处理
     */
    public R<Void> defaultFallback(Throwable throwable) {
        log.warn("服务降级: {}", throwable.getMessage());
        return R.fail("服务繁忙，请稍后重试");
    }

    /**
     * 邀请函服务降级
     */
    public R<Void> invitationFallback(Long id, Throwable throwable) {
        log.warn("邀请函服务降级: id={}, error={}", id, throwable.getMessage());
        return R.fail("邀请函服务暂时不可用");
    }
}
