package com.e2i.wemeet.domain.cost;

import lombok.Getter;

@Getter
public enum Spent {

    DEFAULT("기본 값"),
    MEETING_REQUEST("미팅 요청"),
    MEETING_REQUEST_WITH_MESSAGE("쪽지와 함께 미팅 요청"),
    MEETING_ACCEPT("미팅 수락"),
    MORE_TEAM("팀 더보기");

    private final String detail;

    Spent(String detail) {
        this.detail = detail;
    }

    public static String getTypeName() {
        return "SPENT";
    }
}
