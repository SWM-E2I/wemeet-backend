package com.e2i.wemeet.dto.response;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    SUCCESS("success"),
    FAIL("fail"),
    ERROR("error");

    private final String status;

    ResponseStatus(String status) {
        this.status = status;
    }
}
