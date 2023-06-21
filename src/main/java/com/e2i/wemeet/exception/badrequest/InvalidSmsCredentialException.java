package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

import static com.e2i.wemeet.exception.ErrorCode.INVALID_SMS_CREDENTIAL;

/*
* SMS 문자 인증 실패시 발생
*/
public class InvalidSmsCredentialException extends InvalidValueException {

    public InvalidSmsCredentialException() {
        super(INVALID_SMS_CREDENTIAL);
    }

    public InvalidSmsCredentialException(ErrorCode errorCode) {
        super(errorCode);
    }

}
