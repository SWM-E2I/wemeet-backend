package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.ErrorCode;

/*
* 요청에 필요한 Credit이 없을 때 발생
* */
public class UnAuthorizedCreditException extends UnAuthorizedException {

    public UnAuthorizedCreditException() {
        super(ErrorCode.UNAUTHORIZED_CREDIT);
    }

    public UnAuthorizedCreditException(ErrorCode errorCode) {
        super(errorCode);
    }
}
