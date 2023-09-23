package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class ManagerSelfDeletionException extends BadRequestException {

    public ManagerSelfDeletionException() {
        super(ErrorCode.MANAGER_SELF_DELETION);
    }

    public ManagerSelfDeletionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
