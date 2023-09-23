package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;

public class JwtClaimIncorrectException extends TokenException {

    public JwtClaimIncorrectException() {
        super(ErrorCode.JWT_CLAIM_INCORRECT);
    }

    public JwtClaimIncorrectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
