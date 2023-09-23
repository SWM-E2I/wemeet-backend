package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class JwtExpiredException extends TokenException {

    public JwtExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
