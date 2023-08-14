package com.e2i.wemeet.domain.team.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Region {
    HONGDAE(1, "홍대"),
    GANGNAM(2, "강남"),
    SINCHON(3, "신촌"),
    GUNDAE(4, "건대입구");

    private final int key;
    private final String name;

    Region(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static Region findByKey(int key) {
        return Arrays.stream(Region.values())
            .filter(region -> region.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }
}
