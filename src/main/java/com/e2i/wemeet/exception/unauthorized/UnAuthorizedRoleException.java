package com.e2i.wemeet.exception.unauthorized;

import com.e2i.wemeet.exception.ErrorCode;

/*
* 요청에 필요한 Role이 없을 때 발생
* */
public class UnAuthorizedRoleException extends UnAuthorizedException {

    public UnAuthorizedRoleException() {
        super(ErrorCode.UNAUTHORIZED_ROLE);
    }

    public UnAuthorizedRoleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
