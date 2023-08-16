package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

public class MeetingNotFound extends NotFoundException {

    public MeetingNotFound() {
        super(ErrorCode.MEETING_NOT_FOUND);
    }

}
