package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class NonTeamMemberException extends BadRequestException {

    public NonTeamMemberException() {
        super(ErrorCode.NON_TEAM_MEMBER);
    }

    public NonTeamMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
