package com.e2i.wemeet.dto.request.member;

import com.e2i.wemeet.domain.member.CollegeInfo;
import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "{not.null.mbti}")
    String mbti,

    @Nullable
    String introduction

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
            .mbti(Mbti.findBy(mbti))
            .introduction(introduction)
            .role(Role.USER)
            .credit(5)
            .build();
    }
}
