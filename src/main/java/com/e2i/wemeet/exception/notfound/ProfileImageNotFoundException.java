package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;


/*
 * ProfileImage를 찾을 수 없을 때 발생
 * */
public class ProfileImageNotFoundException extends NotFoundException {

    public ProfileImageNotFoundException() {
        super(ErrorCode.PROFILE_IMAGE_NOT_FOUND);
    }

    public ProfileImageNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
