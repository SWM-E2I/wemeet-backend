package com.e2i.wemeet.service.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
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

    @DisplayName("코드 리스트 조회에 성공한다.")
    @Test
    void findCodeList_Success() {
        // given
        String codeId = "C001";
        String groupCodeId = "G001";
        String codeName = "코드";
        String description = "코드 설명";

        Code testCode = Code.builder()
            .codePk(new CodePk(codeId, groupCodeId))
            .codeName(codeName)
            .description(description)
            .build();

        List<String> dataList = List.of("G001_C001");
        when(codeRepository.findById(new CodePk("C001", "G001"))).thenReturn(Optional.of(testCode));

        // when
        List<Code> result = codeService.findCodeList(dataList);

        // then
        assertEquals(1, result.size());
        assertEquals(result.get(0).getCodeName(), codeName);
        assertEquals(result.get(0).getDescription(), description);
    }
}
