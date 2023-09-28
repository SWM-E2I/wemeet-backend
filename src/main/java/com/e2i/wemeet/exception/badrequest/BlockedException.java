package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class BlockedException extends BadRequestException {

    public BlockedException() {
        super(ErrorCode.BLOCKED_MEMBER);
    }

    public BlockedException(ErrorCode errorCode) {
        super(errorCode);
    }

}
