package com.invitation.service.subscription;

import com.invitation.common.model.R;
import com.invitation.model.dto.subscription.SubscriptionVO;
import com.invitation.model.entity.Subscription;

/**
 * 订阅服务接口
 */
public interface SubscriptionService {

    /**
     * 获取当前订阅信息
     */
    R<SubscriptionVO> getCurrentSubscription(Long userId);

    /**
     * 初始化免费订阅
     */
    void initFreeSubscription(Long userId);

    /**
     * 激活订阅
     */
    void activateSubscription(Long userId, Integer planId);

    /**
     * 取消订阅
     */
    void deactivateSubscription(Long userId);

    /**
     * 检查配额
     */
    boolean checkQuota(Long userId, String resourceType);

    /**
     * 消耗配额
     */
    void consumeQuota(Long userId, String resourceType, int count);
}
