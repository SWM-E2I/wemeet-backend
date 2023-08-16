package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

/*
 * 잘못된 요청을 보넀을 경우 발생
 */
public class BadRequestException extends CustomException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

}
