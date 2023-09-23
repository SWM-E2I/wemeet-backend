package com.e2i.wemeet.exception.token;

import com.e2i.wemeet.exception.ErrorCode;


public class NotEqualRoleToTokenException extends TokenException {

    public NotEqualRoleToTokenException() {
        super(ErrorCode.NOT_EQUAL_ROLE_TO_TOKEN);
    }

    public NotEqualRoleToTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
