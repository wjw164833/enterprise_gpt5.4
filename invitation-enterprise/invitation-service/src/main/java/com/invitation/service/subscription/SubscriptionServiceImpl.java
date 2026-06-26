package com.invitation.service.subscription;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.invitation.common.constant.RedisKeyConstant;
import com.invitation.common.enums.ResultCode;
import com.invitation.common.enums.SubscriptionPlan;
import com.invitation.common.exception.BusinessException;
import com.invitation.common.model.R;
import com.invitation.infra.redis.RedisService;
import com.invitation.model.dto.subscription.SubscriptionVO;
import com.invitation.model.entity.Subscription;
import com.invitation.model.mapper.SubscriptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 订阅服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final RedisService redisService;

    @Override
    public R<SubscriptionVO> getCurrentSubscription(Long userId) {
        String cacheKey = RedisKeyConstant.SUBSCRIPTION_INFO + userId;
        SubscriptionVO cached = redisService.get(cacheKey, SubscriptionVO.class);
        if (cached != null) {
            return R.ok(cached);
        }

        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId)
               .orderByDesc(Subscription::getCreatedAt)
               .last("LIMIT 1");
        Subscription subscription = subscriptionMapper.selectOne(wrapper);

        SubscriptionVO vo = new SubscriptionVO();
        if (subscription != null) {
            vo.setUserId(userId);
            vo.setPlanId(subscription.getPlanId());
            vo.setPlanName(getPlanName(subscription.getPlanId() != null ? subscription.getPlanId().intValue() : null));
            vo.setInvitationQuota(subscription.getInvitationQuota());
            vo.setInvitationUsed(subscription.getInvitationUsed());
            vo.setAiQuota(subscription.getAiQuota());
            vo.setAiUsed(subscription.getAiUsed());
            vo.setExpired(subscription.getExpireTime() != null && subscription.getExpireTime().isBefore(LocalDateTime.now()));
            vo.setExpireTime(subscription.getExpireTime());
        } else {
            vo.setUserId(userId);
            vo.setPlanId((long) SubscriptionPlan.FREE.getCode());
            vo.setPlanName("免费版");
            vo.setInvitationQuota(3);
            vo.setInvitationUsed(0);
            vo.setAiQuota(0);
            vo.setAiUsed(0);
            vo.setExpired(false);
        }

        redisService.set(cacheKey, vo, 30, TimeUnit.MINUTES);
        return R.ok(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initFreeSubscription(Long userId) {
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId);
        if (subscriptionMapper.selectCount(wrapper) > 0) {
            return;
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanId((long) SubscriptionPlan.FREE.getCode());
        subscription.setInvitationQuota(3);
        subscription.setInvitationUsed(0);
        subscription.setAiQuota(0);
        subscription.setAiUsed(0);
        subscription.setExpireTime(null);
        subscriptionMapper.insert(subscription);

        log.info("初始化免费订阅: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateSubscription(Long userId, Integer planId) {
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId)
               .orderByDesc(Subscription::getCreatedAt)
               .last("LIMIT 1");
        Subscription subscription = subscriptionMapper.selectOne(wrapper);

        if (subscription == null) {
            subscription = new Subscription();
            subscription.setUserId(userId);
        }

        subscription.setPlanId(planId != null ? planId.longValue() : 1L);
        switch (planId) {
            case 2:
                subscription.setInvitationQuota(50);
                subscription.setAiQuota(100);
                break;
            case 3:
                subscription.setInvitationQuota(999);
                subscription.setAiQuota(9999);
                break;
            default:
                subscription.setInvitationQuota(3);
                subscription.setAiQuota(0);
        }
        subscription.setExpireTime(LocalDateTime.now().plusMonths(1));

        if (subscription.getId() != null) {
            subscriptionMapper.updateById(subscription);
        } else {
            subscriptionMapper.insert(subscription);
        }

        String cacheKey = RedisKeyConstant.SUBSCRIPTION_INFO + userId;
        redisService.delete(cacheKey);

        log.info("激活订阅: userId={}, planId={}", userId, planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateSubscription(Long userId) {
        LambdaUpdateWrapper<Subscription> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Subscription::getUserId, userId)
                     .set(Subscription::getPlanId, (long) SubscriptionPlan.FREE.getCode())
                     .set(Subscription::getInvitationQuota, 3)
                     .set(Subscription::getAiQuota, 0)
                     .set(Subscription::getExpireTime, null);
        subscriptionMapper.update(null, updateWrapper);

        String cacheKey = RedisKeyConstant.SUBSCRIPTION_INFO + userId;
        redisService.delete(cacheKey);

        log.info("取消订阅: userId={}", userId);
    }

    @Override
    public boolean checkQuota(Long userId, String resourceType) {
        SubscriptionVO sub = getCurrentSubscription(userId).getData();
        if (sub == null) {
            return false;
        }

        switch (resourceType) {
            case "invitation":
                return sub.getInvitationUsed() < sub.getInvitationQuota();
            case "ai":
                return sub.getAiUsed() < sub.getAiQuota();
            default:
                return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumeQuota(Long userId, String resourceType, int count) {
        LambdaQueryWrapper<Subscription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subscription::getUserId, userId)
               .orderByDesc(Subscription::getCreatedAt)
               .last("LIMIT 1");
        Subscription subscription = subscriptionMapper.selectOne(wrapper);
        if (subscription == null) {
            throw new BusinessException(ResultCode.SUBSCRIPTION_NOT_FOUND);
        }

        LambdaUpdateWrapper<Subscription> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Subscription::getId, subscription.getId());

        switch (resourceType) {
            case "invitation":
                updateWrapper.setSql("invitation_used = invitation_used + {0}", count);
                break;
            case "ai":
                updateWrapper.setSql("ai_used = ai_used + {0}", count);
                break;
            default:
                throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        subscriptionMapper.update(null, updateWrapper);

        String cacheKey = RedisKeyConstant.SUBSCRIPTION_INFO + userId;
        redisService.delete(cacheKey);
    }

    private String getPlanName(Integer planId) {
        if (planId == null) {
            return "免费版";
        }
        switch (planId) {
            case 1:
                return "免费版";
            case 2:
                return "专业版";
            case 3:
                return "企业版";
            default:
                return "免费版";
        }
    }
}
