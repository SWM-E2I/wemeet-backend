package com.e2i.wemeet.domain.team;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum AdditionalActivity {

    SHOW, SPORTS, UNIQUE_EXPERIENCE, OUTDOOR_ACTIVITY, CAFE;

    @JsonCreator
    public static AdditionalActivity findBy(String value) {

        return Arrays.stream(AdditionalActivity.values())
            .filter(additionalActivity -> additionalActivity.name().equals(value.toUpperCase()))
            .findFirst()
            .orElseThrow(
                () -> new InvalidValueException(ErrorCode.INVALID_ADDITIONAL_ACTIVITY_VALUE));
    }
}
