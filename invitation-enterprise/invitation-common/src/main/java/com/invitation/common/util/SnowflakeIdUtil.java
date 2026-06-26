package com.invitation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 雪花ID生成工具
 */
@Slf4j
@Component
public class SnowflakeIdUtil {

    /** 起始时间戳 2024-01-01 */
    private static final long EPOCH = 1704067200000L;
    /** 机器ID位数 */
    private static final long WORKER_ID_BITS = 5L;
    /** 数据中心ID位数 */
    private static final long DATACENTER_ID_BITS = 5L;
    /** 序列号位数 */
    private static final long SEQUENCE_BITS = 12L;
    /** 最大机器ID */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /** 最大数据中心ID */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    /** 序列号掩码 */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    /** 机器ID左移位数 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 数据中心ID左移位数 */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    /** 时间戳左移位数 */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private long workerId = 1;
    private long datacenterId = 1;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID");
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成下一个ID（字符串形式）
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
