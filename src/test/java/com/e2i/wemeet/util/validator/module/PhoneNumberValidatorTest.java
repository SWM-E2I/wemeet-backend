package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();

    @DisplayName("+82로 시작하는 핸드폰 번호를 입력하면 검사를 통과한다.")
    @ValueSource(strings = {"+821044447777", "+821077229911", "+821034129813"})
    @ParameterizedTest
    void isValidTrue(final String phoneNumber) {
        // when
        boolean valid = phoneNumberValidator.isValid(phoneNumber, null);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("주어진 핸드폰 번호가 형식에 어긋난다면 검사에 통과할 수 없다.")
    @ValueSource(strings = {"+8210+8210123", "+82101234a678", "+821100004444", "01077229911", "+8210123456789"})
    @ParameterizedTest
    void isValidFalse(final String phoneNumber) {
        // when
        boolean valid = phoneNumberValidator.isValid(phoneNumber, null);

        // then
        assertThat(valid).isFalse();
    }

}