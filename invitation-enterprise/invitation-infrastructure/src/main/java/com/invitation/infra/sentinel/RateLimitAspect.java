package com.invitation.infra.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.invitation.common.annotation.RateLimit;
import com.invitation.common.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流切面 - 基于@RateLimit注解和Sentinel实现
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final Map<String, Boolean> ruleRegistered = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化Sentinel规则将在方法调用时按需注册
        log.info("限流切面初始化完成");
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String resourceName = resolveResourceName(joinPoint, rateLimit);
        registerRule(resourceName, rateLimit);

        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            return joinPoint.proceed();
        } catch (com.alibaba.csp.sentinel.slots.block.flow.FlowException e) {
            log.warn("接口限流: resource={}", resourceName);
            throw new RateLimitException();
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

    private String resolveResourceName(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        if (rateLimit.value() != null && !rateLimit.value().isEmpty()) {
            return rateLimit.value();
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getDeclaringClass().getSimpleName() + ":" + method.getName();
    }

    private void registerRule(String resourceName, RateLimit rateLimit) {
        ruleRegistered.computeIfAbsent(resourceName, key -> {
            List<FlowRule> rules = new ArrayList<>(FlowRuleManager.getRules());
            FlowRule rule = new FlowRule();
            rule.setResource(resourceName);
            rule.setGrade(rateLimit.mode() == 0 ? RuleConstant.FLOW_GRADE_QPS : RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(rateLimit.count());
            rule.setControlBehavior(rateLimit.mode());
            rules.add(rule);
            FlowRuleManager.loadRules(rules);
            log.info("注册限流规则: resource={}, count={}", resourceName, rateLimit.count());
            return true;
        });
    }
}
