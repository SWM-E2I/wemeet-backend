package com.e2i.wemeet.util.validator;

import static com.e2i.wemeet.exception.ErrorCode.INVALID_CREDENTIAL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_EMAIL_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_NICKNAME_FORMAT;
import static com.e2i.wemeet.exception.ErrorCode.INVALID_PHONE_FORMAT;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

/*
  사용자 입력 값 형식 검증 유틸 클래스
*/
public abstract class CustomFormatValidator {

    private CustomFormatValidator() {
        throw new AssertionError();
    }

    private static final Pattern PHONE_REG = Pattern.compile("^\\+8210\\d{8}$");
    private static final Pattern EMAIL_REG = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$");
    private static final Pattern EMAIL_DOMAIN_REG = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final String[] EXCLUDE_EMAIL_DOMAIN = {
        "gmail.com", "naver.com", "daum.net", "nate.com",
        "kakao.com", "hanmail.net", "icloud.com", "me.com", "mac.com"
    };
    private static final Pattern SMS_CREDENTIAL_REG = Pattern.compile("^\\d{6}$");
    private static final Pattern EMAIL_CREDENTIAL_REG = Pattern.compile("^\\d{6}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣\\s]{1,5}$");
    private static final Pattern CODE_PK_PATTERN = Pattern.compile("^[A-Z]{2}-\\d{3}$");
    private static final Pattern OPEN_CHAT_LINK_PATTERN = Pattern.compile("https://open\\.kakao\\.com/o/[a-zA-Z0-9]{8}");

    public static void validatePhoneFormat(final String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_REG.matcher(phone).matches()) {
            throw new InvalidDataFormatException(INVALID_PHONE_FORMAT);
        }
    }

    public static void validateEmailFormat(final String email) {
        // 이메일 형식을 정의
        if (!EMAIL_REG.matcher(email).matches()) {
            throw new InvalidDataFormatException(INVALID_EMAIL_FORMAT); // 이메일 형식이 아니면 예외 발생
        }

        // 정규표현식을 사용하여 이메일 주소에서 도메인 부분 추출
        Matcher matcher = EMAIL_DOMAIN_REG.matcher(email);

        if (matcher.find()) {
            String domain = matcher.group(1).toLowerCase(); // 도메인 부분을 소문자로 변환

            // 도메인이 유효한 도메인 목록에 포함되어 있는지 확인
            for (String validDomain : EXCLUDE_EMAIL_DOMAIN) {
                if (domain.equals(validDomain)) {
                    throw new InvalidDataFormatException(INVALID_EMAIL_FORMAT); // 유효한 도메인이면 false 반환
                }
            }
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
        if (!StringUtils.hasText(nickname) || !NICKNAME_PATTERN.matcher(nickname.trim()).matches()) {
            throw new InvalidDataFormatException(INVALID_NICKNAME_FORMAT);
        }
    }

    public static void validateCodePkFormat(final String groupCodeIdWithCodeId) {
        if (!StringUtils.hasText(groupCodeIdWithCodeId) || !CODE_PK_PATTERN.matcher(groupCodeIdWithCodeId).matches()) {
            throw new InvalidDataFormatException(ErrorCode.INVALID_DATA_FORMAT);
        }
    }

    public static String validateOpenChatLinkFormat(final String openChatLink) {
        if (!StringUtils.hasText(openChatLink) || !OPEN_CHAT_LINK_PATTERN.matcher(openChatLink).matches()) {
            throw new InvalidDataFormatException(ErrorCode.INVALID_CHAT_LINK_FORMAT);
        }
        return openChatLink;
    }
}
