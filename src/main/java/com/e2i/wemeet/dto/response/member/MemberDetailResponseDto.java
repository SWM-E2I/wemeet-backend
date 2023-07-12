package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.member.Mbti;
import com.e2i.wemeet.domain.member.Member;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
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
    List<ProfileImage> profileImageList,
    List<MemberInterest> memberInterestList
) {

    public MemberDetailResponseDto(Member member, List<ProfileImage> profileImageList,
        List<MemberInterest> memberInterestList) {
        this(
            member.getNickname(), member.getGender(), member.getMbti(),
            member.getCollegeInfo().getCollege(),
            member.getCollegeInfo().getCollegeType(),
            member.getCollegeInfo().getAdmissionYear(), member.getIntroduction(), profileImageList,
            memberInterestList
        );
    }
}
