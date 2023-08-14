package com.e2i.wemeet.util.validator.module;

import com.e2i.wemeet.domain.member.data.CollegeType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CollegeTypeValidatorTest {

    private CollegeTypeValidator collegeTypeValidator = new CollegeTypeValidator();

    @DisplayName("존재하지 않는 학과 정보를 입력하면 false를 반환한다.")
    @Test
    void isValidFalse() {
        // given
        final String invalidCollegeType = "INVALID_COLLEGE_TYPE";

        // when
        boolean valid = collegeTypeValidator.isValid(invalidCollegeType, null);

        // then
        Assertions.assertThat(valid).isFalse();
    }

    @DisplayName("올바른 학과 정보를 입력하면 true를 반환한다.")
    @Test
    void isValid() {
        // given
        final String invalidCollegeType = CollegeType.ARTS.name();

        // when
        boolean valid = collegeTypeValidator.isValid(invalidCollegeType, null);

        // then
        Assertions.assertThat(valid).isTrue();
    }
}