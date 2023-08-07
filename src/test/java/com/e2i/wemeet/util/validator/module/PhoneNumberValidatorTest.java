package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();

    @DisplayName("+82로 시작하는 핸드폰 번호를 입력하면 true를 반환한다.")
    @Test
    void isValidTrue() {
        // given
        final String validPhoneNumber = "+821077229911";

        // when
        boolean valid = phoneNumberValidator.isValid(validPhoneNumber, null);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("010으로 시작하는 핸드폰 번호를 입력하면 false를 반환한다.")
    @Test
    void isValidFalse() {
        // given
        final String validPhoneNumber = "01077229911";

        // when
        boolean valid = phoneNumberValidator.isValid(validPhoneNumber, null);

        // then
        assertThat(valid).isFalse();
    }

}