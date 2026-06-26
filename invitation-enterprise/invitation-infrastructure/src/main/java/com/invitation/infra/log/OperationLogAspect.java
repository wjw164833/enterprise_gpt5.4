package com.invitation.infra.log;

import com.invitation.common.annotation.OperationLog;
import com.invitation.common.model.LoginUser;
import com.invitation.common.util.IpUtil;
import com.invitation.model.entity.AuditLog;
import com.invitation.model.mapper.AuditLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面 - AOP记录审计日志
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private IpUtil ipUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                long duration = System.currentTimeMillis() - startTime;
                saveLog(joinPoint, operationLog, result, exception, duration);
            } catch (Exception e) {
                log.warn("保存操作日志失败: {}", e.getMessage());
            }
        }
    }

    @Async("taskExecutor")
    public void saveLog(ProceedingJoinPoint joinPoint, OperationLog operationLog,
                        Object result, Exception exception, long duration) {
        LoginUser loginUser = LoginUser.get();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(loginUser != null ? loginUser.getUserId() : null);
        auditLog.setUsername(loginUser != null ? loginUser.getNickname() : null);
        auditLog.setModule(operationLog.module().isEmpty() ? method.getDeclaringClass().getSimpleName() : operationLog.module());
        auditLog.setAction(operationLog.action().isEmpty() ? method.getName() : operationLog.action());
        auditLog.setIpAddress(ipUtil.getClientIp());

        // 记录详情
        Map<String, Object> detailMap = new HashMap<>(4);
        detailMap.put("duration", duration);
        detailMap.put("success", exception == null);
        if (exception != null) {
            detailMap.put("error", exception.getMessage());
        }
        try {
            auditLog.setDetail(objectMapper.writeValueAsString(detailMap));
        } catch (Exception e) {
            auditLog.setDetail("{}");
        }

        auditLogMapper.insert(auditLog);
    }
}
