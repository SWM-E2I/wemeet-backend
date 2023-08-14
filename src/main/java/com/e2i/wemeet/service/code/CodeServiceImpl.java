package com.e2i.wemeet.service.code;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;

    public List<Code> findCodeList(List<String> codeList) {
        List<CodePk> codePks = codeList.stream()
            .map(CodePk::of)
            .toList();

        return codeRepository.findByCodePkIn(codePks);
    }
}
