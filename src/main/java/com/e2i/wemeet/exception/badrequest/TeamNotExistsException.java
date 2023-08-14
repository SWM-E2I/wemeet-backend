package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamNotExistsException extends BadRequestException {

    public TeamNotExistsException() {
        super(ErrorCode.TEAM_NOT_EXISTS);
    }

    public TeamNotExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
