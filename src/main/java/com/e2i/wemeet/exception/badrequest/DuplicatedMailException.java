package com.e2i.wemeet.exception.badrequest;

import static com.e2i.wemeet.exception.ErrorCode.DUPLICATED_MAIL;

import com.e2i.wemeet.exception.ErrorCode;

public class DuplicatedMailException extends DuplicatedValueException {

    public DuplicatedMailException() {
        super(DUPLICATED_MAIL);
    }

    public DuplicatedMailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
