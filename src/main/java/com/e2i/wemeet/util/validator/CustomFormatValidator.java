package com.e2i.wemeet.util.validator;

import static com.e2i.wemeet.exception.ErrorCode.INVALID_CREDENTIAL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_EMAIL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_NICKNAME_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_PHONE_FORMAT;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import java.util.regex.Pattern;

/*
  사용자 입력 값 형식 검증 유틸 클래스
*/
public abstract class CustomFormatValidator {

    private CustomFormatValidator() {
        throw new AssertionError();
    }

    private static final Pattern PHONE_REG = Pattern.compile("^\\+8210\\d{8}$");
    private static final Pattern EMAIL_REG = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:ac\\.kr)$");
    private static final Pattern SMS_CREDENTIAL_REG = Pattern.compile("^\\d{6}$");
    private static final Pattern EMAIL_CREDENTIAL_REG = Pattern.compile("^\\d{6}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣\\s]{2,10}$");

    public static void validatePhoneFormat(final String phone) {
        if (!PHONE_REG.matcher(phone).matches()) {
            throw new InvalidDataFormatException(INVALID_PHONE_FORMAT);
        }
    }

    public static void validateEmailFormat(final String email) {
        if (!EMAIL_REG.matcher(email).matches()) {
            throw new InvalidDataFormatException(INVALID_EMAIL_FORMAT);
        }
    }

    public static void validateSmsCredentialFormat(final int smsCredential) {
        if (!SMS_CREDENTIAL_REG.matcher(String.valueOf(smsCredential)).matches()) {
            throw new InvalidDataFormatException(INVALID_CREDENTIAL_FORMAT);
        }
    }

    public static void validateEmailCredentialFormat(final int emailCredential) {
        if (!EMAIL_CREDENTIAL_REG.matcher(String.valueOf(emailCredential)).matches()) {
            throw new InvalidDataFormatException(INVALID_CREDENTIAL_FORMAT);
        }
    }

    public static void validateNicknameFormat(final String nickname) {
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new InvalidDataFormatException(INVALID_NICKNAME_FORMAT);
        }
    }
}
