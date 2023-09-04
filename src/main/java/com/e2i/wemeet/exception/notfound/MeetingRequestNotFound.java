package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

public class MeetingRequestNotFound extends NotFoundException {

    public MeetingRequestNotFound() {
        super(ErrorCode.MEETING_REQUEST_NOT_FOUND);
    }

}
