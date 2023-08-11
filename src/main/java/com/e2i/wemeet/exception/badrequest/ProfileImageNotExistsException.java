package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class ProfileImageNotExistsException extends BadRequestException {

    public ProfileImageNotExistsException() {
        super(ErrorCode.PROFILE_IMAGE_NOT_EXISTS);
    }

    public ProfileImageNotExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
