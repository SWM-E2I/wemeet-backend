package com.e2i.wemeet.exception;

public record ErrorResponse(String status, int code, String message) {

    public ErrorResponse(int code, String message) {
        this("ERROR", code, message);
    }
}
