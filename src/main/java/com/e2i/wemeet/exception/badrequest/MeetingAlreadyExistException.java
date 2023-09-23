package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class MeetingAlreadyExistException extends BadRequestException {

    public MeetingAlreadyExistException() {
        super(ErrorCode.MEETING_ALREADY_EXIST);
    }

}
