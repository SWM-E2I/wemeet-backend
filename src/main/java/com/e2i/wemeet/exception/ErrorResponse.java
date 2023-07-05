package com.e2i.wemeet.exception;

import com.e2i.wemeet.dto.response.ResponseStatus;

public record ErrorResponse(ResponseStatus status, int code, String message) {

}
