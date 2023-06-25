package com.e2i.wemeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_SMS_CREDENTIAL(40001, "invalid.sms.credential"),
    INVALID_GENDER_VALUE(40002, "invalid.gender.value"),
    INVALID_MBTI_VALUE(40003, "invalid.mbti.value"),
    UNEXPECTED_INTERNAL(50000, "unexpected.internal"),
    AWS_SNS_MESSAGE_TRANSFER_ERROR(50100, "aws.sns.message.transfer.error");

    private final int code;
    private final String messageKey;

    ErrorCode(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
