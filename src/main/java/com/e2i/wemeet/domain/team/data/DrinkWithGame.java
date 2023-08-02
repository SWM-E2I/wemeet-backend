package com.e2i.wemeet.domain.team.data;

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
}
