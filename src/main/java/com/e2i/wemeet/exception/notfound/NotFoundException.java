package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.CustomException;
import com.e2i.wemeet.exception.ErrorCode;

/*
 * 입력은 올바르나, 해당 객체를 찾을 수 없을 때 발생
 */
public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
