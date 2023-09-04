package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamNotFoundException extends NotFoundException {

    public TeamNotFoundException() {
        super(ErrorCode.TEAM_NOT_FOUND);
    }

    public TeamNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
