package com.e2i.wemeet.dto.request.member;

public record PreferenceRequestDto(
    String startPreferenceAdmissionYear,
    String endPreferenceAdmissionYear,
    String sameCollegeState,
    String drinkingOption,
    boolean isAvoidedFriends,
    String preferenceMbti) {

}
