package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class InvalidMbtiException extends InvalidValueException {

    public InvalidMbtiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidMbtiException() {
        super(ErrorCode.INVALID_MBTI_VALUE);
    }
}
