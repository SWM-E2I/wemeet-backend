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

    NOTFOUND_SMS_CREDENTIAL(40100, "notfound.sms.credential"),

    ACCESS_TOKEN_EXPIRED(40200, "access.token.expired"),
    REFRESH_TOKEN_EXPIRED(40201, "refresh.token.expired"),
    JWT_SIGNATURE_MISMATCH(40202, "jwt.signature.mismatch"),
    JWT_CLAIM_INCORRECT(40203, "jwt.claim.incorrect"),
    JWT_DECODE(40204, "jwt.decode"),
    REFRESH_TOKEN_MISMATCH(40205, "refresh.token.mismatch"),
    REFRESH_TOKEN_NOT_FOUND(40206, "refresh.token.not.found"),

    UNEXPECTED_INTERNAL(50000, "unexpected.internal"),
    AWS_SNS_MESSAGE_TRANSFER_ERROR(50100, "aws.sns.message.transfer.error");
    ;

    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
