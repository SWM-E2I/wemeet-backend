package com.e2i.wemeet.domain.code;

import static org.assertj.core.api.Assertions.assertThat;

import com.e2i.wemeet.support.config.RepositoryTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @DisplayName("코드 PK로 대학 코드를 조회할 수 있다.")
    @Test
    void findByCodePk() {
        // given
        final String groupCodeIdWithCodeId = "CE-001";
        CodePk codePk = CodePk.of(groupCodeIdWithCodeId);

        // when
        Optional<Code> code = codeRepository.findByCodePk(codePk);

        // then
        assertThat(code).isNotEmpty();
        assertThat(code.get().getCodeValue()).isEqualTo("서울대학교");
    }

    @DisplayName("잘못된 코드 PK로 대학 코드를 조회할 수 없다.")
    @ValueSource(strings = {"CE-999", "CE-000", "CES-001"})
    @ParameterizedTest
    void findByCodePkInvalidPk(String invalidGroupCodeIdWithCodeId) {
        // given
        CodePk codePk = CodePk.of(invalidGroupCodeIdWithCodeId);

        // when
        Optional<Code> code = codeRepository.findByCodePk(codePk);

        // then
        assertThat(code).isEmpty();
    }
}
