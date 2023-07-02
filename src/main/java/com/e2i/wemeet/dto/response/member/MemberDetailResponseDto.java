package com.e2i.wemeet.dto.response.member;

import com.e2i.wemeet.domain.member.Gender;
import com.e2i.wemeet.domain.memberinterest.MemberInterest;
import com.e2i.wemeet.domain.profileimage.ProfileImage;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberDetailResponseDto(
    String nickname,
    Gender gender,
    String college,
    String collegeType,
    int admissionYear,
    String introduction,
    List<ProfileImage> profileImageList,
    List<MemberInterest> memberInterestList
) {

}
