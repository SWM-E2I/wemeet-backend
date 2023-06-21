package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    HTTP_METHOD_NOT_ALLOWED(40000, "http.method.not.allowed"),

    INVALID_DATA_FORMAT(40001, "invalid.data.format"),
    INVALID_PHONE_FORMAT(40002, "invalid.phone.format"),
    INVALID_EMAIL_FORMAT(40003, "invalid.email.format"),
    INVALID_CREDENTIAL_FORMAT(40004, "invalid.credential.format"),

    INVALID_SMS_CREDENTIAL(40100, "invalid.sms.credential"),

    UNEXPECTED_INTERNAL(50000, "unexpected.internal")
    ;

    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
