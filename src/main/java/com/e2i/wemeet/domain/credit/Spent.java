package com.e2i.wemeet.domain.credit;

import lombok.Getter;

@Getter
public enum Spent {
    MEETING_REQUEST(10),
    MEETING_ACCEPT(5);

    private final Integer value;

    Spent(Integer value) {
        this.value = value;
    }

    public static String getTypeName() {
        return "SPENT";
    }
}
