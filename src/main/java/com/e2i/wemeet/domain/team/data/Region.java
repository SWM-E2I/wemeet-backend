package com.e2i.wemeet.domain.team.data;

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
}
