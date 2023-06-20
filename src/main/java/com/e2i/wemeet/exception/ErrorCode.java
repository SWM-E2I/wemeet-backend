package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_SMS_CREDENTIAL(40100, "invalid.sms.credential")

    ;

    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
