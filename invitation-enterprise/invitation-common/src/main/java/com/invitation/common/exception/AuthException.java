package com.invitation.common.exception;

import com.invitation.common.enums.ResultCode;

/**
 * 认证异常
 */
public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }

    public AuthException(ResultCode resultCode) {
        super(resultCode);
    }
}
