package com.e2i.wemeet.domain.cost;

import lombok.Getter;

@Getter
public enum Earn {
    PAYMENT_5900("5900원 결제"),
    PAYMENT_9900("9900원 결제"),
    PAYMENT_14900("14900원 결제"),
    PAYMENT_19900("19900원 결제"),

    EVENT("이벤트"),
    ADVERTISEMENT("광고 시청"),
    ;

    private final String detail;

    Earn(String detail) {
        this.detail = detail;
    }

    public static String getTypeName() {
        return "EARN";
    }
}
