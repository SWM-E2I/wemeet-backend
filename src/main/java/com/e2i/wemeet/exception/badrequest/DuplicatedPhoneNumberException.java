package com.e2i.wemeet.exception.badrequest;

import static com.e2i.wemeet.exception.ErrorCode.DUPLICATED_PHONE_NUMBER;

import com.e2i.wemeet.exception.ErrorCode;

public class DuplicatedPhoneNumberException extends DuplicatedValueException {

    public DuplicatedPhoneNumberException() {
        super(DUPLICATED_PHONE_NUMBER);
    }

    public DuplicatedPhoneNumberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
