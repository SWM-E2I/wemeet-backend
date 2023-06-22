package com.e2i.wemeet.domain.member;

import com.e2i.wemeet.exception.ErrorCode;
import com.e2i.wemeet.exception.badrequest.InvalidValueException;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum Mbti {

  ESTJ, ESTP, ESFJ, ESFP, ENTJ, ENTP, ENFJ, ENFP,
  ISTJ, ISTP, ISFJ, ISFP, INTJ, INTP, INFJ, INFP,
  NOTHING;

  @JsonCreator
  public static Mbti findBy(String value) {

    return Arrays.stream(Mbti.values())
        .filter(mbti -> mbti.name().equals(value.toUpperCase()))
        .findFirst()
        .orElseThrow(() -> new InvalidValueException(ErrorCode.INVALID_MBTI_VALUE));
  }
}
