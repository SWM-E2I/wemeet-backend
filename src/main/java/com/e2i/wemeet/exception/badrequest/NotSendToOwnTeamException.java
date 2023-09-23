package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class NotSendToOwnTeamException extends BadRequestException {

    public NotSendToOwnTeamException() {
        super(ErrorCode.NOT_SEND_TO_OWN_TEAM);
    }

    public NotSendToOwnTeamException(ErrorCode errorCode) {
        super(errorCode);
    }
}
