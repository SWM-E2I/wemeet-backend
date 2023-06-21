package com.e2i.wemeet.util.validator;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;

import static com.e2i.wemeet.exception.ErrorCode.*;

/*
  사용자 입력 값 형식 검증 유틸 클래스
*/
public abstract class CustomFormatValidator {
    private CustomFormatValidator() {
    }

    private static final String PHONE_REG = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
    private static final String EMAIL_REG = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:ac\\.kr)$";
    private static final String SMS_CREDENTIAL_REG = "^\\d{6}$";
    private static final String EMAIL_CREDENTIAL_REG = "^\\d{6}$";

    public static void validatePhoneFormat(final String phone) {
        if (!phone.matches(PHONE_REG)) {
            throw new InvalidDataFormatException(INVALID_PHONE_FORMAT);
        }
    }

    public static void validateEmailFormat(final String email) {
        if (!email.matches(EMAIL_REG)) {
            throw new InvalidDataFormatException(INVALID_EMAIL_FORMAT);
        }
    }

    public static void validateSmsCredentialFormat(final int smsCredential) {
        if (!String.valueOf(smsCredential).matches(SMS_CREDENTIAL_REG)) {
            throw new InvalidDataFormatException(INVALID_CREDENTIAL_FORMAT);
        }
    }

    public static void validateEmailCredentialFormat(final int emailCredential) {
        if (!String.valueOf(emailCredential).matches(EMAIL_CREDENTIAL_REG)) {
            throw new InvalidDataFormatException(INVALID_CREDENTIAL_FORMAT);
        }
    }
}
