package com.e2i.wemeet.domain.meeting.data;

import lombok.Getter;

@Getter
public enum AcceptStatus {
    PENDING(0),
    ACCEPT(1),
    REJECT(2),
    EXPIRED(3);

    private final int key;

    AcceptStatus(int key) {
        this.key = key;
    }
}
