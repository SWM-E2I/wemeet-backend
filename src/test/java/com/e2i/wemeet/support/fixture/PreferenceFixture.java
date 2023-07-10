package com.e2i.wemeet.support.fixture;

import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.dto.request.member.ModifyMemberPreferenceRequestDto;
import com.e2i.wemeet.dto.request.member.PreferenceRequestDto;
import java.util.List;

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

    public PreferenceRequestDto createPreferenceDto() {
        return PreferenceRequestDto.builder()
            .startPreferenceAdmissionYear(startPreferenceAdmissionYear)
            .endPreferenceAdmissionYear(endPreferenceAdmissionYear)
            .sameCollegeState(sameCollegeState)
            .drinkingOption(drinkingOption)
            .isAvoidedFriends(isAvoidedFriends)
            .preferenceMbti(preferenceMbti)
            .build();
    }

    public ModifyMemberPreferenceRequestDto createModifyMemberPreferenceDto() {
        return ModifyMemberPreferenceRequestDto.builder()
            .startPreferenceAdmissionYear(startPreferenceAdmissionYear)
            .endPreferenceAdmissionYear(endPreferenceAdmissionYear)
            .sameCollegeState(sameCollegeState)
            .drinkingOption(drinkingOption)
            .isAvoidedFriends(isAvoidedFriends)
            .preferenceMbti(preferenceMbti)
            .preferenceMeetingTypeList(List.of())
            .build();
    }
}
