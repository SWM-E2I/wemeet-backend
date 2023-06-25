package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class TokenNotFoundException extends TokenException {

    public TokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
