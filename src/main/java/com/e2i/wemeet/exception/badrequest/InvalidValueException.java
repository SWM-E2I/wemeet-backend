package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

/*
* 잘못된 값을 입력 했을 때 발생
*/
public class InvalidValueException extends CustomException {
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
