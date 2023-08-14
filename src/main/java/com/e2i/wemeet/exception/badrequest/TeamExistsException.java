package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamExistsException extends BadRequestException {

    public TeamExistsException() {
        super(ErrorCode.TEAM_ALREADY_EXISTS);
    }

    public TeamExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
