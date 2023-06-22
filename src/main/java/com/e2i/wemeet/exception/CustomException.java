package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(final ErrorCode errorCode) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
    }
}
