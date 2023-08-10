package com.e2i.wemeet.dto.response.team;

import com.e2i.wemeet.domain.member.data.CollegeInfo;
import com.e2i.wemeet.domain.member.data.Mbti;
import lombok.Builder;

@Builder
public record TeamMemberResponseDto(
    String college,
    String collegeType,
    String admissionYear,
    Mbti mbti
) {

    public static TeamMemberResponseDto of(CollegeInfo collegeInfo, Mbti mbti
    ) {
        return TeamMemberResponseDto.builder()
            .college(collegeInfo.getCollegeCode().getCodeValue())
            .collegeType(collegeInfo.getCollegeType().getDescription())
            .admissionYear(collegeInfo.getAdmissionYear())
            .mbti(mbti)
            .build();
    }
}
