package com.e2i.wemeet.domain.member;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
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

  @Embedded
  private PreferenceMbti preferenceMbti;

  @Builder
  public Preference(int startPreferenceAdmissionYear, int endPreferenceAdmissionYear,
      boolean sameCollegeState, boolean drinkingOption, boolean isAvoidedFriends,
      PreferenceMbti preferenceMbti) {
    this.startPreferenceAdmissionYear = startPreferenceAdmissionYear;
    this.endPreferenceAdmissionYear = endPreferenceAdmissionYear;
    this.sameCollegeState = sameCollegeState;
    this.drinkingOption = drinkingOption;
    this.isAvoidedFriends = isAvoidedFriends;
    this.preferenceMbti = preferenceMbti;
  }
}
