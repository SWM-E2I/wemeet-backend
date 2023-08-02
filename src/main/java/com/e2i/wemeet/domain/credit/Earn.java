package com.e2i.wemeet.domain.credit;

import lombok.Getter;

@Getter
public enum Earn {
    PAYMENT,
    EVENT,
    ADVERTISEMENT;

    public static String getTypeName() {
        return "EARN";
    }
}
