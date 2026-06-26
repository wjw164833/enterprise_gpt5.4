package com.invitation.infra.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置 - 主从读写分离
 */
@Slf4j
@Component
public class DynamicDataSourceConfig extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @PostConstruct
    public void init() {
        DataSource masterDataSource = dataSourceProperties.initializeDataSourceBuilder().build();
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put("master", masterDataSource);
        targetDataSources.put("slave", masterDataSource);
        setDefaultTargetDataSource(masterDataSource);
        setTargetDataSources(targetDataSources);
        log.info("Dynamic datasource initialized with single dev datasource");
    }

    public static void setMaster() {
        CONTEXT.set("master");
    }

    public static void setSlave() {
        CONTEXT.set("slave");
    }

    public static void clear() {
        CONTEXT.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String key = CONTEXT.get();
        return key != null ? key : "master";
    }
}
