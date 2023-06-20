package com.e2i.wemeet.exception.internal;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

/*
* 서버 에러 예외
* AWS component connection, DB connection..
*/
public class InternalServerException extends CustomException {

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
