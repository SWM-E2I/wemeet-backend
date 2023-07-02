package com.e2i.wemeet.service.code;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final CodeRepository codeRepository;

    public Code findCode(String code, String groupCode) {
        return codeRepository.findById(new CodePk(code, groupCode))
            .orElseThrow(CodeNotFoundException::new);
    }
}
