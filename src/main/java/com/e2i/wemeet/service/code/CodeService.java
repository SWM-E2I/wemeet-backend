package com.e2i.wemeet.service.code;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final CodeRepository codeRepository;

    public Code findCode(String data) {
        String[] code = splitString(data);
        return codeRepository.findById(new CodePk(code[1], code[0]))
            .orElseThrow(CodeNotFoundException::new);
    }

    private String[] splitString(String input) {
        String[] splitArray = input.split("_");
        if (splitArray.length != 2) {
            throw new InvalidDataFormatException(ErrorCode.INVALID_CODE_FORMAT);
        }

        return splitArray;
    }
}
