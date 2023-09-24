package com.e2i.wemeet.exception.badrequest;

import com.e2i.wemeet.exception.ErrorCode;

public class ImageCountExceedException extends BadRequestException {

    public ImageCountExceedException() {
        super(ErrorCode.IMAGE_COUNT_EXCEEDED);
    }

}
