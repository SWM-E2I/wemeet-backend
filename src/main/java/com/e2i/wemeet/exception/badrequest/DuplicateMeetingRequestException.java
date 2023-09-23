package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class DuplicateMeetingRequestException extends BadRequestException {

    public DuplicateMeetingRequestException() {
        super(ErrorCode.DUPLICATE_MEETING_REQUEST);
    }

}
