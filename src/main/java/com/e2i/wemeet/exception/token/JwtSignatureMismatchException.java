package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class JwtSignatureMismatchException extends TokenException{

    public JwtSignatureMismatchException() {
        super(ErrorCode.JWT_SIGNATURE_MISMATCH);
    }

    public JwtSignatureMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
