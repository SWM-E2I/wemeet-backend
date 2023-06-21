package com.e2i.wemeet.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Mbti {

  ESTJ, ESTP, ESFJ, ESFP, ENTJ, ENTP, ENFJ, ENFP,
  ISTJ, ISTP, ISFJ, ISFP, INTJ, INTP, INFJ, INFP,
  NOTHING;

  @JsonCreator
  public static Mbti findBy(String value) {

    for (Mbti mbti : Mbti.values()) {
      if (mbti.name().equals(value.toUpperCase())) {
        return mbti;
      }
    }

    return null;
  }
}
