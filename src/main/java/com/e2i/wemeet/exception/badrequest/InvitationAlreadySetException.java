package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

/*
 * 이미 수락하거나 거절한 팀 초대 신청일 경우
 */
public class InvitationAlreadySetException extends BadRequestException {

    public InvitationAlreadySetException() {
        super(ErrorCode.INVITATION_ALREADY_SET);
    }

    public InvitationAlreadySetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
