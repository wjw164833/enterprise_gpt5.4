package com.invitation.common.exception;

import com.invitation.common.enums.ResultCode;

/**
 * 限流异常
 */
public class RateLimitException extends BusinessException {

    public RateLimitException() {
        super(ResultCode.RATE_LIMIT_EXCEEDED);
    }

    public RateLimitException(String message) {
        super(ResultCode.RATE_LIMIT_EXCEEDED.getCode(), message);
    }
}
