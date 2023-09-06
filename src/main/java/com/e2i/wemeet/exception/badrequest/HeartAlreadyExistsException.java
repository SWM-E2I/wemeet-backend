package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class HeartAlreadyExistsException extends BadRequestException {

    public HeartAlreadyExistsException() {
        super(ErrorCode.HEART_ALREADY_EXISTS);
    }

    public HeartAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}