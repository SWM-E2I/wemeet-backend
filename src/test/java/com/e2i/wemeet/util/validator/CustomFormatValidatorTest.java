package com.e2i.wemeet.util.validator;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomFormatValidatorTest {

    @DisplayName("주어진 입력 값이 핸드폰 형식에 맞으면 예외가 발생하지 않는다")
    @Test
    void validatePhoneFormat() {
        final String phone = "01032452345";

        CustomFormatValidator.validatePhoneFormat(phone);
    }

    @DisplayName("주어진 입력 값이 핸드폰 형식에 맞지 않으면 예외가 발생한다")
    @ValueSource(strings = {"010c83261234", "010-2341-1923", "010.2342.5123"})
    @ParameterizedTest
    void validatePhoneFormatFail(String phone) {
        assertThatThrownBy(() -> CustomFormatValidator.validatePhoneFormat(phone))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("주어진 입력 값이 이메일 형식에 맞으면 예외가 발생하지 않는다.")
    @ValueSource(strings = {"sign841@gs.anyang.ac.kr", "goefew12@koreka.ac.kr"})
    @ParameterizedTest
    void validateEmailFormat(String email) {
        CustomFormatValidator.validateEmailFormat(email);
    }

    @DisplayName("주어진 입력 값이 이메일 형식에 맞지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"sign@!@#aver.cvav.com", "sign0121@naver.com"})
    @ParameterizedTest
    void validateEmailFormatFail(String email) {
        assertThatThrownBy(() -> CustomFormatValidator.validateEmailFormat(email))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("SMS 인증 번호가 형식에 맞지 않으면 예외가 발생한다.")
    @ValueSource(ints = {1234567, 12345})
    @ParameterizedTest
    void validateSmsCredentialFormat(int credential) {
        assertThatThrownBy(() -> CustomFormatValidator.validateSmsCredentialFormat(credential))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("Email 인증 번호가 형식에 맞지 않으면 예외가 발생한다.")
    @ValueSource(ints = {1234567, 12345})
    @ParameterizedTest
    void validateEmailCredentialFormat(int credential) {
        assertThatThrownBy(() -> CustomFormatValidator.validateEmailCredentialFormat(credential))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }
}