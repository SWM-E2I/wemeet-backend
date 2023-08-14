package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollegeCodeValidatorTest {

    private CollegeCodeValidator collegeCodeValidator = new CollegeCodeValidator();

    @DisplayName("유효한 대학 코드 이름을 입력하면 True를 반환한다.")
    @Test
    void isValidTrue() {
        // given
        final String validCollegeCode = "CE-001";

        // when
        boolean valid = collegeCodeValidator.isValid(validCollegeCode, null);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("유효하지 않은 대학 코드 이름을 입력하면 False를 반환한다.")
    @Test
    void isValidFalse() {
        // given
        final String inValidCollegeCode = "ARTS-001";

        // when
        boolean valid = collegeCodeValidator.isValid(inValidCollegeCode, null);

        // then
        assertThat(valid).isFalse();
    }
}