package com.e2i.wemeet.domain.meeting.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;
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

    public static AcceptStatus findByKey(int key) {
        return Arrays.stream(AcceptStatus.values())
            .filter(acceptStatus -> acceptStatus.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }
}
