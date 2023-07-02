package com.e2i.wemeet.dto.request.member;

import java.util.List;

public record ModifyMemberPreferenceRequestDto(
    String drinkingOption,
    String startPreferenceAdmissionYear,
    String endPreferenceAdmissionYear,
    String sameCollegeState,
    boolean isAvoidedFriends,
    String preferenceMbti,
    List<String> preferenceMeetingTypeList
) {

}
