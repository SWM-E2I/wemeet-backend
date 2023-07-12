package com.e2i.wemeet.service.code;

import com.e2i.wemeet.domain.code.Code;
import com.e2i.wemeet.domain.code.CodePk;
import com.e2i.wemeet.domain.code.CodeRepository;
import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidDataFormatException;
import com.e2i.wemeet.exception.notfound.CodeNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CodeServiceImpl implements CodeService {

    private final CodeRepository codeRepository;

    public List<Code> findCodeList(List<String> dataList) {
        List<Code> result = new ArrayList<>();
        for (String data : dataList) {
            String[] splitCode = splitString(data);
            Code code = codeRepository.findById(new CodePk(splitCode[1], splitCode[0]))
                .orElseThrow(CodeNotFoundException::new);

            result.add(code);
        }

        return result;
    }

    private String[] splitString(String input) {
        String[] splitArray = input.split("_");
        if (splitArray.length != 2) {
            throw new InvalidDataFormatException(ErrorCode.INVALID_CODE_FORMAT);
        }

        return splitArray;
    }
}
