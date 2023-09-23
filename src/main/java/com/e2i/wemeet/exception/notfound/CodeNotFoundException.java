package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

public class CodeNotFoundException extends NotFoundException {

    public CodeNotFoundException() {
        super(ErrorCode.CODE_NOT_FOUND);
    }

    public CodeNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
