package com.e2i.wemeet.exception;

import com.e2i.wemeet.dto.response.ResponseStatus;

public record ErrorResponse(
    ResponseStatus status,
    int code,
    String message
) {

    public static ErrorResponse fail(int code, String message) {
        return new ErrorResponse(ResponseStatus.FAIL, code, message);
    }

    public static ErrorResponse error(int code, String message) {
        return new ErrorResponse(ResponseStatus.ERROR, code, message);
    }
}
