package com.e2i.wemeet.service.cost;

import com.e2i.wemeet.domain.cost.Earn;

public record EarnEvent(
    Earn type,
    int value,
    Long memberId
) {

    public static EarnEvent of(String type, int value, Long memberId) {
        return new EarnEvent(Earn.valueOf(type), value, memberId);
    }

    public static EarnEvent of(Earn type, int value, Long memberId) {
        return new EarnEvent(type, value, memberId);
    }
}
