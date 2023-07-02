package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.member.Role;

public record CreateMemberRequestDto(
    String nickname,
    Gender gender,
    String phoneNumber,
    CollegeInfoRequestDto collegeInfo,
    PreferenceRequestDto preference,
    Mbti mbti
) {

    public Member toEntity(String memberCode) {
        return Member.builder()
            .memberCode(memberCode)
            .nickname(nickname)
            .gender(gender)
            .phoneNumber(phoneNumber)
            .collegeInfo(CollegeInfo.builder()
                .college(collegeInfo.college())
                .collegeType(collegeInfo.collegeType())
                .admissionYear(collegeInfo().admissionYear())
                .mail(collegeInfo.mail())
                .build())
            .preference(Preference.builder()
                .startPreferenceAdmissionYear(preference.startPreferenceAdmissionYear())
                .endPreferenceAdmissionYear(preference.endPreferenceAdmissionYear())
                .drinkingOption(preference.drinkingOption())
                .sameCollegeState(preference.sameCollegeState())
                .isAvoidedFriends(preference().isAvoidedFriends())
                .preferenceMbti(preference.preferenceMbti())
                .build())
            .mbti(mbti)
            .role(Role.USER)
            .build();
    }
}
