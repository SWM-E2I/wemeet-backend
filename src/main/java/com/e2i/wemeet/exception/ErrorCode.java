package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    HTTP_METHOD_NOT_ALLOWED(40000, "http.method.not.allowed"),
    INVALID_SMS_CREDENTIAL(40001, "invalid.sms.credential"),
    INVALID_GENDER_VALUE(40002, "invalid.gender.value"),
    INVALID_MBTI_VALUE(40003, "invalid.mbti.value"),

    INVALID_DATA_FORMAT(40004, "invalid.data.format"),
    INVALID_PHONE_FORMAT(40005, "invalid.phone.format"),
    INVALID_EMAIL_FORMAT(40006, "invalid.email.format"),
    INVALID_CREDENTIAL_FORMAT(40007, "invalid.credential.format"),

    ACCESS_TOKEN_EXPIRED(40200, "access.token.expired"),
    REFRESH_TOKEN_EXPIRED(40201, "refresh.token.expired"),

    UNEXPECTED_INTERNAL(50000, "unexpected.internal")
    ;

    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
