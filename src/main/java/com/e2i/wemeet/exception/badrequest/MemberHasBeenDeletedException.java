package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class MemberHasBeenDeletedException extends BadRequestException {

    public MemberHasBeenDeletedException() {
        super(ErrorCode.MEMBER_HAS_BEEN_DELETED);
    }

    public MemberHasBeenDeletedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
