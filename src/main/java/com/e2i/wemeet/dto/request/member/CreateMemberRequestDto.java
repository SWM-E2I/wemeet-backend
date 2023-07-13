package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Preference;
import com.e2i.wemeet.domain.member.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateMemberRequestDto(
    @NotBlank(message = "{not.blank.nickname}")
    String nickname,

    @NotBlank(message = "{not.blank.gender}")
    String gender,

    @NotBlank(message = "{not.blank.phone.number}")
    @Pattern(regexp = "^\\+8210\\d{8}$", message = "{invalid.format.phone.number}")
    String phoneNumber,

    @Valid
    CollegeInfoRequestDto collegeInfo,

    @Valid
    PreferenceRequestDto preference,

    @NotNull(message = "{not.null.preference.meeting.type}")
    List<String> preferenceMeetingTypeList,

    @NotNull(message = "{not.null.mbti}")
    String mbti,

    @Nullable
    String introduction,

    @Nullable
    List<String> memberInterestList
) {

    public Member toMemberEntity(String memberCode) {
        return Member.builder()
            .memberCode(memberCode)
            .nickname(nickname)
            .gender(Gender.findBy(gender))
            .phoneNumber(phoneNumber)
            .collegeInfo(CollegeInfo.builder()
                .college(collegeInfo.college())
                .collegeType(collegeInfo.collegeType())
                .admissionYear(collegeInfo().admissionYear())
                .build())
            .preference(Preference.builder()
                .startPreferenceAdmissionYear(preference.startPreferenceAdmissionYear())
                .endPreferenceAdmissionYear(preference.endPreferenceAdmissionYear())
                .drinkingOption(preference.drinkingOption())
                .sameCollegeState(preference.sameCollegeState())
                .isAvoidedFriends(preference().isAvoidedFriends())
                .preferenceMbti(preference.preferenceMbti())
                .build())
            .mbti(Mbti.findBy(mbti))
            .introduction(introduction)
            .role(Role.USER)
            .build();
    }
}
