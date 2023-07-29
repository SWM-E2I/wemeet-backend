package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class TeamHasBeenDeletedException extends BadRequestException {

    public TeamHasBeenDeletedException() {
        super(ErrorCode.TEAM_HAS_BEEN_DELETED);
    }

    public TeamHasBeenDeletedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
