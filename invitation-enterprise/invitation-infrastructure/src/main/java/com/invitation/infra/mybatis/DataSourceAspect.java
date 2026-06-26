package com.invitation.infra.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 数据源切面 - 根据方法名切换主从数据源
 */
@Slf4j
@Aspect
@Component
public class DataSourceAspect implements Ordered {

    @Around("@annotation(com.invitation.common.annotation.DataScope) || " +
            "execution(* com.invitation.service..*ServiceImpl.insert*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.update*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.delete*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.save*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        try {
            DynamicDataSourceConfig.setMaster();
            return point.proceed();
        } finally {
            DynamicDataSourceConfig.clear();
        }
    }

    @Around("execution(* com.invitation.service..*ServiceImpl.select*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.get*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.list*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.page*(..)) || " +
            "execution(* com.invitation.service..*ServiceImpl.count*(..))")
    public Object aroundRead(ProceedingJoinPoint point) throws Throwable {
        try {
            DynamicDataSourceConfig.setSlave();
            return point.proceed();
        } finally {
            DynamicDataSourceConfig.clear();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
