package com.e2i.wemeet.exception.notfound;

import com.e2i.wemeet.exception.ErrorCode;

public class CostNotFoundException extends NotFoundException {

    public CostNotFoundException() {
        super(ErrorCode.COST_NOT_FOUND);
    }

}
