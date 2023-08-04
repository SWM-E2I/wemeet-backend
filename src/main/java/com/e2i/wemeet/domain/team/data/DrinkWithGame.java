package com.e2i.wemeet.domain.team.data;

import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import java.util.Arrays;

public enum DrinkWithGame {
    ANY(0, "상관 없어"),
    MASTER(1, "나는야 술게임 고수"),
    BEGINNER(2, "술게임 잘몰라"),
    HATER(3, "술게임 싫어");

    private final int key;
    private final String name;

    DrinkWithGame(int key, String name) {
        this.key = key;
        this.name = name;
    }

    public static DrinkWithGame findByKey(int key) {
        return Arrays.stream(DrinkWithGame.values())
            .filter(drinkWithGame -> drinkWithGame.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }

    public int getKey() {
        return key;
    }
}
