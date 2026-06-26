package com.invitation.common.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页返回体
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;
    /** 当前页码 */
    private long page;
    /** 每页大小 */
    private long size;
    /** 总页数 */
    private long pages;
    /** 数据列表 */
    private List<T> records;

    public PageResult() {}

    public PageResult(long total, long page, long size, List<T> records) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = (total + size - 1) / size;
        this.records = records;
    }

    public static <T> PageResult<T> of(long total, long page, long size, List<T> records) {
        return new PageResult<>(total, page, size, records);
    }

    public static <T> PageResult<T> empty(long page, long size) {
        return new PageResult<>(0, page, size, java.util.Collections.emptyList());
    }
}
