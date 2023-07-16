package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class NotBelongToTeamException extends BadRequestException {

    public NotBelongToTeamException() {
        super(ErrorCode.NOT_BELONG_TO_TEAM);
    }

    public NotBelongToTeamException(ErrorCode errorCode) {
        super(errorCode);
    }
}
