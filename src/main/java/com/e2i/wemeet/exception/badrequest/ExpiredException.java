package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class ExpiredException extends BadRequestException {

    public ExpiredException() {
        super(ErrorCode.EXPIRED);
    }

    public ExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }

}
