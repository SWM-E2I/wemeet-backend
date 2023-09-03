package com.e2i.wemeet.domain.cost;

import lombok.Getter;

@Getter
public enum Spent {

    DEFAULT(1),
    MEETING_REQUEST(10),
    MEETING_REQUEST_WITH_MESSAGE(12),
    MEETING_ACCEPT(5),
    MORE_TEAM(3);

    private final Integer value;

    Spent(Integer value) {
        this.value = value;
    }

    public static String getTypeName() {
        return "SPENT";
    }
}
