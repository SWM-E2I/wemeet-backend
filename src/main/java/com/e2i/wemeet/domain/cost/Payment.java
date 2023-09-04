package com.e2i.wemeet.domain.cost;

import lombok.Getter;

@Getter
public enum Payment {
    PAYMENT_5900("5900원 결제"),
    PAYMENT_9900("9900원 결제"),
    PAYMENT_14900("14900원 결제"),
    PAYMENT_19900("19900원 결제");

    private final String detail;

    Payment(String detail) {
        this.detail = detail;
    }

    public static String getTypeName() {
        return "PAYMENT";
    }
}
