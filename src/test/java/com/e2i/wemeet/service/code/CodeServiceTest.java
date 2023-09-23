package com.e2i.wemeet.service.code;

import static com.e2i.wemeet.support.fixture.code.CodeFixture.ANYANG_UNIVERSITY;
import static com.e2i.wemeet.support.fixture.code.CodeFixture.HANYANG_UNIVERSITY;
import static com.e2i.wemeet.support.fixture.code.CodeFixture.KOREA_UNIVERSITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private CodeRepository codeRepository;

    @InjectMocks
    private CodeServiceImpl codeService;

    // TODO :: refactoring
    @DisplayName("코드 리스트 조회에 성공한다.")
    @Test
    void findCodeList_Success() {
        // given
        final String code1 = "CE-001";
        final String code2 = "CE-002";
        final String code3 = "CE-003";
        List<String> codeIdWithGroupCodeIds = List.of(code1, code2, code3);

        List<Code> codes = List.of(
            HANYANG_UNIVERSITY.create(),
            KOREA_UNIVERSITY.create(),
            ANYANG_UNIVERSITY.create()
        );

        when(codeRepository.findByCodePkIn(anyList()))
            .thenReturn(codes);

        // when
        List<Code> codeList = codeService.findCodeList(codeIdWithGroupCodeIds);

        // then
        assertThat(codes).hasSize(3)
            .extracting("codePk.codeId", "codeValue")
            .containsExactlyInAnyOrder(
                tuple(HANYANG_UNIVERSITY.getCodeId(), HANYANG_UNIVERSITY.getCodeValue()),
                tuple(KOREA_UNIVERSITY.getCodeId(), KOREA_UNIVERSITY.getCodeValue()),
                tuple(ANYANG_UNIVERSITY.getCodeId(), ANYANG_UNIVERSITY.getCodeValue())
            );
    }

    @DisplayName("잘못된 형식의 코드를 입력하면 InvalidDataFormatException이 발생한다.")
    @ValueSource(strings = {"CES-001", "C-002", "2K-003"})
    @ParameterizedTest
    void findCodeList_InvalidDataFormatException(String codeIdWithGroupCodeId) {
        // given
        List<String> dataList = List.of(codeIdWithGroupCodeId);

        // when & then
        assertThatThrownBy(() -> codeService.findCodeList(dataList))
            .isInstanceOf(InvalidDataFormatException.class);
    }
}
