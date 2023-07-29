package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.ErrorCode;

public class AccessTokenNotFoundException extends UnAuthorizedException {

    public AccessTokenNotFoundException() {
        super(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
    }

    public AccessTokenNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
