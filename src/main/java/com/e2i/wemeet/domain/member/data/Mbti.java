package com.e2i.wemeet.domain.member.data;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidDatabaseKeyToEnumException;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Mbti {

    XXXX(0),
    ESTJ(1), ESTP(2), ESFJ(3), ESFP(4), ENTJ(5), ENTP(6), ENFJ(7), ENFP(8),
    ISTJ(9), ISTP(10), ISFJ(11), ISFP(12), INTJ(13), INTP(14), INFJ(15), INFP(16);

    private final int key;

    Mbti(int key) {
        this.key = key;
    }

    @JsonCreator
    public static Mbti findBy(String value) {

        return Arrays.stream(Mbti.values())
            .filter(mbti -> mbti.name().equals(value.toUpperCase()))
            .findFirst()
            .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_MBTI_VALUE));
    }

    public static Mbti findByKey(int key) {
        return Arrays.stream(Mbti.values())
            .filter(mbti -> mbti.getKey() == key)
            .findFirst()
            .orElseThrow(InvalidDatabaseKeyToEnumException::new);
    }
}
