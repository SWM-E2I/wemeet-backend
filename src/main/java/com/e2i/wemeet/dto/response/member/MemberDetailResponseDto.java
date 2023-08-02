package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.member.data.Gender;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.domain.profile_image.ProfileImage;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberDetailResponseDto(
    String nickname,
    Gender gender,
    Mbti mbti,
    String college,
    String collegeType,
    String admissionYear,
    String introduction,
    List<ProfileImage> profileImageList
) {

    public MemberDetailResponseDto(Member member, List<ProfileImage> profileImageList) {
        this(
            member.getNickname(), member.getGender(), member.getMbti(),
            member.getCollegeInfo().getCollege(),
            member.getCollegeInfo().getCollegeType(),
            member.getCollegeInfo().getAdmissionYear(), member.getIntroduction(), profileImageList
        );
    }
}
