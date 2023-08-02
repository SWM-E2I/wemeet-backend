package com.e2i.wemeet.domain.team.data;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum AdditionalActivity {

    SHOW(1, "공연/전시"),
    SPORTS(2, "스포츠 관람"),
    UNIQUE_EXPERIENCE(3, "이색 활동"),
    OUTDOOR_ACTIVITY(4, "야외 활동"),
    CAFE(5, "카페/맛집");

    private final int key;
    private final String name;

    AdditionalActivity(int key, String name) {
        this.key = key;
        this.name = name;
    }

    @JsonCreator
    public static AdditionalActivity findBy(String value) {
        if (value == null) {
            return null;
        }

        return Arrays.stream(AdditionalActivity.values())
            .filter(additionalActivity -> additionalActivity.name().equals(value.toUpperCase()))
            .findFirst()
            .orElseThrow(
                () -> new InvalidValueException(ErrorCode.INVALID_ADDITIONAL_ACTIVITY_VALUE));
    }
}
