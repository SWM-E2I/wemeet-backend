package com.e2i.wemeet.util.validator.module;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GenderValidatorTest {

    private final GenderValidator genderValidator = new GenderValidator();

    @DisplayName("올바른 성별을 입력하면 검사에 통과한다.")
    @ValueSource(strings = {"MAN", "WOMAN"})
    @ParameterizedTest
    void valid(String gender) {
        // when
        boolean valid = genderValidator.isValid(gender, null);

        // then
        assertThat(valid).isTrue();
    }

    @DisplayName("잘못된 성별을 입력하면 검사에 통과할 수 없다.")
    @ValueSource(strings = {"MALE", "FEMALE", "m", "w", "남자", "여자"})
    @ParameterizedTest
    void invalid(String gender) {
        // when
        boolean valid = genderValidator.isValid(gender, null);

        // then
        assertThat(valid).isFalse();
    }
}