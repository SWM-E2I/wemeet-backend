package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

/*
* Member를 찾을 수 없을 때 발생
* */
public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
