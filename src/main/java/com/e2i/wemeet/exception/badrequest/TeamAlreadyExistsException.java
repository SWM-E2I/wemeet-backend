package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamAlreadyExistsException extends BadRequestException {

    public TeamAlreadyExistsException() {
        super(ErrorCode.TEAM_ALREADY_EXISTS);
    }

    public TeamAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
