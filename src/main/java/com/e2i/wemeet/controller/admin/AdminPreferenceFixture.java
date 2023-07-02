package com.e2i.wemeet.controller.admin;

import com.e2i.wemeet.domain.member.Preference;

public enum AdminPreferenceFixture {

    GENERAL_PREFERENCE(2019, 2022, true, true, true, "ENTP")
    ;

    private final int startPreferenceAdmissionYear;

    private final int endPreferenceAdmissionYear;

    private final boolean sameCollegeState;

    private final boolean drinkingOption;

    private final boolean isAvoidedFriends;

    private final String preferenceMbti;

    AdminPreferenceFixture(int startPreferenceAdmissionYear, int endPreferenceAdmissionYear,
        boolean sameCollegeState, boolean drinkingOption, boolean isAvoidedFriends,
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
