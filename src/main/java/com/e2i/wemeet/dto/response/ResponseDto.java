package com.e2i.wemeet.dto.response;

public record ResponseDto<T>(
    ResponseStatus status,
    String message,
    T data
) {

}
