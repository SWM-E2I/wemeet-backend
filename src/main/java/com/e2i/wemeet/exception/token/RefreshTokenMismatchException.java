package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class RefreshTokenMismatchException extends TokenException {

    public RefreshTokenMismatchException() {
        super(ErrorCode.REFRESH_TOKEN_MISMATCH);
    }

    public RefreshTokenMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
