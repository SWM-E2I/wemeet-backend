package com.e2i.wemeet.domain.cost;

import lombok.Getter;

@Getter
public enum Earn {
    EVENT("이벤트"),
    ADVERTISEMENT("광고 시청");

    private final String detail;

    Earn(String detail) {
        this.detail = detail;
    }

    public static String getTypeName() {
        return "EARN";
    }
}
