package com.e2i.wemeet.service.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        String codeId = "C001";
        String groupCodeId = "G001";
        String codeName = "코드";
        String description = "코드 설명";

        Code testCode = Code.builder()
            .codeId(codeId)
            .codeValue(codeName)
            .build();

        List<String> dataList = List.of("G001_C001");
        when(codeRepository.findById(new CodePk(codeId, groupCodeId))).thenReturn(
            Optional.of(testCode));

        // when
        List<Code> result = codeService.findCodeList(dataList);

        // then
        assertEquals(1, result.size());
    }

    @DisplayName("잘못된 형식의 코드를 입력하면 InvalidDataFormatException이 발생한다.")
    @Test
    void findCodeList_InvalidDataFormatException() {
        // given
        List<String> dataList = List.of("G001");

        // when & then
        assertThrows(InvalidDataFormatException.class, () ->
            codeService.findCodeList(dataList)
        );
    }
}
