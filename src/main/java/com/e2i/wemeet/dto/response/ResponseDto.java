package com.e2i.wemeet.dto.response;

public record ResponseDto<T>(
    ResponseStatus status,
    String message,
    T data
) {

    public static <T> ResponseDto<T> success(final String message, final T data) {
        return new ResponseDto<>(ResponseStatus.SUCCESS, message, data);
    }

    public static <T> ResponseDto<T> success(final T data) {
        return new ResponseDto<>(ResponseStatus.SUCCESS, "Successful processing of request", data);
    }

    public static ResponseDto<Void> success(final String message) {
        return new ResponseDto<>(ResponseStatus.SUCCESS, message, null);
    }
}
