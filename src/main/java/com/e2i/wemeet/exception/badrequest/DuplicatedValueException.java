package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

public class DuplicatedValueException extends CustomException {

    public DuplicatedValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
