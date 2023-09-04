package com.e2i.wemeet.service.cost;

import com.e2i.wemeet.domain.cost.Spent;

public record SpendEvent(
    Spent type,
    Long memberId
) {

    public static SpendEvent of(String type, Long memberId) {
        return new SpendEvent(Spent.valueOf(type), memberId);
    }

    public static SpendEvent of(Spent type, Long memberId) {
        return new SpendEvent(type, memberId);
    }
}
