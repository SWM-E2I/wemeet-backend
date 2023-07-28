package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.memberpreferencemeetingtype.MemberPreferenceMeetingType;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberPreferenceResponseDto(
    String drinkingOption,
    String sameCollegeState,
    Boolean isAvoidedFriends,
    List<MemberPreferenceMeetingType> preferenceMeetingTypeList,
    String startPreferenceAdmissionYear,
    String endPreferenceAdmissionYear,
    String preferenceMbti
) {

    public MemberPreferenceResponseDto(Member member,
        List<MemberPreferenceMeetingType> memberPreferenceMeetingTypeList) {
        this(member.getPreference().getDrinkingOption(),
            member.getPreference().getSameCollegeState(),
            member.getPreference().getIsAvoidedFriends(),
            memberPreferenceMeetingTypeList,
            member.getPreference().getStartPreferenceAdmissionYear(),
            member.getPreference().getEndPreferenceAdmissionYear(),
            member.getPreference().getPreferenceMbti());
    }
}
