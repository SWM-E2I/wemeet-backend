package com.e2i.wemeet.service.cost;

import com.e2i.wemeet.domain.cost.Payment;

public record PaymentEvent(
    Payment type,
    Long memberId
) {

    public static PaymentEvent of(String type, Long memberId) {
        return new PaymentEvent(Payment.valueOf(type), memberId);
    }

    public static PaymentEvent of(Payment type, Long memberId) {
        return new PaymentEvent(type, memberId);
    }

}
