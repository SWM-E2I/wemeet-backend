package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

/*
 * 팀 초대 신청을 찾을 수 없는 경우
 */
public class InvitationNotFoundException extends NotFoundException {

    public InvitationNotFoundException() {
        super(ErrorCode.INVITATION_NOT_FOUND);
    }

    public InvitationNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
