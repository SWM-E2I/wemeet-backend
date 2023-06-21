package com.e2i.wemeet.util.validator;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CustomFormatValidatorTest {

    @DisplayName("주어진 입력 값이 핸드폰 형식에 맞으면 예외가 발생하지 않는다")
    @Test
    void validatePhoneFormat() {
        final String phone1 = "01032452345";
        final String phone2 = "010-8955-1823";
        final String phone3 = "010-5013-2812";

        CustomFormatValidator.validatePhoneFormat(phone1);
        CustomFormatValidator.validatePhoneFormat(phone2);
        CustomFormatValidator.validatePhoneFormat(phone3);
    }

    @DisplayName("주어진 입력 값이 핸드폰 형식에 맞지 않으면 예외가 발생한다")
    @Test
    void validatePhoneFormatFail() {
        final String phone1 = "010c83245123";
        final String phone2 = "010183245123";
        final String phone3 = "0108324512!";

        assertAll(
                () -> assertThatThrownBy(() -> CustomFormatValidator.validatePhoneFormat(phone1))
                        .isExactlyInstanceOf(InvalidDataFormatException.class),
                () -> assertThatThrownBy(() -> CustomFormatValidator.validatePhoneFormat(phone2))
                        .isExactlyInstanceOf(InvalidDataFormatException.class),
                () -> assertThatThrownBy(() -> CustomFormatValidator.validatePhoneFormat(phone3))
                        .isExactlyInstanceOf(InvalidDataFormatException.class)
        );
    }

    @DisplayName("주어진 입력 값이 이메일 형식에 맞으면 예외가 발생하지 않는다.")
    @Test
    void validateEmailFormat() {
        final String email1 = "sign841@gs.anyang.ac.kr";
        final String email2 = "goefew12@koreka.ac.kr";

        CustomFormatValidator.validateEmailFormat(email1);
        CustomFormatValidator.validateEmailFormat(email2);
    }

    @DisplayName("주어진 입력 값이 이메일 형식에 맞지 않으면 예외가 발생한다.")
    @Test
    void validateEmailFormatFail() {
        final String email1 = "sign@!@#aver.cvav.com";
        final String email2 = "sign0121@naver.com";

        assertThatThrownBy(() -> CustomFormatValidator.validateEmailFormat(email2))
                .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("SMS 인증 번호가 형식에 맞지 않으면 예외가 발생한다.")
    @Test
    void validateSmsCredentialFormat() {
        final int credential = 14323812;

        assertThatThrownBy(() -> CustomFormatValidator.validateSmsCredentialFormat(credential))
                .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("Email 인증 번호가 형식에 맞지 않으면 예외가 발생한다.")
    @Test
    void validateEmailCredentialFormat() {
        final int credential = 14323812;

        assertThatThrownBy(() -> CustomFormatValidator.validateSmsCredentialFormat(credential))
                .isExactlyInstanceOf(InvalidDataFormatException.class);
    }
}