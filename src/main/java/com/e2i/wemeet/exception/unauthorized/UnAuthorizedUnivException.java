package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.ErrorCode;

public class UnAuthorizedUnivException extends UnAuthorizedException {

    public UnAuthorizedUnivException() {
        super(ErrorCode.UNAUTHORIZED_UNIV);
    }

    public UnAuthorizedUnivException(ErrorCode errorCode) {
        super(errorCode);
    }
}
