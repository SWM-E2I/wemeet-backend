package com.e2i.wemeet.domain.team.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;

public enum DrinkRate {

    ZERO(0, "술 없이도 즐거워"),
    LOW(1, "술은 기분 좋을 정도로만"),
    MIDDLE(2, "술 좋아해"),
    HIGH(3, "술에 진심이야");

    private final int key;
    private final String name;

    DrinkRate(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static DrinkRate findByKey(int key) {
        return Arrays.stream(DrinkRate.values())
            .filter(drinkRate -> drinkRate.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }

    public int getKey() {
        return key;
    }
}
