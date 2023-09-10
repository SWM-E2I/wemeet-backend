package com.e2i.wemeet.service.meeting;

import com.e2i.wemeet.domain.cost.Spent;
import com.e2i.wemeet.service.cost.SpendEvent;
import com.e2i.wemeet.service.sns.SnsEvent;

public record MeetingEvent(
    SnsEvent snsEvent,
    SpendEvent spendEvent
) {

    public static MeetingEvent of(String receivePhoneNumber, String message, Spent type, Long memberId) {
        SnsEvent snsEvent = new SnsEvent(receivePhoneNumber, message);
        SpendEvent spendEvent = new SpendEvent(type, memberId);
        return new MeetingEvent(snsEvent, spendEvent);
    }

}
