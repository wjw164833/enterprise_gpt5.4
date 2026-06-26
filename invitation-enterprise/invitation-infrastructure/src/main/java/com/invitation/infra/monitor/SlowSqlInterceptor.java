package com.invitation.infra.monitor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 慢SQL拦截器 - 记录执行时间超过阈值的SQL
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
            org.apache.ibatis.session.RowBounds.class, org.apache.ibatis.session.ResultHandler.class})
})
public class SlowSqlInterceptor implements Interceptor {

    /** 慢SQL阈值(毫秒) */
    private static final long SLOW_SQL_THRESHOLD = 3000;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > SLOW_SQL_THRESHOLD) {
                MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
                String sqlId = ms.getId();
                log.warn("慢SQL检测: sqlId={}, duration={}ms", sqlId, duration);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}
