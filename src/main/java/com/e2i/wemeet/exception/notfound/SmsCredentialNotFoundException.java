package com.e2i.wemeet.exception.notfound;

import static com.e2i.wemeet.exception.ErrorCode.NOTFOUND_SMS_CREDENTIAL;

import com.e2i.wemeet.exception.ErrorCode;

public class SmsCredentialNotFoundException extends NotFoundException {

    public SmsCredentialNotFoundException() {
        super(NOTFOUND_SMS_CREDENTIAL);
    }

    public SmsCredentialNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
