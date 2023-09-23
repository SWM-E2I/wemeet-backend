package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.ErrorCode;

/*
* 요청에 필요한 Credit이 없을 때 발생
* */
public class CreditNotEnoughException extends UnAuthorizedException {

    public CreditNotEnoughException() {
        super(ErrorCode.UNAUTHORIZED_CREDIT);
    }

    public CreditNotEnoughException(ErrorCode errorCode) {
        super(errorCode);
    }
}
