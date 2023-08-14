package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MbtiValidatorTest {

    private MbtiValidator mbtiValidator = new MbtiValidator();

    @DisplayName("유효한 MBTI 값을 입력하면 True를 반환한다.")
    @Test
    void isValidTrue() {
        // given
        final String validMbti = "INFJ";

        // when
        boolean valid = mbtiValidator.isValid(validMbti, null);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("유효하지 않은 값을 입력하면 False를 반환한다.")
    @Test
    void isValidFalse() {
        // given
        final String inValidMbti = "PNFP";

        // when
        boolean valid = mbtiValidator.isValid(inValidMbti, null);

        // then
        assertThat(valid).isFalse();
    }
}