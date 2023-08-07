package com.e2i.wemeet.domain.code;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.config.RepositoryTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest
class CodeRepositoryTest {

    @Autowired
    private CodeRepository codeRepository;

    @DisplayName("대학교명으로 대학교 코드를 조회할 수 있다.")
    @Test
    void findByCodeValue_CollegeCode() {
        // given
        String collegeName = "서울대학교";

        // when
        Optional<Code> collegeCode = codeRepository.findByCodeValue(collegeName);

        // then
        assertThat(collegeCode).isNotEmpty();
        assertThat(collegeCode.get().getCodePk().getCodeId()).isEqualTo("001");
    }

    @DisplayName("대학교명이 존재하지 않으면 대학교 코드를 조회할 수 없다.")
    @Test
    void findByCodeValue_invalidCollegeCode() {
        // given
        String collegeName = "서운대학교";

        // when
        Optional<Code> collegeCode = codeRepository.findByCodeValue(collegeName);

        // then
        assertThat(collegeCode).isEmpty();
    }
}
