package com.e2i.wemeet.controller.admin;

public enum AdminPreferenceFixture {

    GENERAL_PREFERENCE("19", "22", "22", "22", true, "ENTP");

    private final String startPreferenceAdmissionYear;

    private final String endPreferenceAdmissionYear;

    private final String sameCollegeState;

    private final String drinkingOption;

    private final boolean isAvoidedFriends;

    private final String preferenceMbti;

    AdminPreferenceFixture(String startPreferenceAdmissionYear, String endPreferenceAdmissionYear,
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
