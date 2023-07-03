package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Preference;

public enum PreferenceFixture {

    GENERAL_PREFERENCE("19", "22", "0", "0", true, "ENTP");

    private final String startPreferenceAdmissionYear;

    private final String endPreferenceAdmissionYear;

    private final String sameCollegeState;

    private final String drinkingOption;

    private final boolean isAvoidedFriends;

    private final String preferenceMbti;

    PreferenceFixture(String startPreferenceAdmissionYear, String endPreferenceAdmissionYear,
        String sameCollegeState, String drinkingOption, boolean isAvoidedFriends,
        String preferenceMbti) {
        this.startPreferenceAdmissionYear = startPreferenceAdmissionYear;
        this.endPreferenceAdmissionYear = endPreferenceAdmissionYear;
        this.sameCollegeState = sameCollegeState;
        this.drinkingOption = drinkingOption;
        this.isAvoidedFriends = isAvoidedFriends;
        this.preferenceMbti = preferenceMbti;
    }

    public Preference create() {
        return Preference.builder()
            .startPreferenceAdmissionYear(startPreferenceAdmissionYear)
            .endPreferenceAdmissionYear(endPreferenceAdmissionYear)
            .sameCollegeState(sameCollegeState)
            .drinkingOption(drinkingOption)
            .isAvoidedFriends(isAvoidedFriends)
            .preferenceMbti(preferenceMbti)
            .build();
    }

}
