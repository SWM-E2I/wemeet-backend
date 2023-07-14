package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamAlreadyActiveException extends BadRequestException {

    public TeamAlreadyActiveException() {
        super(ErrorCode.TEAM_ALREADY_ACTIVE);
    }

    public TeamAlreadyActiveException(ErrorCode errorCode) {
        super(errorCode);
    }
}
