package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class InvalidDatabaseKeyToEnumException extends InvalidValueException {

    public InvalidDatabaseKeyToEnumException() {
        super(ErrorCode.INVALID_DATABASE_KEY_TO_ENUM);
    }

    public InvalidDatabaseKeyToEnumException(ErrorCode errorCode) {
        super(errorCode);
    }
}
