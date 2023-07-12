package com.e2i.wemeet.service.code;

import com.e2i.wemeet.domain.code.Code;
import java.util.List;

public interface CodeService {

    /*
     * 코드 리스트 조회
     */
    List<Code> findCodeList(List<String> dataList);
}
