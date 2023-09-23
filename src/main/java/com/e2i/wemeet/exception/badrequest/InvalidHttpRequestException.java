package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

import static com.e2i.wemeet.exception.ErrorCode.HTTP_METHOD_NOT_ALLOWED;

/*
* 잘못된 Http 요청일 때 발생
* */
public class InvalidHttpRequestException extends CustomException {

    public InvalidHttpRequestException() {
        super(HTTP_METHOD_NOT_ALLOWED);
    }

    public InvalidHttpRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
