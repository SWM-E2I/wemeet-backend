package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum Gender {

    MALE,
    FEMALE;

    @JsonCreator
    public static Gender findBy(String value) {

        return Arrays.stream(Gender.values())
            .filter(gender -> gender.name().equals(value.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_GENDER_VALUE));
    }
}
