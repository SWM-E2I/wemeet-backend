package com.e2i.wemeet.dto.dsl;

import com.e2i.wemeet.domain.member.data.CollegeType;
import com.e2i.wemeet.domain.member.data.Mbti;
import com.e2i.wemeet.dto.response.team.TeamMemberResponseDto;

public record TeamMemberInformationDto(
    String collegeName,
    CollegeType collegeType,
    String admissionYear,
    Mbti mbti
) {

    public TeamMemberResponseDto toTeamMemberResponseDto() {
        return TeamMemberResponseDto.builder()
            .college(this.collegeName)
            .collegeType(this.collegeType.name())
            .admissionYear(this.admissionYear)
            .mbti(this.mbti)
            .build();
    }
}
