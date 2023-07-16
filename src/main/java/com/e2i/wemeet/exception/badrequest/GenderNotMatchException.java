package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class GenderNotMatchException extends BadRequestException {

    public GenderNotMatchException() {
        super(ErrorCode.GENDER_NOT_MATCH);
    }

    public GenderNotMatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
