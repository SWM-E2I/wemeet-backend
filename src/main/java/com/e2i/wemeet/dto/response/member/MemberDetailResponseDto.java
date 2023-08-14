package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import lombok.Builder;

@Builder
public record MemberDetailResponseDto(
    String nickname,
    Gender gender,
    Mbti mbti,
    String college,
    String collegeType,
    String admissionYear,
    ProfileImageResponseDto profileImage
) {

    public static MemberDetailResponseDto of(final Member member, final String college) {
        ProfileImageResponseDto profileImage = ProfileImageResponseDto.of(member.getProfileImage());

        return MemberDetailResponseDto.builder()
            .nickname(member.getNickname())
            .gender(member.getGender())
            .mbti(member.getMbti())
            .college(college)
            .collegeType(member.getCollegeInfo().getCollegeType().getDescription())
            .admissionYear(member.getCollegeInfo().getAdmissionYear())
            .profileImage(profileImage)
            .build();
    }
}
