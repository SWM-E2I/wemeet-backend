package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class RefreshTokenNotExistException extends TokenException {

    public RefreshTokenNotExistException() {
        super(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
    }

    public RefreshTokenNotExistException(ErrorCode errorCode) {
        super(errorCode);
    }

}
