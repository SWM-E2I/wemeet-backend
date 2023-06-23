package com.e2i.wemeet.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Preference {

  private int startPreferenceAdmissionYear;

  private int endPreferenceAdmissionYear;

  private boolean sameCollegeState;

  private boolean drinkingOption;

  private boolean isAvoidedFriends;

  @Column(length = 8)
  private String preferenceMbti;

  @Builder
  public Preference(int startPreferenceAdmissionYear, int endPreferenceAdmissionYear,
      boolean sameCollegeState, boolean drinkingOption, boolean isAvoidedFriends,
      String preferenceMbti) {
    this.startPreferenceAdmissionYear = startPreferenceAdmissionYear;
    this.endPreferenceAdmissionYear = endPreferenceAdmissionYear;
    this.sameCollegeState = sameCollegeState;
    this.drinkingOption = drinkingOption;
    this.isAvoidedFriends = isAvoidedFriends;
    this.preferenceMbti = preferenceMbti;
  }
}
