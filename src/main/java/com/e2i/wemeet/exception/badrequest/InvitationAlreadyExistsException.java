package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class InvitationAlreadyExistsException extends BadRequestException {

    public InvitationAlreadyExistsException() {
        super(ErrorCode.INVITATION_ALREADY_EXISTS);
    }

    public InvitationAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
