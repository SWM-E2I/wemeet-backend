package com.e2i.wemeet.config.log;

import lombok.Getter;

@Getter
public enum MdcKey {
    CORRELATION_ID("correlationId"),
    MEMBER_ID("memberId"),
    ;

    private final String key;

    MdcKey(String key) {
        this.key = key;
    }
}
