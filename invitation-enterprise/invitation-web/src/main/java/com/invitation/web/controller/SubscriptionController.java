package com.invitation.web.controller;

import com.invitation.common.model.R;
import com.invitation.model.dto.subscription.SubscriptionVO;
import com.invitation.service.subscription.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订阅管理控制器
 */
@Tag(name = "订阅管理")
@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "获取当前订阅信息")
    @GetMapping("/current")
    public R<SubscriptionVO> getCurrentSubscription() {
        return subscriptionService.getCurrentSubscription(null);
    }

    @Operation(summary = "检查配额")
    @GetMapping("/quota/check")
    public R<Boolean> checkQuota(@RequestParam String resourceType) {
        boolean hasQuota = subscriptionService.checkQuota(null, resourceType);
        return R.ok(hasQuota);
    }
}
