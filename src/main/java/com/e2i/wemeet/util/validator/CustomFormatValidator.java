package com.e2i.wemeet.util.validator;

import static com.e2i.wemeet.exception.ErrorCode.INVALID_CREDENTIAL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_EMAIL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_PHONE_FORMAT;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;

/*
  사용자 입력 값 형식 검증 유틸 클래스
*/
public abstract class CustomFormatValidator {

    private CustomFormatValidator() {
    }

    private static final String PHONE_REG = "^\\+8210\\d{8}$";
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
