package com.e2i.wemeet.dto.request.member;

public record PreferenceRequestDto(
    int startPreferenceAdmissionYear,
    int endPreferenceAdmissionYear,
    boolean sameCollegeState,
    boolean drinkingOption,
    boolean isAvoidedFriends,
    String preferenceMbti) {

}
