package com.e2i.wemeet.dto.response;

public record ResponseDto(
    ResponseStatus status,
    String message,
    Object data
) {
}
