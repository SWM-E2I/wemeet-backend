package com.e2i.wemeet.util.validator;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomFormatValidatorTest {

    @DisplayName("주어진 입력 값이 핸드폰 형식에 맞으면 예외가 발생하지 않는다")
    @Test
    void validatePhoneFormat() {
        final String phone = "+821032452345";
        assertThatNoException()
            .isThrownBy(() -> CustomFormatValidator.validatePhoneFormat(phone));
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
        assertThatNoException()
            .isThrownBy(() -> CustomFormatValidator.validateEmailFormat(email));
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

    @DisplayName("닉네임 형식이 맞으면 검사에 통과한다")
    @ValueSource(strings = {"군산 피바", "욤", "사란짱", "엘리멘탈로 "})
    @ParameterizedTest
    void validateNicknameFormat(String nickname) {
        assertThatNoException()
            .isThrownBy(() -> CustomFormatValidator.validateNicknameFormat(nickname));
    }

    @DisplayName("닉네임 형식에 맞지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"", " ", "띄 어 쓰기", "여섯글자불가"})
    @ParameterizedTest
    void validateNicknameFormatFail(String nickname) {
        assertThatThrownBy(() -> CustomFormatValidator.validateNicknameFormat(nickname))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("코드 형식에 맞으면 검사에 통과한다.")
    @ValueSource(strings = {"CE-001", "CE-999", "AR-010", "CE-003"})
    @ParameterizedTest
    void validateCodePkFormat(String groupCodeWithCodeId) {
        assertThatNoException()
            .isThrownBy(() -> CustomFormatValidator.validateCodePkFormat(groupCodeWithCodeId));
    }

    @DisplayName("코드 형식에 맞지 않으면 예외가 발생한다.")
    @ValueSource(strings = {"CE-1000", "Ea-001", "1E-010", "10-003"})
    @ParameterizedTest
    void validateCodePkFormatFail(String groupCodeWithCodeId) {
        assertThatThrownBy(() -> CustomFormatValidator.validateNicknameFormat(groupCodeWithCodeId))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }

    @DisplayName("코드 형식에 맞지 않으면 예외가 발생한다.")
    @Test
    void validateCodePkFormatFails() {
        assertThatThrownBy(() -> CustomFormatValidator.validateNicknameFormat(null))
            .isExactlyInstanceOf(InvalidDataFormatException.class);
    }
}
