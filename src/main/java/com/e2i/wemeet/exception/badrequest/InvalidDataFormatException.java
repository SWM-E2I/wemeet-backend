package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

import static com.e2i.wemeet.exception.ErrorCode.INVALID_DATA_FORMAT;

/*
* 입력받은 데이터가 정해진 형식에 어긋날 때 발생
* */
public class InvalidDataFormatException extends InvalidValueException {

    public InvalidDataFormatException() {
        super(INVALID_DATA_FORMAT);
    }

    public InvalidDataFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
