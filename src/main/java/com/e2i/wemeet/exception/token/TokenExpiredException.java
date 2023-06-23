package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class TokenExpiredException extends TokenException {

    public TokenExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
